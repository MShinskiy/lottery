package com.lavkatech.lottery.controller;

import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.entity.dto.DecodedUserQuery;
import com.lavkatech.lottery.entity.dto.LotteryResult;
import com.lavkatech.lottery.entity.dto.UserDto;
import com.lavkatech.lottery.exception.UserNotFoundException;
import com.lavkatech.lottery.service.db.UserService;
import com.lavkatech.lottery.util.CipherUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final static Logger log = LogManager.getLogger();

    @Value("${cipher.initVector}")
    private String initVector;
    @Value("${cipher.key}")
    private String key;

    private final UserService userService;

    @Autowired
    public ApiController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Обработать результат лотереи
     * @param token зашифрованная query пользователя
     * @return результат лотереи
     */
    @PostMapping("/lotteries")
    public ResponseEntity<Object> processLotteryResult(@RequestHeader("Token") String token) {
        // Получить токен пользователя
        DecodedUserQuery duq = CipherUtility.encodedStringToPojo(token, initVector, key);
        if(duq == null)
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();

        // Использовать билет
        //User user;
        try {
            // Загрузить пользователя
            //user = userService.loadUser(duq.dtprf());

            LotteryResult res = userService.useTicket(duq.dtprf());
            log.debug("User[dtprf={}] uses a ticket.", duq.dtprf());
            return ResponseEntity
                    .ok(res);
        } catch (UserNotFoundException e) {
            // Пользователь не найден
            log.debug(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            // Нет билетов
            log.debug(e.getMessage());
            return ResponseEntity.noContent().build();
        }
        //logService.createLotteryLog(user, res.value(), res.order());
    }

    /**
     * Получить информацию о пользователе
     * @param token зашифрованная query пользователя
     * @param dtprf id пользователя
     * @return информация о пользователе
     */
    @GetMapping("/users/{dtprf}")
    public ResponseEntity<Object> getUserInfo(@RequestHeader("Token") String token, @PathVariable String dtprf) {
        // Проверить токен пользователя
        if(isValidToken(dtprf, token))
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        try {
            // Загрузить информацию о пользователе
            User user = userService.loadUser(dtprf);
            UserDto body = UserDto.createFrom(user);
            return ResponseEntity
                    .ok(body);
        } catch (UserNotFoundException e) {
            // Пользователь не найден
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @PutMapping("/users/{dtprf}/markers")
    public ResponseEntity<Object> updateCurrentMarker(@RequestHeader("Token") String token, @PathVariable String dtprf) {
        // Получить токен пользователя
        DecodedUserQuery duq = CipherUtility.encodedStringToPojo(token, initVector, key);
        if(duq == null || !Objects.equals(dtprf, duq.dtprf()))
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        try {
            //User user = userService.loadUser(duq.dtprf());
            userService.acceptChallenge(duq.dtprf());
            return ResponseEntity
                    .ok().build();
        } catch (UserNotFoundException e) {
            // Пользователь не найден
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @GetMapping("/users/{dtprf}/markers")
    public ResponseEntity<Object> getMarkers(@RequestParam("filter") String filterOption, @PathVariable String dtprf, @RequestHeader("Token") String token) {
        // Проверить токен
        if(isValidToken(dtprf, token))
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        // !!! don't support all markers retrieval
        if(!filterOption.equals("confirmed"))
            return ResponseEntity
                    .noContent()
                    .build();
        try {
            boolean challengeAccepted = userService.isChallengeAccepted(dtprf);
            // Вернуть результат
            return ResponseEntity
                    .ok(challengeAccepted);
        } catch (UserNotFoundException e) {
            // Мало вероятно, применимо к api доступу
            return ResponseEntity
                    .notFound()
                    .build();
        }

    }

    private boolean isValidToken(String dtprf, String token) {
        DecodedUserQuery duq = CipherUtility.encodedStringToPojo(token, initVector, key);
        return duq == null || !Objects.equals(dtprf, duq.dtprf());
    }
}
