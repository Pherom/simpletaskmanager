package com.pherom.simpletaskmanager.user.service;

import com.pherom.simpletaskmanager.user.dto.UserResponseDTO;
import com.pherom.simpletaskmanager.user.dto.UserUpdateRequestDTO;
import com.pherom.simpletaskmanager.user.entity.User;
import com.pherom.simpletaskmanager.user.mapper.UserMapper;
import com.pherom.simpletaskmanager.user.repository.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JpaUserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService service;

    @Test
    void updateById_ShouldUpdateAndReturnUpdatedUser() {
        User existingUser = new User(1, "Anna", "password123", "anna@gmail.com");
        User updatedUser = new User(1, "Mark", "password123", "mark@gmail.com");
        UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO(updatedUser.getUsername(), updatedUser.getEmail());
        UserResponseDTO expectedResponseDTO = new UserResponseDTO(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail());

        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(repository.existsByEmail(updatedUser.getEmail())).thenReturn(false);
        when(repository.existsByUsername(updatedUser.getUsername())).thenReturn(false);
        when(repository.save(any(User.class))).thenReturn(updatedUser);
        when(mapper.toDTO(updatedUser)).thenReturn(expectedResponseDTO);

        UserResponseDTO updateResponse = service.updateById(existingUser.getId(), requestDTO);

        assertEquals(expectedResponseDTO, updateResponse);
        verify(repository).findById(1L);
        verify(repository).save(any(User.class));
        verify(mapper).toDTO(any(User.class));
    }

}