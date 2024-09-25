package com.lavkatech.lottery.entity.dto;

import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;

public record MarkerDto(String marker, String expOn, Group group, Level level) {
}
