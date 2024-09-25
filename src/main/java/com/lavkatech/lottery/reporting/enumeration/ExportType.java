package com.lavkatech.lottery.reporting.enumeration;

public enum ExportType {
    CHALLENGE("Подтверждение участия"),
    ENTRIES("Входы"),
    LOTTERY("Список участников"),
    BALANCE("Баланс");

    private String value;

    ExportType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
