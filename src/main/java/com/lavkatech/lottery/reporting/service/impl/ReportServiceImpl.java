package com.lavkatech.lottery.reporting.service.impl;

import com.lavkatech.lottery.reporting.dto.*;
import com.lavkatech.lottery.reporting.enumeration.Month;
import com.lavkatech.lottery.reporting.service.ReportService;
import com.lavkatech.lottery.service.db.LogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    private final static String tempPath = System.getProperty("java.io.tmpdir") + File.separator;
    private final static Logger log = LogManager.getLogger();
    private final LogService logService;

    public ReportServiceImpl(LogService logService) {
        this.logService = logService;
    }

    @Override
    public List<? extends ImportDto> doBalanceImport(File file) throws IOException {
        List<BalanceImportDto> balanceData = new ArrayList<>();
        int count = 0;
        try(FileInputStream fis = new FileInputStream(file)) {
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;

                //0 -> dtprf, 1 -> фейерверки, 2 -> мандарины, 3 -> билеты
                String dtprf = "";
                Integer fireworks = null;
                Integer mandarins = null;
                Integer tickets = null;
                for (Cell cell : row)
                    switch (cell.getColumnIndex()) {
                        case 0 -> dtprf = tryGetStringCellValue(cell);
                        case 1 -> fireworks = tryGetNumericCellValue(cell);
                        case 2 -> mandarins = tryGetNumericCellValue(cell);
                        case 3 -> tickets = tryGetNumericCellValue(cell);
                    }

                count++;

                if(dtprf == null || dtprf.isEmpty())
                    log.error("ImportService row {} was skipped because service failed to parse dtprf (dtrpf={})", row.getRowNum(), dtprf);
                else if(fireworks == null)
                    log.error("ImportService row {} was skipped because service failed to parse mandarins amount (dtrpf={})", row.getRowNum(), dtprf);
                else if(mandarins == null)
                    log.error("ImportService row {} was skipped because service failed to parse fireworks amount (dtrpf={})", row.getRowNum(), dtprf);
                else if(tickets == null)
                    log.error("ImportService row {} was skipped because service failed to parse tickets amount (dtrpf={})", row.getRowNum(), dtprf);
                else
                    balanceData.add(new BalanceImportDto(dtprf, fireworks, mandarins, tickets));
            }
            log.info("{} rows processed, {} rows sent to import", count, balanceData.size());
        }
        return balanceData;
    }

    @Override
    public List<? extends ImportDto> doMaxBalanceImport(File file) throws IOException {
        List<MaxBalanceImportDto> balanceData = new ArrayList<>();
        int count = 0;
        try(FileInputStream fis = new FileInputStream(file)) {
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;

                //0 -> dtprf, 1 -> фейерверки, 2 -> мандарины, 3 -> билеты
                String dtprf = "";
                Integer fireworks = null;
                Integer mandarins = null;
                for (Cell cell : row)
                    switch (cell.getColumnIndex()) {
                        case 0 -> dtprf = tryGetStringCellValue(cell);
                        case 1 -> fireworks = tryGetNumericCellValue(cell);
                        case 2 -> mandarins = tryGetNumericCellValue(cell);
                    }

                count++;

                if(dtprf == null || dtprf.isEmpty())
                    log.error("ImportService row {} was skipped because service failed to parse dtprf (dtrpf={})", row.getRowNum(), dtprf);
                else if(fireworks == null)
                    log.error("ImportService row {} was skipped because service failed to parse mandarins amount (dtrpf={})", row.getRowNum(), dtprf);
                else if(mandarins == null)
                    log.error("ImportService row {} was skipped because service failed to parse fireworks amount (dtrpf={})", row.getRowNum(), dtprf);
                else
                    balanceData.add(new MaxBalanceImportDto(dtprf, fireworks, mandarins));
            }
            log.info("{} rows processed, {} rows sent to import", count, balanceData.size());
        }
        return balanceData;
    }

    @Override
    public List<? extends ImportDto> doProgressImport(File file) throws IOException {
        List<ProgressImportDto> progressData = new ArrayList<>();
        int count = 0;
        try(FileInputStream fis = new FileInputStream(file)) {
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;

                //0 -> dtprf, 1 -> значение1, 2 -> значение2
                String dtprf = "";
                Integer val1 = null;
                Integer val2 = null;
                for (Cell cell : row)
                    switch (cell.getColumnIndex()) {
                        case 0 -> dtprf = tryGetStringCellValue(cell);
                        case 1 -> val1 = tryGetNumericCellValue(cell);
                        case 2 -> val2 = tryGetNumericCellValue(cell);
                    }

                count++;

                if(dtprf == null || dtprf.isEmpty())
                    log.error("ImportService row {} was skipped because service failed to parse dtprf (dtrpf={})", row.getRowNum(), dtprf);
                else if(val1 == null)
                    log.error("ImportService row {} was skipped because service failed to parse val1 (dtrpf={})", row.getRowNum(), dtprf);
                else if(val2 == null)
                    log.error("ImportService row {} was skipped because service failed to parse val2 (dtrpf={})", row.getRowNum(), dtprf);
                else
                    progressData.add(new ProgressImportDto(dtprf, val1, val2));
            }
            log.info("{} rows processed, {} rows sent to import", count, progressData.size());
        }
        return progressData;
    }


    private Integer tryGetNumericCellValue(Cell cell) {
        if (cell == null) return null;
        Integer val = null;
        try {
            val = (int) cell.getNumericCellValue();
        } catch (IllegalStateException ise) {
            try {
                val = Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException nfe) {
                log.error("Value in cell:{} row:{} is not valid", cell.getAddress().getColumn(), cell.getRow().getRowNum() + 1, nfe);
            }
        }
        return val;
    }

    private String tryGetStringCellValue(Cell cell) {
        if (cell == null) return null;
        String val = null;
        try {
            val = cell.getStringCellValue();
        } catch (IllegalStateException ise) {
            try {
                val = String.valueOf(cell.getNumericCellValue());
            } catch (NumberFormatException nfe) {
                log.error("Value in cell:{} row:{} is not valid", cell.getAddress().getColumn(), cell.getRow().getRowNum() + 1, nfe);
            }
        }
        return val;
    }

    private LocalDate tryGetDateCellValue(Cell cell) {
        if (cell == null) return null;
        LocalDate val = null;
        try {
            val = cell.getDateCellValue()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        } catch (IllegalStateException ise) {
            log.error("Value in cell:{} row:{} is not valid", cell.getAddress().getColumn(), cell.getRow().getRowNum() + 1, ise);
        }
        return val;
    }

    /*

    Создание отчета принятия маркеров

    */
    // Вызов метода
    @Override
    public File doChallengeExport(Month month) throws IOException {
        try {
            // Create report data
            List<ChallengeExportDto> report = logService.createChallengeReport(month);
            // Create report file
            String pathToFile = createChallengeReportFile(report);
            // Send created file to user
            return new File(pathToFile);
        } catch (Exception e) {
            log.error("Error occurred while creating report", e);
            return null;
        }
    }
    // Создание файла отчета
    private String createChallengeReportFile(List<ChallengeExportDto> lines) throws IOException {
        //Название
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd_HHmmss");
        String newFilePath = tempPath + "Report_" + dtf.format(LocalDateTime.now()) + ".xlsx";
        //Создание файла
        try(OutputStream fos = new FileOutputStream(newFilePath)) {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("Report");

            int n = lines.size();
            //Создание строчек соответственно кол-ву точек
            for (int i = 0; i < n + 1; i++) sheet.createRow(i);

            Iterator<ChallengeExportDto> it = lines.listIterator();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    //Создание наименований столбцов
                    createChallengeReportHeader(wb);
                    continue;
                }

                if (!it.hasNext()) break;

                //Запись данных
                ChallengeExportDto line = it.next();
                row.createCell(0).setCellValue(line.getDtprf());
                row.createCell(1).setCellValue(line.getDate());
                row.createCell(2).setCellValue(line.getTime());
                row.createCell(3).setCellValue(line.getMarker());
                row.createCell(4).setCellValue(line.getTimestamp());
            }

            //Запись в файл
            wb.write(fos);
        }
        return newFilePath;
    }
    // Создание заголовка файла
    private static void createChallengeReportHeader(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheet("Report");
        Row header = sheet.getRow(0);

        header.createCell(0).setCellValue("DTPRF");
        header.createCell(1).setCellValue("Дата");
        header.createCell(2).setCellValue("Время");
        header.createCell(3).setCellValue("Маркер активности");
        header.createCell(4).setCellValue("Временной штамп");
    }

    /*

    Создание отчета текущего баланса

     */
    // Вызов метода
    @Override
    public File doBalanceExport(Month month) throws IOException {
        try{
            // Create report data
            List<BalanceExportDto> report = logService.createBalanceLog(month);
            // Create report file
            String pathToFile = createBalanceReportFile(report);
            // Send created file to user
            return new File(pathToFile);
        } catch (Exception e) {
            return null;
        }
    }
    // Создание файла отчета
    private String createBalanceReportFile(List<BalanceExportDto> lines) throws IOException {
        //Название
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd_HHmmss");
        String newFilePath = tempPath + "Report_" + dtf.format(LocalDateTime.now()) + ".xlsx";
        //Создание файла
        try(OutputStream fos = new FileOutputStream(newFilePath)) {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("Report");

            int n = lines.size();
            //Создание строчек соответственно кол-ву точек
            for (int i = 0; i < n + 1; i++) sheet.createRow(i);

            Iterator<BalanceExportDto> it = lines.listIterator();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    //Создание наименований столбцов
                    createBalanceReportHeader(wb);
                    continue;
                }

                if (!it.hasNext()) break;

                //Запись данных
                BalanceExportDto line = it.next();
                row.createCell(0).setCellValue(line.getDtprf());
                row.createCell(1).setCellValue(line.getWinnings());
                row.createCell(2).setCellValue(line.getOpenedTickets());
            }

            //Запись в файл
            wb.write(fos);
        }
        return newFilePath;
    }
    // Создание заголовка файла
    private static void createBalanceReportHeader(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheet("Report");
        Row header = sheet.getRow(0);

        header.createCell(0).setCellValue("DTPRF");
        header.createCell(1).setCellValue("Сумма выигрыша");
        header.createCell(2).setCellValue("Кол-во открытых билетов");
    }

    /*

    Создание отчета результатов лотереи

     */
    // Вызов метода
    @Override
    public File doLotteryExport(Month month) throws IOException {
        try {
            List<LotteryExportDto> report = logService.createLotteryReport(month);
            String pathToFile = createLotteryReportFile(report);
            return new File(pathToFile);
        } catch (Exception e) {
            log.error("Error occurred while creating report", e);
            return null;
        }
    }
    // Создание файла отчета
    private String createLotteryReportFile(List<LotteryExportDto> lines) throws IOException {
        //Название
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd_HHmmss");
        String newFilePath = tempPath + "Report_" + dtf.format(LocalDateTime.now()) + ".xlsx";
        //Создание файла
        try(OutputStream fos = new FileOutputStream(newFilePath)) {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("Report");

            int n = lines.size();
            //Создание строчек соответственно кол-ву точек
            for (int i = 0; i < n + 1; i++) sheet.createRow(i);

            Iterator<LotteryExportDto> it = lines.listIterator();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    //Создание наименований столбцов
                    createLotteryReportHeader(wb);
                    continue;
                }

                if (!it.hasNext()) break;

                //Запись данных
                LotteryExportDto line = it.next();
                row.createCell(0).setCellValue(line.getDtprf());
                row.createCell(1).setCellValue(line.getOrder());
                row.createCell(2).setCellValue(line.getDate());
                row.createCell(3).setCellValue(line.getTime());
                row.createCell(4).setCellValue(line.getMillis());
                row.createCell(5).setCellValue(line.getValue());
                row.createCell(6).setCellValue(line.getTimestamp());
            }

            //Запись в файл
            wb.write(fos);
        }
        return newFilePath;
    }
    // Создание заголовка файла
    private static void createLotteryReportHeader(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheet("Report");
        Row header = sheet.getRow(0);

        header.createCell(0).setCellValue("DTPRF");
        header.createCell(1).setCellValue("Номер билета");
        header.createCell(2).setCellValue("Дата");
        header.createCell(3).setCellValue("Время");
        header.createCell(4).setCellValue("Миллисекунды");
        header.createCell(5).setCellValue("Баллы");
        header.createCell(6).setCellValue("Временной штамп");
    }

    /*

    Создание отчета входа пользователей

     */
    // Вызов метод
    @Override
    public File doEntriesExport(Month month) throws IOException {
        try {
            List<EntryExportDto> report = logService.createEntryReport(month);
            String pathToFile = createEntriesReportFile(report);
            return new File(pathToFile);
        } catch(Exception e) {
            log.error("Error occurred while creating report", e);
            return null;
        }
    }
    // Создание файла отчета
    private String createEntriesReportFile(List<EntryExportDto> lines) throws IOException {
        //Название
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd_HHmmss");
        String newFilePath = tempPath + "Report_" + dtf.format(LocalDateTime.now()) + ".xlsx";
        //Создание файла
        try(OutputStream fos = new FileOutputStream(newFilePath)) {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("Report");

            int n = lines.size();
            //Создание строчек соответственно кол-ву точек
            for (int i = 0; i < n + 1; i++) sheet.createRow(i);

            Iterator<EntryExportDto> it = lines.listIterator();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    //Создание наименований столбцов
                    createEntriesReportHeader(wb);
                    continue;
                }

                if (!it.hasNext()) break;

                //Запись данных
                EntryExportDto line = it.next();
                row.createCell(0).setCellValue(line.getDtprf());
                row.createCell(1).setCellValue(line.getDate());
                row.createCell(2).setCellValue(line.getTime());
                row.createCell(3).setCellValue(line.getTimestamp());
            }

            //Запись в файл
            wb.write(fos);
        }
        return newFilePath;
    }
    // Создание заголовка файла
    private static void createEntriesReportHeader(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheet("Report");
        Row header = sheet.getRow(0);

        header.createCell(0).setCellValue("DTPRF");
        header.createCell(1).setCellValue("Дата");
        header.createCell(2).setCellValue("Время");
        header.createCell(3).setCellValue("Временной штамп");
    }
}
