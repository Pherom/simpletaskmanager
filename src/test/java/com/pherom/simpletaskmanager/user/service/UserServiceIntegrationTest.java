package com.pherom.simpletaskmanager.user.service;

import com.pherom.simpletaskmanager.user.dto.UserResponseDTO;
import com.pherom.simpletaskmanager.user.dto.UserUpdateRequestDTO;
import com.pherom.simpletaskmanager.user.entity.User;
import com.pherom.simpletaskmanager.user.exception.EmailAlreadyExistsException;
import com.pherom.simpletaskmanager.user.exception.UserNotFoundException;
import com.pherom.simpletaskmanager.user.exception.UsernameAlreadyExistsException;
import com.pherom.simpletaskmanager.user.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private JpaUserRepository repository;

    @Autowired
    private UserService service;

    @BeforeEach
    void reset() {
        repository.deleteAll();
    }

    @Test
    void updateUserByIdAndRetrieveIt() {
        User existingUser = repository.save(new User("Mark", "password123", "mark@gmail.com"));

        String username = "Shimon";
        String email = "shimon@gmail.com";

        UserResponseDTO updateResponseDTO = service.updateById(existingUser.getId(), new UserUpdateRequestDTO(username, email));
        Optional<UserResponseDTO> findResponseDTO = service.findById(updateResponseDTO.id());

        assertTrue(findResponseDTO.isPresent());
        assertEquals(username, updateResponseDTO.username());
        assertEquals(email, updateResponseDTO.email());
        assertEquals(username, findResponseDTO.get().username());
        assertEquals(email, findResponseDTO.get().email());
    }

    @Test
    void updateNonExistingUser() {
        assertThrows(UserNotFoundException.class, () -> service.updateById(3, new UserUpdateRequestDTO("Shimon", "shimon@gmail.com")));
    }

    @Test
    void updateUserWithAnAlreadyExistingUsername() {
        String existingUsername = "Anna";

        User existingUser = repository.save(new User("Mark", "password123", "mark@gmail.com"));
        repository.save(new User(existingUsername, "password246", "anna@gmail.com"));

        UsernameAlreadyExistsException ex = assertThrows(UsernameAlreadyExistsException.class, () -> service.updateById(existingUser.getId(), new UserUpdateRequestDTO(existingUsername, null)));
        assertTrue(ex.getMessage().contains(existingUsername));
    }

    @Test
    void updateUserWithAnAlreadyExistingEmail() {
        String existingEmail = "anna@gmail.com";
        User existingUser = repository.save(new User("Mark", "password123", "mark@gmail.com"));
        repository.save(new User("Anna", "password246", existingEmail));

        EmailAlreadyExistsException ex = assertThrows(EmailAlreadyExistsException.class, () -> service.updateById(existingUser.getId(), new UserUpdateRequestDTO("Shimon", existingEmail)));
        assertTrue(ex.getMessage().contains(existingEmail));
    }

    @Test
    void findUserByExistingId() {
        User existingUser = repository.save(new User("Mark", "password123", "mark@gmail.com"));

        UserResponseDTO expected = new UserResponseDTO(existingUser.getId(), existingUser.getUsername(), existingUser.getEmail());
        Optional<UserResponseDTO> responseDTO = service.findById(existingUser.getId());

        assertTrue(responseDTO.isPresent());
        assertEquals(expected, responseDTO.get());
    }

    @Test
    void findUserByMissingId() {
        assertTrue(service.findById(3).isEmpty());
    }

    @Test
    void findAllUsersEmpty() {
        List<UserResponseDTO> responseDTOS = service.findAll();
        assertTrue(responseDTOS.isEmpty());
    }

    @Test
    void findAllUsers() {
        User existingUser1 = repository.save(new User("Mark", "password123", "mark@gmail.com"));
        User existingUser2 = repository.save(new User("Anna", "password246", "anna@gmail.com"));

        List<UserResponseDTO> expected = List.of(
                new UserResponseDTO(existingUser1.getId(), existingUser1.getUsername(), existingUser1.getEmail()),
                new UserResponseDTO(existingUser2.getId(), existingUser2.getUsername(), existingUser2.getEmail())
        );

        List<UserResponseDTO> all = service.findAll();
        assertEquals(expected, all);
    }

    @Test
    void findByExistingUsername() {
        User existingUser1 = repository.save(new User("Mark", "password123", "mark@gmail.com"));
        UserResponseDTO expected = new UserResponseDTO(existingUser1.getId(), existingUser1.getUsername(), existingUser1.getEmail());

        Optional<UserResponseDTO> responseDTO = service.findByUsername(existingUser1.getUsername());

        assertTrue(responseDTO.isPresent());
        assertEquals(expected, responseDTO.get());
    }

    @Test
    void findByMissingUsername() {
        Optional<UserResponseDTO> responseDTO = service.findByUsername("something");
        assertTrue(responseDTO.isEmpty());
    }

    @Test
    void deleteByExistingId() {
        User existingUser1 = repository.save(new User("Mark", "password123", "mark@gmail.com"));

        service.deleteById(existingUser1.getId());

        Optional<User> foundUser = repository.findById(existingUser1.getId());
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void deleteByMissingId() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> service.deleteById(1));
        assertTrue(ex.getMessage().contains("1"));
    }

    @Test
    void deleteAll() {
        User existingUser1 = repository.save(new User("Mark", "password123", "mark@gmail.com"));
        User existingUser2 = repository.save(new User("Anna", "password246", "anna@gmail.com"));

        service.deleteAll();

        List<User> all = repository.findAll();
        assertTrue(all.isEmpty());
    }
}