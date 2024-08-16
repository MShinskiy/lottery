package com.lavkatech.lottery.service.impl;

import com.lavkatech.lottery.entity.Lottery;
import com.lavkatech.lottery.repository.LotteryRepository;
import com.lavkatech.lottery.service.LotteryService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LotteryServiceImpl implements LotteryService {

    private final LotteryRepository lotteryRepo;
    private Lottery lottery;

    @Autowired
    public LotteryServiceImpl(LotteryRepository lotteryRepo) {
        this.lotteryRepo = lotteryRepo;
    }

    @PostConstruct
    private void afterInit() {
        lottery = lotteryRepo.findFirst()
                .orElseGet(Lottery::new);
    }

    @PreDestroy
    private void beforeDestroy() {
        lotteryRepo.save(lottery);
    }

    @Override
    public long getValue() {
        return calcWinning.apply(
                lottery.getCurrentOrder()
                        .getAndIncrement()
        );
    }
}
