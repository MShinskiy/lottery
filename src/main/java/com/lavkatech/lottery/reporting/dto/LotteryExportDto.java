package com.lavkatech.lottery.reporting.dto;

import lombok.Getter;

@Getter
public class LotteryExportDto extends ExportDto {
    private final String dtprf;
    private final Long order;
    private final String date;
    private final String time;
    private final String millis;
    private final Long value;
    private final String timestamp;

    public LotteryExportDto(String dtprf, Long order, String date, String time, String millis, Long value, String timestamp) {
        this.dtprf = dtprf;
        this.order = order;
        this.date = date;
        this.time = time;
        this.millis = millis;
        this.value = value;
        this.timestamp = timestamp;
    }
}
