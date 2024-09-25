package com.lavkatech.lottery.service.db.impl;

import com.lavkatech.lottery.entity.LotteryLog;
import com.lavkatech.lottery.entity.MarkerLog;
import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.entity.EntryLog;
import com.lavkatech.lottery.reporting.dto.BalanceExportDto;
import com.lavkatech.lottery.reporting.dto.ChallengeExportDto;
import com.lavkatech.lottery.reporting.dto.EntryExportDto;
import com.lavkatech.lottery.reporting.dto.LotteryExportDto;
import com.lavkatech.lottery.reporting.enumeration.Month;
import com.lavkatech.lottery.repository.EntryLogRepository;
import com.lavkatech.lottery.repository.LotteryLogRepository;
import com.lavkatech.lottery.repository.MarkerLogRepository;
import com.lavkatech.lottery.service.db.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    private final LotteryLogRepository lotteryLogRepo;
    private final MarkerLogRepository markerLogRepo;
    private final EntryLogRepository entryLogRepo;

    @Autowired
    public LogServiceImpl(LotteryLogRepository lotteryLogRepo, MarkerLogRepository markerLogRepo, EntryLogRepository entryLogRepo) {
        this.lotteryLogRepo = lotteryLogRepo;
        this.markerLogRepo = markerLogRepo;
        this.entryLogRepo = entryLogRepo;
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
    public void createEntryLog(User user) {
        EntryLog log = new EntryLog(user);
        entryLogRepo.save(log);
    }

    @Override
    public List<ChallengeExportDto> createChallengeReport(Month month) {
        List<MarkerLog> history = markerLogRepo.findAllByEntryOnAfterAndEntryOnBefore(month.first(), month.last());
        return history.stream()
                .map(MarkerLog::toDto)
                .toList();
    }

    @Override
    public List<LotteryExportDto> createLotteryReport(Month month) {
        List<LotteryLog> history = lotteryLogRepo.findAllByEntryOnAfterAndEntryOnBefore(month.first(), month.last());
        return history.stream()
                .map(LotteryLog::toDto)
                .toList();
    }

    @Override
    public List<EntryExportDto> createEntryReport(Month month) {
        List<EntryLog> history = entryLogRepo.findAllByEntryOnAfterAndEntryOnBefore(month.first(), month.last());
        return history.stream()
                .map(EntryLog::toDto)
                .toList();
    }

    @Override
    public List<BalanceExportDto> createBalanceLog(Month month) {
        return lotteryLogRepo.findWinningsForUsersForPeriod(month.first(), month.last());
    }

    @Override
    public String getLastUserMarker(User user) {
        MarkerLog log = markerLogRepo.findTopByEntryByOrderByEntryOnDesc(user).orElse(null);
        if (log == null)
            return null;
        return log.getMarker();
    }
}
