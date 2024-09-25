package com.lavkatech.lottery.entity.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record HouseDto(String name, String description, Integer tasksComplete, Integer tasksTotal, Integer maxCoins, TaskStatus taskStatus, String taskDescriptionStringMap, String caption, List<Task> tasks, List<Button> buttons) {

    public List<Map<String, Object>> getTasks() {
        return tasks.stream()
                .map( e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("description", e.description());
                    map.put("isComplete", e.isComplete());
                    return map;
                }).toList();
    }

    public List<Map<String, Object>> getButtons() {
        return buttons.stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("text", e.text());
                    map.put("url", e.url());
                    return map;
                }).toList();
    }

    public String getTaskStatus() {
        return taskStatus == null ? "" : taskStatus.name();
    }
}

record Task(String description, Boolean isComplete) {

}

record Button(String text, String url) {

}

enum TaskStatus {
    AVAILABLE,
    COMPLETE,
    EMPTY
}