package com.pherom.simpletaskmanager.task;

public record TaskResponseDTO(long id, String title, String description, boolean completed) {
}
