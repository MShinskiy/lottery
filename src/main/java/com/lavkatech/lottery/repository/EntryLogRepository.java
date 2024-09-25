package com.lavkatech.lottery.repository;

import com.lavkatech.lottery.entity.EntryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EntryLogRepository extends JpaRepository<EntryLog, UUID> {
    List<EntryLog> findAllByEntryOnAfterAndEntryOnBefore(LocalDateTime from, LocalDateTime to);
}
