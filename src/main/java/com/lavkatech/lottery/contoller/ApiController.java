package com.lavkatech.lottery.contoller;

import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.entity.dto.DecodedUserQuery;
import com.lavkatech.lottery.entity.dto.LotteryInfo;
import com.lavkatech.lottery.entity.dto.UserInfo;
import com.lavkatech.lottery.exception.UserNotFoundException;
import com.lavkatech.lottery.service.LotteryService;
import com.lavkatech.lottery.service.db.UserService;
import com.lavkatech.lottery.util.CipherUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Value("${cipher.initVector}")
    private String initVector;
    @Value("${cipher.key}")
    private String key;

    private final UserService userService;
    private final LotteryService lotteryService;

    @Autowired
    public ApiController(UserService userService, LotteryService lotteryService) {
        this.userService = userService;
        this.lotteryService = lotteryService;
    }

    @PostMapping("/lotteries")
    public ResponseEntity<Object> processLotteryResult(@RequestHeader("Token") String token) {
        DecodedUserQuery duq = CipherUtility.encodedStringToPojo(token, initVector, key);
        if(duq == null)
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();

        long res = lotteryService.getLotteryResult();
        return ResponseEntity
                .ok(new LotteryInfo(res));
    }

    @GetMapping("/users/{dtprf}")
    public ResponseEntity<Object> getUserInfo(@RequestHeader("Token") String token, @PathVariable String dtprf) {
        DecodedUserQuery duq = CipherUtility.encodedStringToPojo(token, initVector, key);
        if(duq == null || !Objects.equals(dtprf, duq.getDtprf()))
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        try {
            User user = userService.loadUser(dtprf);
            UserInfo body = UserInfo.createFrom(user);
            return ResponseEntity
                    .ok(body);
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}
