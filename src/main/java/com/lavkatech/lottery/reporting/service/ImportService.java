package com.lavkatech.lottery.reporting.service;

import com.lavkatech.lottery.reporting.dto.ImportDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
@Service
public interface ImportService {
    int importUserBalance(Map<String, List<ImportDto>> lines);
    int importUserMaxBalance(Map<String, List<ImportDto>> lines);
    int importUserProgress(Map<String, List<ImportDto>> lines);
}
