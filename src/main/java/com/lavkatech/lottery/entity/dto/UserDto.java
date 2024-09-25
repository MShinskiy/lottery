package com.lavkatech.lottery.entity.dto;

import com.lavkatech.lottery.entity.User;

public record UserDto(
        String dtprf,
        String group,
        String level,
        long fireworks,
        long fireworksMax,
        long mandarins,
        long mandarinsMax,
        long tickets
) {

    public static UserDto createFrom(User user) {
        return new UserDto(
                user.getDtprf(),
                user.getGroup().name(),
                user.getLevel().name(),
                user.getFireworks(),
                user.getFireworksMax(),
                user.getMandarins(),
                user.getMandarinsMax(),
                user.getTickets()
        );
    }
}
