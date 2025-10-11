package com.pherom.simpletaskmanager.user.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("A user with the name: " + username + " already exists");
    }
}
