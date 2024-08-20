package com.lavkatech.lottery.contoller;

import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.entity.dto.DecodedUserQuery;
import com.lavkatech.lottery.entity.dto.LotteryResult;
import com.lavkatech.lottery.entity.dto.UserInfo;
import com.lavkatech.lottery.exception.UserNotFoundException;
import com.lavkatech.lottery.service.LotteryService;
import com.lavkatech.lottery.service.db.LogService;
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
    private final LotteryService lotteryService;
    private final LogService logService;

    @Autowired
    public ApiController(UserService userService, LotteryService lotteryService, LogService logService) {
        this.userService = userService;
        this.lotteryService = lotteryService;
        this.logService = logService;
    }

    @PostMapping("/lotteries")
    public ResponseEntity<Object> processLotteryResult(@RequestHeader("Token") String token) {
        // Получить токен пользователя
        DecodedUserQuery duq = CipherUtility.encodedStringToPojo(token, initVector, key);
        if(duq == null)
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();

        // Использовать билет
        User user;
        try {
            user = userService.loadUser(duq.getDtprf());
            userService.useTicket(duq.getDtprf());
            log.debug("User[dtprf={}] uses a ticket.", duq.getDtprf());
        } catch (UserNotFoundException e) {
            log.debug(e.getMessage());
            // Пользователь не найден
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            // Нет билетов
            log.debug(e.getMessage());
            return ResponseEntity.noContent().build();
        }

        // Получить выигрыш
        LotteryResult res = lotteryService.getLotteryResult();
        logService.createLotteryLog(user, res.value(), res.order());
        return ResponseEntity
                .ok(res);
    }

    @GetMapping("/users/{dtprf}")
    public ResponseEntity<Object> getUserInfo(@RequestHeader("Token") String token, @PathVariable String dtprf) {
        // Получить токен пользователя
        DecodedUserQuery duq = CipherUtility.encodedStringToPojo(token, initVector, key);
        if(duq == null || !Objects.equals(dtprf, duq.getDtprf()))
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        try {
            // Загрузить информацию о пользователе
            User user = userService.loadUser(dtprf);
            UserInfo body = UserInfo.createFrom(user);
            return ResponseEntity
                    .ok(body);
        } catch (UserNotFoundException e) {
            // Пользователь не найден
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}
