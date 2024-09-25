package com.lavkatech.lottery.entity;

import com.lavkatech.lottery.reporting.dto.ExportDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class Log {

    public Log(User entryBy) {
        this.entryBy = entryBy;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Чей лог
    @ManyToOne
    private User entryBy;
    // Когда создан лог
    @CreatedDate
    private LocalDateTime entryOn;

    abstract ExportDto toDto();
}
