package com.lavkatech.lottery.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Entity
@Table(name = "lottery")
public class Lottery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "total_winnings")
    private long totalWinnings;
    @Column(name = "order", columnDefinition = "BIGINT")
    private AtomicLong currentOrder;
}
