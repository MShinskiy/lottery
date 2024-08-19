package com.lavkatech.lottery.entity.dto;

import com.lavkatech.lottery.entity.User;

public record UserInfo(String dtprf, long fireworks, long mandarins, long tickets) {

    public static UserInfo createFrom(User user) {
        return new UserInfo(user.getDtprf(), user.getFireworks(), user.getMandarins(), user.getTickets());
    }
}
