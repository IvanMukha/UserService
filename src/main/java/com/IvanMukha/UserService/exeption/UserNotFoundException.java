package com.IvanMukha.UserService.exeption;

import com.IvanMukha.UserService.model.User;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(Long id) {
        super(id, User.class);
    }
}
