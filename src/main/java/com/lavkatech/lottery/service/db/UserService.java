package com.lavkatech.lottery.service.db;

import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.entity.dto.LotteryResult;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import com.lavkatech.lottery.exception.UserNotFoundException;
import com.lavkatech.lottery.reporting.dto.ImportDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public interface UserService {
    // import routines
    void importUserData(List<? extends ImportDto> lines);
    User getOrCreateUser(String dtprf, Group group, Level level);
    List<User> getOrCreateAllUsers(List<String> dtprfs);
    // user routines
    User loadUser(String dtprf) throws UserNotFoundException;
    List<User> loadGroup(Group group, Level level);
    LotteryResult useTicket(String dtprf) throws UserNotFoundException, IllegalArgumentException;
    LotteryResult useTicket(User user) throws UserNotFoundException, IllegalArgumentException;
    void saveUsers(Collection<? extends User> users);
    void acceptChallenge(String dtprf) throws UserNotFoundException;
    Boolean isChallengeAccepted(String dtprf) throws UserNotFoundException;
    LocalDate getChallengeExpiringDate(String dtprf) throws UserNotFoundException;
    //log routines
    void logEntry(User user);
    void logEntry(String dtprf);
}