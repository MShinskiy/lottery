package com.lavkatech.lottery.entity;

import com.lavkatech.lottery.reporting.dto.LotteryExportDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@Entity
@Table(name = "history_lotteries")
@NoArgsConstructor
public class LotteryLog extends Log{

    public LotteryLog(long order, long value, User entryBy) {
        super(entryBy);
        this.order = order;
        this.value = value;
    }

    // Порядковый номер билета
    @Column(name = "lottery_order")
    private long order;
    // Значение билета
    @Column(name = "lottery_value")
    private long value;

    @Override
    public LotteryExportDto toDto() {
        return new LotteryExportDto(
                getEntryBy().getDtprf(),
                order,
                getEntryOn().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                getEntryOn().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                getEntryOn().format(DateTimeFormatter.ofPattern("SSSS")),
                value,
                getEntryOn().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSSSSS"))
        );
    }
}
