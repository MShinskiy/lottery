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
    @Enumerated(EnumType.STRING)
    @Column(name = "user_level")
    private Level level;

    // Валюты
    private long fireworks; // фейерверки
    private long mandarins; // мандарины
    private long tickets; // билеты

    // Привязанные сущности
    @OneToMany(mappedBy = "entryBy", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<MarkerLog> markers = new ArrayList<>(); // логирование подтверждения
    @OneToMany(mappedBy = "openedBy", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<LotteryLog> lotteries = new ArrayList<>(); // логирование участия в лотерее

    // Создать пользователя из DTO
    public static User fromDto(UserDto dto) {
        User user = new User();
        user.dtprf = dto.dtprf();
        user.group = Group.valueOf(dto.group());
        user.level = Level.valueOf(dto.level());
        user.fireworks = dto.fireworks();
        user.mandarins = dto.mandarins();
        user.tickets = dto.tickets();
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
        return user;
    }
}
