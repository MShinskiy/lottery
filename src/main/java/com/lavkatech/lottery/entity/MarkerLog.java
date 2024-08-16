package com.lavkatech.lottery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "markers")
@NoArgsConstructor
public class MarkerLog {

    public MarkerLog(String marker, User entryBy) {
        this.marker = marker;
        this.entryBy = entryBy;
        this.entryOn = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Маркер подтверждения
    private String marker;

    // Кто вошел
    @ManyToOne
    private User entryBy;
    // Когда вошел
    private LocalDateTime entryOn;
}
