package com.IvanMukha.UserService.controller.impl;

import com.IvanMukha.UserService.DTO.UserDTO;
import com.IvanMukha.UserService.controller.UserController;
import com.IvanMukha.UserService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
public class DefaultUserController implements UserController {
    private final UserService userService;

    @Override
    public ResponseEntity<UserDTO> createUser(UserDTO userDTO) {
        UserDTO savedUser = userService.save(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @Override
    public ResponseEntity<UserDTO> getById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getById(id));
    }

    @Override
    public ResponseEntity<Page<UserDTO>> getAll(String name, String surname, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAll(name, surname, pageable));
    }

    @Override
    public ResponseEntity<UserDTO> updateById(Long id, UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateById(id, userDTO));
    }

    @Override
    public ResponseEntity<Void> changeUserStatus(Long id, Boolean isActive) {
        userService.changeUserStatus(id, isActive);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
