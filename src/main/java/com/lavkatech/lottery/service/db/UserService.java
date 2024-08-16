package com.lavkatech.lottery.service.db;

import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import com.lavkatech.lottery.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User loadUser(String dtprf) throws UserNotFoundException;
    List<User> loadGroup(Group group, Level level);
}