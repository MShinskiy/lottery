package com.lavkatech.lottery.reporting.enumeration;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum Month {
    JAN("Январь", 1,31),
    FEB("Февраль", 2, 28),
    MAR("Март", 3, 31),
    APR("Апрель", 4, 30),
    MAY("Май", 5, 31),
    JUN("Июнь", 6, 30),
    JUL("Июль", 7, 31),
    AUG("Август", 8, 31),
    SEP("Сентябрь", 9, 30),
    OCT("Октябрь", 10, 31),
    NOV("Ноябрь", 11, 30),
    DEC("Декабрь", 12, 31);

    private final String fullname;
    private final int nDays;
    private final int monthOfYear;

    Month(String fullname, int monthOfYear, int nDays) {
        this.fullname = fullname;
        this.monthOfYear = monthOfYear;
        this.nDays = nDays;
    }

    public String fullname() {
        return fullname;
    }

    /**
     * Возвращает первое число месяца
     * @return число типа LocalDateTime
     */
    public LocalDateTime first() {
        int year = LocalDateTime.now().getYear();
        return LocalDateTime.of(year, monthOfYear, 1, 0, 0);
    }

    /**
     * Возвращает последнее число месяца
     * @return число типа LocalDateTime
     */
    public LocalDateTime last() {
        int year = LocalDateTime.now().getYear();
        boolean isLeap = LocalDate.now().isLeapYear();
        if (this == FEB && isLeap)
            return LocalDateTime.of(year, monthOfYear, 29, 23, 59);
        return LocalDateTime.of(year, monthOfYear, nDays, 23, 59);
    }

    public java.time.Month toJavaTimeMonth() {
        return java.time.Month.of(monthOfYear);
    }
}
