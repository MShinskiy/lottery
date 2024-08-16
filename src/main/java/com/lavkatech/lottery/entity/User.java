package com.lavkatech.lottery.entity;

import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

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
    private Group group;
    @Column(name = "user_level")
    private Level level;

    // Валюты
    private long fireworks; // фейерверки
    private long mandarins; // мандарины
    private long tickets; // билеты

    // Привязанные сущности
    @OneToMany(mappedBy = "entryBy", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<MarkerLog> markers; // логирование подтверждения
    @OneToMany(mappedBy = "openedBy", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<LotteryLog> lotteries; // логирование участия в лотерее
}
