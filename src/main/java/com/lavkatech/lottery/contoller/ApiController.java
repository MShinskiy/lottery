package com.lavkatech.lottery.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/lotteries")
    public ResponseEntity<Object> getLotteryResult(@RequestParam String token) {
        // TODO
        return null;
    }

    @GetMapping("/users/{dtprf}")
    public ResponseEntity<Object> getUserInfo(@RequestParam String token, @PathVariable String dtprf) {
        // TODO
        return null;
    }
}
