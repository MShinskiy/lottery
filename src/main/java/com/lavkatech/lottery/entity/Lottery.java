package com.lavkatech.lottery.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

@Data
@Entity
@Table(name = "lottery")
public class Lottery {

    public Lottery() {
        this.totalWinnings = 0;
        this.currentOrder = new AtomicLong(1);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "total_winnings")
    private long totalWinnings;
    @Column(name = "order", columnDefinition = "BIGINT")
    private AtomicLong currentOrder;
}
