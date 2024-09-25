package com.lavkatech.lottery.reporting.dto;

import lombok.Getter;

@Getter
public class MaxBalanceImportDto extends ImportDto {

    private final long maxFireworks;
    private final long maxMandarins;

    public MaxBalanceImportDto(String dtprf, long maxFireworks, long maxMandarins) {
        super(dtprf);
        this.maxFireworks = maxFireworks;
        this.maxMandarins = maxMandarins;
    }
}
