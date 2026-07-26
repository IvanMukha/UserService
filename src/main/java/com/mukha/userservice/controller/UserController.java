package com.mukha.userservice.controller;

import com.mukha.userservice.dto.UserDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/users")
public interface UserController {

    @PostMapping
    ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO);

    @GetMapping("/{id}")
    ResponseEntity<UserDTO> getById(@PathVariable Long id);

    @GetMapping
    ResponseEntity<Page<UserDTO>> getAll(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) String surname,
                                         @PageableDefault(size = 50) Pageable pageable);

    @PatchMapping("/{id}")
    ResponseEntity<UserDTO> updateById(@PathVariable Long id,
                                        @RequestBody UserDTO userDTO);

    @PatchMapping("/{id}/status")
    ResponseEntity<Void> changeUserStatus(@PathVariable Long id,
                                          @RequestParam Boolean isActive);
}

