package com.lavkatech.lottery.entity;

import com.lavkatech.lottery.entity.dto.DecodedUserQuery;
import com.lavkatech.lottery.entity.dto.UserDto;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @Setter(AccessLevel.NONE)
    private String dtprf;

    // Группа пользователей
    @Column(name = "user_group")
    @Enumerated(EnumType.STRING)
    private Group group;
    @Column(name = "user_level")
    @Enumerated(EnumType.STRING)
    private Level level;

    // Валюты
    private long fireworks; // фейерверки
    private long fireworksMax;
    private long mandarins; // мандарины
    private long mandarinsMax;
    private long tickets; // билеты

    // строка прогресса
    private String progressString;

    // Привязанные сущности
    @OneToMany(mappedBy = "entryBy", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<MarkerLog> markers = new ArrayList<>(); // логирование подтверждения
    @OneToMany(mappedBy = "entryBy", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<LotteryLog> lotteries = new ArrayList<>(); // логирование участия в лотерее
    @OneToMany(mappedBy = "entryBy", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<EntryLog> entries = new ArrayList<>();

    public void addMarkerLog(MarkerLog log) {
        markers.add(log);
    }

    public void addLotteryLog(LotteryLog log) {
        lotteries.add(log);
    }

    public void addEntryLog(EntryLog log) {
        entries.add(log);
    }

    public static User newUser(String dtprf) {
        User user = new User();
        user.dtprf = dtprf;
        user.group = null;
        user.level = null;
        user.fireworks = 0;
        user.fireworksMax = 0;
        user.mandarins = 0;
        user.mandarinsMax = 0;
        user.tickets = 0;
        user.progressString = "";
        return user;
    }

    public static User newUser(String dtprf, Group group, Level level) {
        User user = newUser(dtprf);
        user.setGroup(group);
        user.setLevel(level);
        return user;
    }

    // Создать пользователя из DTO
    public static User fromDto(UserDto dto) {
        User user = new User();
        user.dtprf = dto.dtprf();
        user.group = Group.valueOf(dto.group());
        user.level = Level.valueOf(dto.level());
        user.fireworks = dto.fireworks();
        user.mandarins = dto.mandarins();
        user.tickets = dto.tickets();
        user.progressString = "";
        return user;
    }

    // Создать пользователя из запроса
    public static User fromQuery(DecodedUserQuery query) {
        User user = new User();
        user.dtprf = query.dtprf();
        user.group = query.group();
        user.level = query.level();
        user.fireworks = 0;
        user.mandarins = 0;
        user.tickets = 0;
        user.progressString = "";
        return user;
    }
}
