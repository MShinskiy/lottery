package com.lavkatech.lottery.reporting.service.impl;

import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.reporting.dto.BalanceImportDto;
import com.lavkatech.lottery.reporting.dto.ImportDto;
import com.lavkatech.lottery.reporting.dto.MaxBalanceImportDto;
import com.lavkatech.lottery.reporting.dto.ProgressImportDto;
import com.lavkatech.lottery.reporting.service.ImportService;
import com.lavkatech.lottery.service.db.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImportServiceImpl implements ImportService {

    private final static Logger log = LogManager.getLogger();

    private UserService userService;

    // Circular dependency
    @Autowired
    public void setUserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public int importUserBalance(Map<String, List<ImportDto>> lines) {
        int count = 0;
        long timeToLoad, timeToSave;
        //Замер времени для отладки
        long startLoad = System.nanoTime();
        //Загрузка всех пользователей в работе
        List<User> users = userService.getOrCreateAllUsers(new ArrayList<>(lines.keySet()));
        long endLoad = System.nanoTime();
        timeToLoad = endLoad - startLoad;
        log.debug("Total load time of {} user(s) during coin import: {} ms", users.size(), timeToLoad/1000000);

        //Построение таблицы соответствий
        Map<String, User> lookup = users.stream().collect(Collectors.toMap(User::getDtprf, Function.identity()));
        //Импорт
        for(Map.Entry<String, List<ImportDto>> userLines : lines.entrySet()) {
            User user = lookup.get(userLines.getKey());
            if(null == user) continue;
            for(ImportDto line : userLines.getValue()) {
                //Начисление баланса.
                //Конвертирование
                BalanceImportDto dto = (BalanceImportDto) line;
                //Переопределить баланс пользователя
                user.setFireworks(dto.getFireworks());
                user.setMandarins(dto.getMandarins());
                user.setTickets(dto.getTickets());

                count++;
            }
        }
        //Обновление значений
        long startSave = System.nanoTime();
        userService.saveUsers(users);
        long endSave = System.nanoTime();
        timeToSave = endSave - startSave;
        log.debug("Total save time of {} users during coin import: {} ms", users.size(), timeToSave/1000000);
        return count;
    }

    @Override
    public int importUserMaxBalance(Map<String, List<ImportDto>> lines) {
        int count = 0;
        long timeToLoad, timeToSave;
        //Замер времени для отладки
        long startLoad = System.nanoTime();
        //Загрузка всех пользователей в работе
        List<User> users = userService.getOrCreateAllUsers(new ArrayList<>(lines.keySet()));
        long endLoad = System.nanoTime();
        timeToLoad = endLoad - startLoad;
        log.debug("Total load time of {} user(s) during coin import: {} ms", users.size(), timeToLoad/1000000);

        //Построение таблицы соответствий
        Map<String, User> lookup = users.stream().collect(Collectors.toMap(User::getDtprf, Function.identity()));
        //Импорт
        for(Map.Entry<String, List<ImportDto>> userLines : lines.entrySet()) {
            User user = lookup.get(userLines.getKey());
            if(null == user) continue;
            for(ImportDto line : userLines.getValue()) {
                //Начисление максимального баланса.
                //Конвертирование
                MaxBalanceImportDto dto = (MaxBalanceImportDto) line;
                //Переопределить баланс пользователя
                user.setFireworksMax(dto.getMaxFireworks());
                user.setMandarinsMax(dto.getMaxMandarins());

                count++;
            }
        }
        //Обновление значений
        long startSave = System.nanoTime();
        userService.saveUsers(users);
        long endSave = System.nanoTime();
        timeToSave = endSave - startSave;
        log.debug("Total save time of {} users during coin import: {} ms", users.size(), timeToSave/1000000);
        return count;
    }

    @Override
    public int importUserProgress(Map<String, List<ImportDto>> lines) {
        int count = 0;
        long timeToLoad, timeToSave;
        //Замер времени для отладки
        long startLoad = System.nanoTime();
        //Загрузка всех пользователей в работе
        List<User> users = userService.getOrCreateAllUsers(new ArrayList<>(lines.keySet()));
        long endLoad = System.nanoTime();
        timeToLoad = endLoad - startLoad;
        log.debug("Total load time of {} user(s) during coin import: {} ms", users.size(), timeToLoad/1000000);

        //Построение таблицы соответствий
        Map<String, User> lookup = users.stream().collect(Collectors.toMap(User::getDtprf, Function.identity()));
        //Импорт
        for(Map.Entry<String, List<ImportDto>> userLines : lines.entrySet()) {
            User user = lookup.get(userLines.getKey());
            if(null == user) continue;
            for(ImportDto line : userLines.getValue()) {
                //Начисление максимального баланса.
                //Конвертирование
                ProgressImportDto dto = (ProgressImportDto) line;
                //Переопределить прогресс пользователя
                user.setProgressString(dto.getProgressString());

                count++;
            }
        }
        //Обновление значений
        long startSave = System.nanoTime();
        userService.saveUsers(users);
        long endSave = System.nanoTime();
        timeToSave = endSave - startSave;
        log.debug("Total save time of {} users during coin import: {} ms", users.size(), timeToSave/1000000);
        return count;
    }
}
