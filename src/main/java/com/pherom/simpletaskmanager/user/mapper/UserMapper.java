package com.pherom.simpletaskmanager.user.mapper;

import com.pherom.simpletaskmanager.user.dto.UserResponseDTO;
import com.pherom.simpletaskmanager.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
    }

}
