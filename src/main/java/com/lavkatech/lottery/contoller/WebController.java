package com.lavkatech.lottery.contoller;

import com.lavkatech.lottery.entity.dto.DecodedUserQuery;
import com.lavkatech.lottery.util.CipherUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    private final static Logger log = LogManager.getLogger();

    @Value("${cipher.initVector}")
    private String initVector;
    @Value("${cipher.key}")
    private String key;

    @GetMapping("/home")
    public String enterLotteryPage(@RequestParam String query, @RequestParam(required = false) String marker) {
        DecodedUserQuery decodedQuery = CipherUtility.encodedStringToPojo(query, initVector, key);
        return "index";
    }


}
