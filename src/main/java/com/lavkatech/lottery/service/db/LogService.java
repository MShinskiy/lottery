package com.lavkatech.lottery.service.db;

import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.reporting.dto.BalanceExportDto;
import com.lavkatech.lottery.reporting.dto.ChallengeExportDto;
import com.lavkatech.lottery.reporting.dto.EntryExportDto;
import com.lavkatech.lottery.reporting.dto.LotteryExportDto;
import com.lavkatech.lottery.reporting.enumeration.Month;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LogService {
    // логирование
    /**
     * Сохранение нового лога маркера
     * @param user пользователь для которого создается лог
     * @param marker новый маркер для пользователя
     */
    void createMarkerLog(User user, String marker);

    /**
     * Сохранение нового лога лотереи
     * @param user пользователь для которого создается лог
     * @param value сумма выигрыша
     * @param order номер билета
     */
    void createLotteryLog(User user, long value, long order);

    /**
     * Сохранение нового лога входа пользователя
     * @param user пользователь для которого создается лог
     */
    void createEntryLog(User user);

    // получение логов
    String getLastUserMarker(User user);

    /**
     * Создание отчета принятия вызова (обновления маркера пользователя)
     * @param month месяц для отчета
     * @return отчет
     */
    List<ChallengeExportDto> createChallengeReport(Month month);

    /**
     * Создание отчета результатов лотереи
     * @param month месяц для отчета
     * @return отчет
     */
    List<LotteryExportDto> createLotteryReport(Month month);

    /**
     * Создание отчета входов пользователь
     * @param month месяц для отчета
     * @return отчет
     */
    List<EntryExportDto> createEntryReport(Month month);

    /**
     * Создание отчета баланса пользователь
     * @param month месяц для отчета
     * @return отчет
     */
    List<BalanceExportDto> createBalanceLog(Month month);

}
