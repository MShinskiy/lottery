package com.lavkatech.lottery.service.db;

import com.lavkatech.lottery.entity.Marker;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface MarkerService {
    /**
     * Получить текущий маркер
     * @param group группа
     * @param level уровень
     * @return маркер
     */
    Marker getCurrentMarker(Group group, Level level);

    /**
     * Получить все текущие маркеры
     * @return маркер
     */
    List<Marker> getAllCurrentMarker();

    /**
     * Обновить текущий маркер. Создает новую запись в таблице маркеров.
     * @param group группа
     * @param level уровень
     * @param marker маркер
     */
    void updateCurrentMarker(Marker marker, LocalDate expiringOn, Group group, Level level);

    /**
     * Обновить текущий маркер. Создает новую запись в таблице маркеров.
     * @param group группа
     * @param level уровень
     * @param marker маркер
     */
    void updateCurrentMarker(String marker, LocalDate expiringOn, Group group, Level level);
}
