package com.pherom.simpletaskmanager.task.dto;

public record TaskResponseDTO(long id, String title, String description, boolean completed) {
}
