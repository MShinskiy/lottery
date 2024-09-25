package com.lavkatech.lottery.reporting.enumeration;

public enum ImportType {
    BALANCE("Баланс пользователя"),
    MAX_BALANCE("Цели пользователя"),
    PROGRESS("Прогресс пользователя");

    private final String value;

    public String value() {
        return value;
    }

    ImportType(String value) {
        this.value = value;
    }
}
