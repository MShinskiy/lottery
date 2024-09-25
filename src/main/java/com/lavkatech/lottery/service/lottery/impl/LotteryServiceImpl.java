package com.lavkatech.lottery.service.lottery.impl;

import com.lavkatech.lottery.entity.Lottery;
import com.lavkatech.lottery.entity.dto.LotteryResult;
import com.lavkatech.lottery.repository.LotteryRepository;
import com.lavkatech.lottery.service.lottery.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
public class LotteryServiceImpl implements LotteryService {

    private final LotteryRepository lotteryRepo;

    @Autowired
    public LotteryServiceImpl(LotteryRepository lotteryRepo) {
        this.lotteryRepo = lotteryRepo;
    }


    @Retryable(retryFor = SQLException.class, // в случае ошибки
            maxAttempts = 5, // Макс. кол-во попыток
            backoff = @Backoff(delay = 100L) // промежуток времени между попытками
    )
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public LotteryResult getLotteryResult() throws SQLException {
        Lottery l = getLotteryEntity();
        l.incrementOrder();
        long order = l.getCurrentOrder();
        lotteryRepo.save(l);
        long value = calcWinning.apply(order);
        return new LotteryResult(order, value);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public Lottery getLotteryEntity() {
        return lotteryRepo.findAll().stream().findFirst()
                .orElseGet(Lottery::new);
    }
}
