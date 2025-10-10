package com.pherom.simpletaskmanager.user.service;

import com.pherom.simpletaskmanager.user.dto.UserUpdateRequestDTO;
import com.pherom.simpletaskmanager.user.dto.UserResponseDTO;
import com.pherom.simpletaskmanager.user.entity.User;
import com.pherom.simpletaskmanager.user.exception.UserNotFoundException;
import com.pherom.simpletaskmanager.user.mapper.UserMapper;
import com.pherom.simpletaskmanager.user.repository.JpaUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final JpaUserRepository repository;
    private final UserMapper mapper;

    public UserService(JpaUserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public UserResponseDTO updateById(long id, UserUpdateRequestDTO request) {
        Optional<User> found = repository.findById(id);

        if (found.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        return mapper.toDTO(updateUser(found.get(), request));
    }

    public Optional<UserResponseDTO> findById(long id) {
        return repository.findById(id).map(mapper::toDTO);
    }

    public List<UserResponseDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    public Optional<UserResponseDTO> findByUsername(String username) {
        return repository.findByUsername(username).map(mapper::toDTO);
    }

    @Transactional
    public UserResponseDTO deleteById(long id) {
        Optional<User> found = repository.findById(id);
        return found.map(u -> {
            repository.delete(u);
            return mapper.toDTO(u);
        }).orElseThrow(() -> new UserNotFoundException(id));
    }

    private User updateUser(User user, UserUpdateRequestDTO request) {
        if (request.username() != null) {
            user.setUsername(request.username());
        }

        if (request.email() != null) {
            user.setEmail(request.email());
        }

        return repository.save(user);
    }

}
