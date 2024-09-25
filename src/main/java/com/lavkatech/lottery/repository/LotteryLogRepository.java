package com.lavkatech.lottery.repository;

import com.lavkatech.lottery.entity.LotteryLog;
import com.lavkatech.lottery.reporting.dto.BalanceExportDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LotteryLogRepository extends JpaRepository<LotteryLog, UUID> {

    @Query("""
    SELECT new com.lavkatech.lottery.reporting.dto.BalanceExportDto(ll.entryBy.dtprf, SUM(ll.value), COUNT(ll.value))
    FROM LotteryLog ll
    WHERE ll.entryOn > :from AND ll.entryOn < :to
    GROUP BY ll.entryBy
    ORDER BY SUM(ll.value) DESC
    """)
    List<BalanceExportDto> findWinningsForUsersForPeriod(LocalDateTime from, LocalDateTime to);

    List<LotteryLog> findAllByEntryOnAfterAndEntryOnBefore(LocalDateTime from, LocalDateTime to);
}
