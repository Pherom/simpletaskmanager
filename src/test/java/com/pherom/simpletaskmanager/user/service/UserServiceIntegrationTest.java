package com.pherom.simpletaskmanager.user.service;

import com.pherom.simpletaskmanager.user.dto.UserResponseDTO;
import com.pherom.simpletaskmanager.user.dto.UserUpdateRequestDTO;
import com.pherom.simpletaskmanager.user.entity.User;
import com.pherom.simpletaskmanager.user.exception.EmailAlreadyExistsException;
import com.pherom.simpletaskmanager.user.exception.UserNotFoundException;
import com.pherom.simpletaskmanager.user.exception.UsernameAlreadyExistsException;
import com.pherom.simpletaskmanager.user.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceIntegrationTest {

    @Autowired
    private JpaUserRepository repository;

    @Autowired
    private UserService service;

    @BeforeAll
    void reset() {
        repository.save(new User("Mark", "password123", "mark@gmail.com"));
        repository.save(new User("Anna", "password246", "anna@gmail.com"));
    }

    @Test
    void updateUserByIdAndRetrieveIt() {
        String username = "Shimon";
        String email = "shimon@gmail.com";

        UserResponseDTO updateResponseDTO = service.updateById(1, new UserUpdateRequestDTO(username, email));
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
        String updatedUsername = "Anna";
        UsernameAlreadyExistsException ex = assertThrows(UsernameAlreadyExistsException.class, () -> service.updateById(1, new UserUpdateRequestDTO(updatedUsername, null)));
        assertTrue(ex.getMessage().contains(updatedUsername));
    }

    @Test
    void updateUserWithAnAlreadyExistingEmail() {
        String updatedEmail = "anna@gmail.com";
        EmailAlreadyExistsException ex = assertThrows(EmailAlreadyExistsException.class, () -> service.updateById(1, new UserUpdateRequestDTO("Shimon", updatedEmail)));
        assertTrue(ex.getMessage().contains(updatedEmail));
    }

}