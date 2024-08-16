package com.lavkatech.lottery.service.db.impl;

import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import com.lavkatech.lottery.exception.UserNotFoundException;
import com.lavkatech.lottery.repository.UserRepository;
import com.lavkatech.lottery.service.db.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Autowired
    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User loadUser(String dtprf) throws UserNotFoundException {
        return userRepo.findById(dtprf).orElseThrow(() -> new  UserNotFoundException("User[dtprf=" + dtprf + "] was not found!"));
    }

    @Override
    public List<User> loadGroup(Group group, Level level) {
        return userRepo.findByGroupAndLevel(group, level);
    }

}
