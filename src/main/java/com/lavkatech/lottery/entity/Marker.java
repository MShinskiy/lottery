package com.lavkatech.lottery.entity;

import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "markers")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Marker {

    public Marker(String marker, LocalDate expiringOn, Group group, Level level) {
        this.marker = marker;
        this.expiringOn = expiringOn;
        this.userGroup = group;
        this.userLevel = level;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Маркер подтверждения на момент создания
    private String marker;
    //Дата истечения
    private LocalDate expiringOn;
    // Группа пользователей
    @Enumerated(EnumType.STRING)
    private Group userGroup;
    @Enumerated(EnumType.STRING)
    private Level userLevel;
    // Метка создание
    @CreatedDate
    private LocalDateTime createdOn;
}
