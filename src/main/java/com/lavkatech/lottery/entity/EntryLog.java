package com.lavkatech.lottery.entity;

import com.lavkatech.lottery.reporting.dto.EntryExportDto;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "history_entries")
@NoArgsConstructor
public class EntryLog extends Log{

    public EntryLog(User entryBy) {
        super(entryBy);
    }

    @Override
    public EntryExportDto toDto() {
        return new EntryExportDto(
                getEntryBy().getDtprf(),
                getEntryOn().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                getEntryOn().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                getEntryOn().format(DateTimeFormatter.ofPattern("yyyyy.MM.dd HH:mm:ss.SSSSSS"))
        );
    }
}
