package com.lavkatech.lottery.reporting.service;

import com.lavkatech.lottery.reporting.dto.ImportDto;
import com.lavkatech.lottery.reporting.enumeration.Month;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public interface ReportService {
    // Импорт
    List<? extends ImportDto> doBalanceImport(File file) throws IOException;
    List<? extends ImportDto> doMaxBalanceImport(File file) throws IOException;
    List<? extends ImportDto> doProgressImport(File file) throws IOException;
    // Экспорт
    File doChallengeExport(Month month) throws IOException;
    File doBalanceExport(Month month) throws IOException;
    File doLotteryExport(Month month) throws IOException;
    File doEntriesExport(Month month) throws IOException;
}
