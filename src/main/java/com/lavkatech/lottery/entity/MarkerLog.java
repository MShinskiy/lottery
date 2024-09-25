package com.lavkatech.lottery.entity;

import com.lavkatech.lottery.reporting.dto.ChallengeExportDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@Entity
@Table(name = "history_markers")
@NoArgsConstructor
public class MarkerLog extends Log{

    public MarkerLog(String marker, User entryBy) {
        super(entryBy);
        this.marker = marker;
    }

    // Маркер подтверждения
    private String marker;

    @Override
    public ChallengeExportDto toDto() {
        return new ChallengeExportDto(
                getEntryBy().getDtprf(),
                getEntryOn().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                getEntryOn().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                marker,
                getEntryOn().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSSSSS"))
        );
    }
}
