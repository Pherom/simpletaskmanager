package com.pherom.simpletaskmanager.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDTO(@Size(min = 3, max = 25) String username, @Email String email) {}
