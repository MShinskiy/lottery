package com.lavkatech.lottery.controller;

import com.lavkatech.lottery.entity.Marker;
import com.lavkatech.lottery.entity.dto.MarkerDto;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import com.lavkatech.lottery.reporting.dto.ImportDto;
import com.lavkatech.lottery.reporting.enumeration.ExportType;
import com.lavkatech.lottery.reporting.enumeration.ImportType;
import com.lavkatech.lottery.reporting.enumeration.Month;
import com.lavkatech.lottery.service.db.MarkerService;
import com.lavkatech.lottery.reporting.service.ReportService;
import com.lavkatech.lottery.service.db.UserService;
import com.lavkatech.lottery.util.Util;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminMvcController {

    private final static Logger log = LogManager.getLogger();
    private final static String tempPath = System.getProperty("java.io.tmpdir") + File.separator;
    private final MarkerService markerService;
    private final ReportService reportService;
    private final UserService userService;

    @Autowired
    public AdminMvcController(MarkerService markerService, ReportService reportService, UserService userService) {
        this.markerService = markerService;
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping("/panel")
    public String getAdminPanel(Model model) {

        List<Marker> markers;
        try {
            markers = markerService.getAllCurrentMarker();
        } catch (NullPointerException e) {
            markers = createTemplateMarkers();
        }
        List<MarkerDto> dtos = markers
                .stream()
                .map(m ->
                        new MarkerDto(
                                m.getMarker(),
                                m.getExpiringOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                m.getUserGroup(),
                                m.getUserLevel()
                        )
                )
                .collect(Collectors.toList());

        fillWithTemplateMarkers(dtos);

        model.addAttribute("markers", dtos);

        return "admin-panel";
    }

    private List<Marker> createTemplateMarkers() {
        List<Marker> markers = new ArrayList<>();
        for (Group group : Group.values()) {
            for (Level level : Level.values()) {
                markers.add(createTemplateMarker(group, level));
            }
        }
        return markers;
    }

    private Marker createTemplateMarker(Group group, Level level) {
        return new Marker("", LocalDate.MIN, group, level);
    }

    private void fillWithTemplateMarkers(List<MarkerDto> markersDto) {
        List<Marker> templates = createTemplateMarkers();
        for (MarkerDto dto : markersDto) {
            templates.removeIf(t -> t.getUserGroup().equals(dto.group()) && t.getUserLevel().equals(dto.level()));
        }

        markersDto.addAll(templates.stream()
                .map(t ->
                        new MarkerDto(
                                t.getMarker(),
                                t.getExpiringOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                t.getUserGroup(),
                                t.getUserLevel()
                        )
                )
                .toList());
    }

    @PostMapping("/import")
    public String fileImport(RedirectAttributes model, @RequestParam("file") MultipartFile file, @RequestParam("import-type") String importType) {
        if (file.isEmpty()) {
            model.addFlashAttribute("msgUld", "Файл пустой или отсутствует");
            log.error("ImportService file is empty");
            return "admin-panel";
        }
        String filename = file.getOriginalFilename();
        String newFilename = Util.generateRandomFilename(32) + ".xlsx";
        File tempFile = new File(tempPath + newFilename);

        try {
            Files.copy(
                    file.getInputStream(),
                    tempFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
            log.info("Import file downloaded with name {}", tempFile.getName());
        } catch (IOException e) {
            log.error("Import file with name {} could not be downloaded", tempFile.getName(), e);
        }

        //Update users from file
        try {
            long startProcess = System.nanoTime();
            List<? extends ImportDto> lines;
            switch (ImportType.valueOf(importType)) {
                case BALANCE -> lines = reportService.doBalanceImport(tempFile);
                case MAX_BALANCE -> lines = reportService.doMaxBalanceImport(tempFile);
                case PROGRESS -> lines = reportService.doProgressImport(tempFile);
                default -> {
                    log.error("Unknown import type for {}", importType);
                    model.addFlashAttribute("msgUld", "Произошла непредвиденная ошибка.");
                    return "redirect:panel";
                }
            }
            long endProcess = System.nanoTime();
            long processTime = endProcess - startProcess;
            log.debug("Total processing time for file {} ({}): {} ms", filename, newFilename, processTime / 1000000);
            //Do update
            userService.importUserData(lines);

            //Log and notify admin
            model.addFlashAttribute("msgUld", String.format("Файл %s загружен и импортирован.", filename));
            return "redirect:panel";
        } catch (Exception e) {
            log.error("Error occurred while importing data from file with name {}, original filename {}", tempFile.getName(), filename, e);
            model.addFlashAttribute("msgUld", String.format("Во время импортирования файла %s произошла ошибка", filename));
            return "redirect:panel";
        } finally {
            boolean isDeleted = tempFile.delete();
            log.info("Temp file with name {} has been deleted: {}", tempFile.getName(), isDeleted);
        }
    }

    @GetMapping(value = "/export",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody FileSystemResource fileExport(@RequestParam("export-type") ExportType type,
                                                       @RequestParam("export-month") Month month,
                                                       HttpServletResponse response,
                                                       Model model
    ) {
        try {
            File toDownload =
                    type == ExportType.CHALLENGE ? reportService.doChallengeExport(month) :
                            type == ExportType.BALANCE ? reportService.doBalanceExport(month) :
                                    type == ExportType.LOTTERY ? reportService.doLotteryExport(month) :
                                            type == ExportType.ENTRIES ? reportService.doEntriesExport(month) :
                                                    null;
            if (toDownload == null) throw new NullPointerException("Пустой файл отчета");
            //Send created file to user
            model.addAttribute("msgDwld", "Сейчас начнется скачивание...");
            response.setHeader("Content-Disposition", "attachment;filename=" + toDownload.getName());
            log.info("Temp file with name {} has been created for export", toDownload.getName());
            return new FileSystemResource(toDownload);
        } catch (Exception e) {
            log.error("Error occurred while creating report", e);
            model.addAttribute("msgDwld", "Во время составления отчета произошла ошибка");
            return null;
        }
    }

    @PostMapping("/markers")
    public String addMarker(String marker, @RequestParam("date") LocalDate expiringOn, Group group, Level level) {
        markerService.updateCurrentMarker(marker, expiringOn, group, level);
        return "redirect:panel";
    }
}