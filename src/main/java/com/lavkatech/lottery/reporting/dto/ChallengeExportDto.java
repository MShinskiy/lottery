package com.lavkatech.lottery.reporting.dto;

import lombok.Getter;

@Getter
public class ChallengeExportDto extends ExportDto{
    private final String dtprf;
    private final String date;
    private final String time;
    private final String marker;
    private final String timestamp;

    public ChallengeExportDto(String dtprf, String date, String time, String marker, String timestamp) {
        this.dtprf = dtprf;
        this.date = date;
        this.time = time;
        this.marker = marker;
        this.timestamp = timestamp;
    }
}
