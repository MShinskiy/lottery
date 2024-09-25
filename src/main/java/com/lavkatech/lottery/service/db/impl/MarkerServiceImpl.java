package com.lavkatech.lottery.service.db.impl;

import com.lavkatech.lottery.entity.Marker;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import com.lavkatech.lottery.repository.MarkerRepository;
import com.lavkatech.lottery.service.db.MarkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MarkerServiceImpl implements MarkerService {

    private final MarkerRepository markerRepo;

    @Autowired
    public MarkerServiceImpl(MarkerRepository markerRepo) {
        this.markerRepo = markerRepo;
    }

    @Override
    public Marker getCurrentMarker(Group group, Level level) {
        return markerRepo.findTopByUserGroupAndUserLevelOrderByCreatedOnDesc(group, level)
                .orElseThrow(() -> new NullPointerException(String.format("No active markers found for group %s and level %s.", group, level)));
    }

    @Override
    public List<Marker> getAllCurrentMarker() {
        return markerRepo.findLatestMarkers();
    }

    @Override
    public void updateCurrentMarker(Marker marker, LocalDate expiringOn, Group group, Level level) {
        updateCurrentMarker(marker.getMarker(), expiringOn, group, level);
    }

    @Override
    public void updateCurrentMarker(String marker, LocalDate expiringOn, Group group, Level level) {
        Marker newMarker = new Marker(marker, expiringOn, group, level);
        markerRepo.save(newMarker);
    }
}