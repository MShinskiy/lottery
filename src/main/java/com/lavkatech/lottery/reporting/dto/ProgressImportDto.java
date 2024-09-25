package com.lavkatech.lottery.reporting.dto;

import lombok.Getter;

@Getter
public class ProgressImportDto extends ImportDto {

    private final String progressString;

    public ProgressImportDto(String dtprf, String progressString) {
        super(dtprf);
        this.progressString = progressString;
    }

    public ProgressImportDto(String dtprf, int val1, int val2) {
        super(dtprf);
        this.progressString = val1 + "/" + val2;
    }
}
