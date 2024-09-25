package com.lavkatech.lottery.repository;

import com.lavkatech.lottery.entity.MarkerLog;
import com.lavkatech.lottery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarkerLogRepository extends JpaRepository<MarkerLog, UUID> {

    Optional<MarkerLog> findTopByEntryByOrderByEntryOnDesc(User user);
    List<MarkerLog> findAllByEntryOnAfterAndEntryOnBefore(LocalDateTime from, LocalDateTime to);
}
