package com.lavkatech.lottery.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.entity.dto.DecodedUserQuery;
import com.lavkatech.lottery.entity.dto.FrontDto;
import com.lavkatech.lottery.entity.dto.HouseDto;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import com.lavkatech.lottery.exception.UserNotFoundException;
import com.lavkatech.lottery.service.db.UserService;
import com.lavkatech.lottery.util.CipherUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class UserMvcController {
    private final static Logger log = LogManager.getLogger();

    private final UserService userService;

    @Value("${cipher.initVector}")
    private String initVector;
    @Value("${cipher.key}")
    private String key;

    @Autowired
    public UserMvcController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/home")
    public String enterLotteryPage(@RequestParam("query") String token, @RequestParam("house") String houseDtoJson, Model model) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        DecodedUserQuery decodedQuery = CipherUtility.encodedStringToPojo(token, initVector, key);
        if(decodedQuery == null)
            return "error";

        try {
            HouseDto houseDto = mapper.readerFor(HouseDto.class).readValue(houseDtoJson);

            // Не допускать отсутствие группы и уровня
            Group group;
            try {
                group = decodedQuery.group() == null ? Group.OTHER : decodedQuery.group();
            } catch (IllegalArgumentException e) {
                group = Group.OTHER;
            }

            Level level;
            try {
                level = decodedQuery.level() == null ? Level.LOW : decodedQuery.level();
            } catch (IllegalArgumentException e) {
                level = Level.LOW;
            }

            User user = userService.getOrCreateUser(decodedQuery.dtprf(), group, level);
            Boolean isChallengeAccepted = userService.isChallengeAccepted(decodedQuery.dtprf());
            LocalDate expOn = userService.getChallengeExpiringDate(decodedQuery.dtprf());
            String dateString = expOn.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            userService.logEntry(user);
            FrontDto frontDto = FrontDto.create(user, houseDto, token, isChallengeAccepted, dateString);
            String json = mapper.writeValueAsString(frontDto);
            model.addAttribute("json", json);
        } catch (UserNotFoundException e) {
            return "error";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return "index";
    }
}
