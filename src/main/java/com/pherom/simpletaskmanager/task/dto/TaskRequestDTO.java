package com.pherom.simpletaskmanager.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequestDTO(
        @NotBlank @Size(max = 50) String title,
        @Size(max = 255) String description,
        Boolean completed
) {}
