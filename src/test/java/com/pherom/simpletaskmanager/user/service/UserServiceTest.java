package com.pherom.simpletaskmanager.user.service;

import com.pherom.simpletaskmanager.user.dto.UserResponseDTO;
import com.pherom.simpletaskmanager.user.dto.UserUpdateRequestDTO;
import com.pherom.simpletaskmanager.user.entity.User;
import com.pherom.simpletaskmanager.user.exception.EmailAlreadyExistsException;
import com.pherom.simpletaskmanager.user.exception.UserNotFoundException;
import com.pherom.simpletaskmanager.user.exception.UsernameAlreadyExistsException;
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

    @Test
    void updateById_ShouldThrowUserNotFoundException() {
        long idToUpdate = 1;
        UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO("mark", "mark@gmail.com");
        when(repository.findById(idToUpdate)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> service.updateById(idToUpdate, requestDTO));

        verify(repository).findById(idToUpdate);
        verifyNoMoreInteractions(repository);
        assertTrue(ex.getMessage().contains("1"));
    }

    @Test
    void updateById_ShouldThrowUsernameAlreadyExistsException() {
        long idToUpdate = 1;
        UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO("Mark", "mark@gmail.com");
        User existingUser = new User(idToUpdate, "Anna", "password123", "anna@gmail.com");

        when(repository.findById(idToUpdate)).thenReturn(Optional.of(existingUser));
        when(repository.existsByUsername(requestDTO.username())).thenReturn(true);

        UsernameAlreadyExistsException ex = assertThrows(UsernameAlreadyExistsException.class, () -> service.updateById(idToUpdate, requestDTO));

        verify(repository).findById(idToUpdate);
        verify(repository).existsByUsername(requestDTO.username());
        verifyNoMoreInteractions(repository);
        assertTrue(ex.getMessage().contains(requestDTO.username()));
    }

    @Test
    void updateById_ShouldThrowEmailAlreadyExistsException() {
        long idToUpdate = 1;
        UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO("Mark", "mark@gmail.com");
        User existingUser = new User(idToUpdate, "Anna", "password123", "anna@gmail.com");

        when(repository.findById(idToUpdate)).thenReturn(Optional.of(existingUser));
        when(repository.existsByUsername(requestDTO.username())).thenReturn(false);
        when(repository.existsByEmail(requestDTO.email())).thenReturn(true);

        EmailAlreadyExistsException ex = assertThrows(EmailAlreadyExistsException.class, () -> service.updateById(idToUpdate, requestDTO));

        verify(repository).findById(idToUpdate);
        verify(repository).existsByUsername(requestDTO.username());
        verify(repository).existsByEmail(requestDTO.email());
        verifyNoMoreInteractions(repository);
        assertTrue(ex.getMessage().contains(requestDTO.email()));
    }

}