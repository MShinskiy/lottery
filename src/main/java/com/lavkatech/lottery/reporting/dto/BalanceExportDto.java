package com.lavkatech.lottery.reporting.dto;

import lombok.Getter;

@Getter
public class BalanceExportDto extends ExportDto {
    private final String dtprf;
    private final String winnings;
    private final String openedTickets;

    public BalanceExportDto(String dtprf, String winnings, String openedTickets) {
        this.dtprf = dtprf;
        this.winnings = winnings;
        this.openedTickets = openedTickets;
    }

    // JPQL Constructor
    public BalanceExportDto(String dtprf, Long winnings, Long openedTickets) {
        this.dtprf = dtprf;
        this.winnings = String.valueOf(winnings);
        this.openedTickets = String.valueOf(openedTickets);
    }


}
