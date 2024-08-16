package com.lavkatech.lottery.service.db;

import com.lavkatech.lottery.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface LogService {
    // логирование
    void createMarkerLog(User user, String marker);
    void createLotteryLog(User user, long value, long order);

    // получение логов
    String getLastUserMarker(User user);
}
