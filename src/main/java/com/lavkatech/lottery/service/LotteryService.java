package com.lavkatech.lottery.service;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public interface LotteryService {
    Function<Long, Long> calcWinning = (order) -> {
        // switch is not allowed to be used
        // with long in java....
        if (order % 1000 == 0)
            return 10000L;
        if(order % 100 == 0)
            return 5000L;
        if(order % 50 == 0)
            return 2000L;
        if(order % 5 == 0)
            return 500L;
        return 0L;
    };

    long getLotteryResult();
}
