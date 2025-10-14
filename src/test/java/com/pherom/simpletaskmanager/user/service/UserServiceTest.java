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

import java.util.List;
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
        verify(repository).existsByEmail(updatedUser.getEmail());
        verify(repository).existsByUsername(updatedUser.getUsername());
        verify(repository).save(any(User.class));
        verify(mapper).toDTO(any(User.class));
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void updateById_ShouldThrowUserNotFoundException() {
        long idToUpdate = 1;
        UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO("mark", "mark@gmail.com");
        when(repository.findById(idToUpdate)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> service.updateById(idToUpdate, requestDTO));

        verify(repository).findById(idToUpdate);
        verifyNoMoreInteractions(repository, mapper);
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
        verifyNoMoreInteractions(repository, mapper);
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
        verifyNoMoreInteractions(repository, mapper);
        assertTrue(ex.getMessage().contains(requestDTO.email()));
    }

    @Test
    void findById_ShouldReturnAnExistingUser() {
        User existingUser = new User(1L, "username", "password", "email");
        UserResponseDTO expectedResponse = new UserResponseDTO(1L, "username", "email");

        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(mapper.toDTO(existingUser)).thenReturn(expectedResponse);

        Optional<UserResponseDTO> responseDTO = service.findById(1L);

        verify(repository).findById(1L);
        verify(mapper).toDTO(existingUser);
        verifyNoMoreInteractions(repository, mapper);
        assertTrue(responseDTO.isPresent());
        assertEquals(expectedResponse, responseDTO.get());
    }

    @Test
    void findById_ShouldReturnEmpty() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserResponseDTO> responseDTO = service.findById(1L);

        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository, mapper);
        assertTrue(responseDTO.isEmpty());
    }

    @Test
    void findAll_ShouldReturnEmpty() {
        List<User> empty = List.of();

        when(repository.findAll()).thenReturn(empty);

        List<UserResponseDTO> responseDTOS = service.findAll();

        verify(repository).findAll();
        verifyNoMoreInteractions(repository, mapper);
        assertTrue(responseDTOS.isEmpty());
    }

    @Test
    void findAll_ShouldReturnListOfUserResponses() {
        List<User> users = List.of(
          new User(1L, "Mark", "password123", "mark@gmail.com"),
          new User(2L, "Anna", "password246", "anna@gmail.com")
        );

        List<UserResponseDTO> expectedResponseDTOS = users.stream().map(u -> new UserResponseDTO(u.getId(), u.getUsername(), u.getEmail())).toList();

        when(repository.findAll()).thenReturn(users);
        for (int i = 0; i < users.size(); ++i) {
            when(mapper.toDTO(users.get(i))).thenReturn(expectedResponseDTOS.get(i));
        }

        List<UserResponseDTO> responseDTOS = service.findAll();

        verify(repository).findAll();
        for (User user : users) {
            verify(mapper).toDTO(user);
        }
        verifyNoMoreInteractions(repository, mapper);
        assertEquals(expectedResponseDTOS, responseDTOS);
    }

    @Test
    void findByUsername_ShouldReturnAnExistingUser() {
        User existingUser = new User(1L, "username", "password", "email");
        UserResponseDTO expectedResponse = new UserResponseDTO(1L, "username", "email");

        when(repository.findByUsername(existingUser.getUsername())).thenReturn(Optional.of(existingUser));
        when(mapper.toDTO(existingUser)).thenReturn(expectedResponse);

        Optional<UserResponseDTO> responseDTO = service.findByUsername(existingUser.getUsername());

        verify(repository).findByUsername(existingUser.getUsername());
        verify(mapper).toDTO(existingUser);
        verifyNoMoreInteractions(repository, mapper);
        assertTrue(responseDTO.isPresent());
        assertEquals(expectedResponse, responseDTO.get());
    }

    @Test
    void findByUsername_ShouldReturnEmpty() {
        when(repository.findByUsername("username")).thenReturn(Optional.empty());

        Optional<UserResponseDTO> responseDTO = service.findByUsername("username");

        verify(repository).findByUsername("username");
        verifyNoMoreInteractions(repository, mapper);
        assertTrue(responseDTO.isEmpty());
    }

}