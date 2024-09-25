package com.lavkatech.lottery.service.db.impl;

import com.lavkatech.lottery.entity.EntryLog;
import com.lavkatech.lottery.entity.LotteryLog;
import com.lavkatech.lottery.entity.MarkerLog;
import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.reporting.dto.BalanceImportDto;
import com.lavkatech.lottery.reporting.dto.ImportDto;
import com.lavkatech.lottery.entity.dto.LotteryResult;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import com.lavkatech.lottery.exception.UserNotFoundException;
import com.lavkatech.lottery.reporting.dto.MaxBalanceImportDto;
import com.lavkatech.lottery.reporting.dto.ProgressImportDto;
import com.lavkatech.lottery.reporting.service.ImportService;
import com.lavkatech.lottery.repository.UserRepository;
import com.lavkatech.lottery.service.db.MarkerService;
import com.lavkatech.lottery.service.db.UserService;
import com.lavkatech.lottery.service.lottery.LotteryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger log = LogManager.getLogger();

    // repos
    private final UserRepository userRepo;
    // services
    private final LotteryService lotteryService;
    private final MarkerService markerService;
    private final ImportService importService;

    @Autowired
    public UserServiceImpl(UserRepository userRepo, MarkerService markerService, LotteryService lotteryService, ImportService importService) {
        this.userRepo = userRepo;
        this.markerService = markerService;
        this.lotteryService = lotteryService;
        this.importService = importService;
    }

    @Override
    public void importUserData(List<? extends ImportDto> lines) {
        if (lines == null || lines.isEmpty()) {
            log.error("No importing lines or received list is null.");
            return;
        }
        long startTime = System.nanoTime(); // Start time measurement

        Class<? extends ImportDto> targetClass = lines.get(0).getClass();
        Map<String, List<ImportDto>> lookup = organizeImportLines(lines);
        int count = 0;
        if (targetClass.equals(BalanceImportDto.class))
            count = importService.importUserBalance(lookup);
        else if (targetClass.equals(MaxBalanceImportDto.class))
            count = importService.importUserMaxBalance(lookup);
        else if (targetClass.equals(ProgressImportDto.class))
            count = importService.importUserProgress(lookup);
        else
            //Не нормальное течение импорта
            log.error("Not supported import type for class {}", targetClass);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        if (count == lines.size())
            log.info("All {} lines successfully imported for {} user(s).", count, lookup.size());
        else
            log.info("{}/{} lines imported for {} user(s).", count, lines.size(), lookup.size());
        log.debug("Import execution time of {} lines: {} ms", count, duration / 1000000);
    }

    @Override
    public User getOrCreateUser(String dtprf, Group group, Level level) {
        try {
            // Существующий пользователь
            User user = loadUser(dtprf);
            // Значения переданные с основного модуля Успехоград приоритетные
            boolean saveRequired = false;
            if(user.getLevel() != level) {
                user.setLevel(level);
                saveRequired = true;
            }

            if(user.getGroup() != group) {
                user.setGroup(group);
                saveRequired = true;
            }

            if(saveRequired)
                user = userRepo.save(user);

            return user;
        } catch (UserNotFoundException e) {
            // Создать и сохранить пользователя
            return userRepo.save(User.newUser(dtprf, group, level));
        }
    }

    @Override
    public List<User> getOrCreateAllUsers(List<String> dtprfs) {
        //Замер времени
        long startLoad = System.nanoTime();
        List<User> users = userRepo.findAllById(dtprfs);
        //Получить новых пользователей
        List<String> notExistingUsers = dtprfs.stream().filter(dtprf -> !containsUserWithDtprf(users, dtprf)).toList();
        long endLoad = System.nanoTime();
        long timeToLoad = endLoad - startLoad;
        log.debug("Total load time of {} user(s) during level-group import: {} ms", users.size(), timeToLoad / 1000000);

        long startCreate = System.nanoTime();
        //Создать и соединить пользователей в один массив
        List<User> parsedNewUsers = notExistingUsers.stream()
                .map(User::newUser)
                .toList();
        List<User> newUsers = userRepo.saveAllAndFlush(parsedNewUsers);
        long endCreate = System.nanoTime();
        long timeToCreate = endCreate - startCreate;
        log.debug("Total creation time of {} user(s) during level-group import: {} ms", newUsers.size(), timeToCreate / 1000000);
        users.addAll(newUsers);
        return users;
    }

    public boolean containsUserWithDtprf(List<User> searchList, String dtprf) {
        for (User user : searchList)
            if (user.getDtprf().equals(dtprf))
                return true;
        return false;
    }

    public Map<String, List<ImportDto>> organizeImportLines(List<? extends ImportDto> importLines) {
        long startTime = System.nanoTime(); // Start time measurement
        Map<String, List<ImportDto>> lines = importLines.stream()
                .collect(Collectors.groupingBy(ImportDto::getDtprf, HashMap::new, Collectors.toList()));
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        log.debug("Total aggregation time of {} lines: {} ms", importLines.size(), duration / 1000000);

        return lines;
    }

    @Override
    public User loadUser(String dtprf) throws UserNotFoundException {
        return userRepo.findById(dtprf).orElseThrow(() -> new UserNotFoundException("User[dtprf=" + dtprf + "] was not found!"));
    }

    @Override
    public List<User> loadGroup(Group group, Level level) {
        return userRepo.findByGroupAndLevel(group, level);
    }

    @Override
    public LotteryResult useTicket(String dtprf) throws UserNotFoundException, IllegalArgumentException {
        return useTicket(loadUser(dtprf));
    }

    @Override
    public LotteryResult useTicket(User user) throws UserNotFoundException, IllegalArgumentException {
        // Получить пользователя
        if (user.getTickets() < 1)
            throw new IllegalArgumentException("No tickets available for user[dtprf=" + user.getDtprf() + "].");
        // Получить результат лотереи в одной транзакции
        try {
            LotteryResult result = lotteryService.getLotteryResult();
            // в случае успеха списать билет
            user.setTickets(user.getTickets() - 1);
            user.addLotteryLog(new LotteryLog(result.order(), result.value(), user));
            userRepo.save(user);
            return result;
        } catch (SQLException e) {
            // Если попытки не удались
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public void saveUsers(Collection<? extends User> users) {
        userRepo.saveAllAndFlush(users);
    }

    @Override
    public void acceptChallenge(String dtprf) throws UserNotFoundException {
        User user = loadUser(dtprf);
        String currentMarker = markerService.getCurrentMarker(user.getGroup(), user.getLevel()).getMarker();
        user.addMarkerLog(new MarkerLog(currentMarker, user));
        //logService.createMarkerLog(user, currentMarker);
        userRepo.save(user);
    }

    @Override
    public Boolean isChallengeAccepted(String dtprf) throws UserNotFoundException {
        User user = loadUser(dtprf);
        try {
            // Получить текущий маркер системы
            String currentMarker = markerService.getCurrentMarker(user.getGroup(), user.getLevel()).getMarker();
            // Получить последний маркер пользователя
            String lastMarker = user.getMarkers().stream()
                    .min((m1, m2) -> m2.getEntryOn().compareTo(m1.getEntryOn()))
                    .orElseThrow(() -> new NullPointerException("No markers for user[dtprf=" + dtprf + "]."))
                    .getMarker();
            // Сравнить
            return lastMarker.equals(currentMarker);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public LocalDate getChallengeExpiringDate(String dtprf) throws UserNotFoundException {
        User user = loadUser(dtprf);
        return markerService.getCurrentMarker(user.getGroup(), user.getLevel()).getExpiringOn();
    }

    @Override
    public void logEntry(User user) {
        user.addEntryLog(new EntryLog(user));
        userRepo.save(user);
    }

    @Override
    public void logEntry(String dtprf) {
        try {
            User user = loadUser(dtprf);
            logEntry(user);
        } catch (UserNotFoundException ignore) {

        }
    }

}
