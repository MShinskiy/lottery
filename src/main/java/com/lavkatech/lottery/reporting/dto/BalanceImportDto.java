package com.lavkatech.lottery.reporting.dto;

import lombok.Getter;

@Getter
public class BalanceImportDto extends ImportDto {
    private final long fireworks;
    private final long mandarins;
    private final long tickets;

    public BalanceImportDto(String dtprf, long fireworks, long mandarins, long tickets) {
        super(dtprf);
        this.fireworks = fireworks;
        this.mandarins = mandarins;
        this.tickets = tickets;
    }
}
