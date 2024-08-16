package com.lavkatech.lottery.service.db.impl;

import com.lavkatech.lottery.entity.LotteryLog;
import com.lavkatech.lottery.entity.MarkerLog;
import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.repository.LotteryLogRepository;
import com.lavkatech.lottery.repository.MarkerLogRepository;
import com.lavkatech.lottery.service.db.LogService;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    private final LotteryLogRepository lotteryLogRepo;
    private final MarkerLogRepository markerLogRepo;

    public LogServiceImpl(LotteryLogRepository lotteryLogRepo, MarkerLogRepository markerLogRepo) {
        this.lotteryLogRepo = lotteryLogRepo;
        this.markerLogRepo = markerLogRepo;
    }

    @Override
    public void createMarkerLog(User user, String marker) {
        MarkerLog log = new MarkerLog(marker, user);
        markerLogRepo.save(log);
    }

    @Override
    public void createLotteryLog(User user, long value, long order) {
        LotteryLog log = new LotteryLog(order, value, user);
        lotteryLogRepo.save(log);
    }

    @Override
    public String getLastUserMarker(User user) {
        MarkerLog log = markerLogRepo.findTopByEntryByOrderByEntryOnDesc(user).orElse(null);
        if(log == null)
            return null;
        return log.getMarker();
    }
}
