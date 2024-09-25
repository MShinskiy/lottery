package com.lavkatech.lottery.entity.dto;

import com.lavkatech.lottery.entity.LotteryLog;
import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import lombok.Builder;

import java.util.Comparator;
import java.util.List;

@Builder
public record FrontDto(String dtprf,
                       String query,
                       Group group,
                       Level level,
                       Long ticketsAvail,
                       Long ticketsTotal,
                       Long mandarins,
                       Long mandarinsMax,
                       Long fireworks,
                       Long fireworksMax,
                       Boolean challengeAccepted,
                       String markerExpiringOn,
                       String progressString,
                       List<LotteryResult> history,
                       HouseDto house
) {

    public static FrontDto create(User user, HouseDto dto, String query, Boolean challengeAccepted, String markerExpiringOn) {
        return FrontDto.builder()
                .dtprf(user.getDtprf())
                .query(query)
                .group(user.getGroup())
                .level(user.getLevel())
                .ticketsAvail(user.getTickets())
                .ticketsTotal((long) user.getLotteries().size() + user.getTickets())
                .mandarins(user.getMandarins())
                .mandarinsMax(user.getMandarinsMax())
                .fireworks(user.getFireworks())
                .fireworksMax(user.getFireworksMax())
                .challengeAccepted(challengeAccepted)
                .progressString(user.getProgressString())
                .markerExpiringOn(markerExpiringOn)
                .history(user.getLotteries()
                        .stream()
                        .sorted(Comparator.comparingLong(LotteryLog::getOrder))
                        .map(ll -> new LotteryResult(ll.getOrder(), ll.getValue()))
                        .toList()
                )
                .house(dto)
                .build();
    }

}
