package com.lavkatech.lottery.entity.enumeration;

import lombok.Getter;

@Getter
public enum Level {
    HIGH("Высокий"),
    LOW("Низкий");

    private final String name;

    Level(String name) {
        this.name = name;
    }
}
