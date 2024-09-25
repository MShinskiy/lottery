package com.lavkatech.lottery.reporting.dto;

import lombok.Getter;

@Getter
public class EntryExportDto extends ExportDto{
    private final String dtprf;
    private final String date;
    private final String time;
    private final String timestamp;

    public EntryExportDto(String dtprf, String date, String time, String timestamp) {
        this.dtprf = dtprf;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
    }
}
