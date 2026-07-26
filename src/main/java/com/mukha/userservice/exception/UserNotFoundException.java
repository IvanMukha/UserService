package com.mukha.userservice.exception;

import com.mukha.userservice.model.User;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(Long id) {
        super(id, User.class);
    }
}
