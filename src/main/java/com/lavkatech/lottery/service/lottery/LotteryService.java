package com.lavkatech.lottery.service.lottery;

import com.lavkatech.lottery.entity.Lottery;
import com.lavkatech.lottery.entity.dto.LotteryResult;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.function.Function;

@Service
public interface LotteryService {
    Function<Long, Long> calcWinning = (order) -> {
        // switch is not allowed to be used
        // with long in java....
        if(order % 2025 == 0)
            return 2025L;
        if (order % 1000 == 0)
            return 5000L;
        if(order % 100 == 0)
            return 1000L;
        if(order % 50 == 0)
            return 500L;
        if(order % 5 == 0)
            return 100L;
        return 0L;
    };

    LotteryResult getLotteryResult() throws SQLException;

    Lottery getLotteryEntity();
}
