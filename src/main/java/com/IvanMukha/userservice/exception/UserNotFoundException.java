package com.IvanMukha.userservice.exception;

import com.IvanMukha.userservice.model.User;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(Long id) {
        super(id, User.class);
    }
}
