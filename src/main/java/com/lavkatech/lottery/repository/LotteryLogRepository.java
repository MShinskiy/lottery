package com.lavkatech.lottery.repository;

import com.lavkatech.lottery.entity.LotteryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LotteryLogRepository extends JpaRepository<LotteryLog, UUID> {
}
