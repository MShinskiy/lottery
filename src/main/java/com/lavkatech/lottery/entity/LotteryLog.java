package com.lavkatech.lottery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "history_lotteries")
@NoArgsConstructor
public class LotteryLog {

    public LotteryLog(long order, long value, User openedBy) {
        this.order = order;
        this.value = value;
        this.openedBy = openedBy;
        this.openedOn = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Порядковый номер билета
    @Column(name = "lottery_order")
    private long order;
    // Значение билета
    @Column(name = "lottery_value")
    private long value;

    // Кто открыл
    @ManyToOne
    private User openedBy;
    // Когда открыл
    private LocalDateTime openedOn;
}
