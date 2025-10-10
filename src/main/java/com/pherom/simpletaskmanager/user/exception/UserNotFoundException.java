package com.pherom.simpletaskmanager.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super("Could not find a user with ID: " + id);
    }

    public UserNotFoundException(String username) {
        super("Could not find a user with username: " + username);
    }
}
