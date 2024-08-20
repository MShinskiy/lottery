package com.lavkatech.lottery.service.impl;

import com.lavkatech.lottery.entity.Lottery;
import com.lavkatech.lottery.entity.dto.LotteryResult;
import com.lavkatech.lottery.repository.LotteryRepository;
import com.lavkatech.lottery.service.LotteryService;
import com.lavkatech.lottery.service.db.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LotteryServiceImpl implements LotteryService {

    private final LotteryRepository lotteryRepo;
    private final LogService logService;
    //private Lottery lottery;

    @Autowired
    public LotteryServiceImpl(LotteryRepository lotteryRepo, LogService logService) {
        this.lotteryRepo = lotteryRepo;
        this.logService = logService;
    }

/*    @PostConstruct
    private void afterInit() {
        lottery = lotteryRepo.findAll()
                .stream()
                .findFirst()
                .orElseGet(Lottery::new);
    }

    @PreDestroy
    private void beforeDestroy() {
        lotteryRepo.save(lottery);
    }*/

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public LotteryResult getLotteryResult() {
        Lottery l = getLotteryEntity();
        long order = l.getCurrentOrder();
        l.incrementOrder();
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
