package com.mukha.userservice.controller;

import com.mukha.userservice.dto.UserDTO;
import com.mukha.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO savedUser = userService.save(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwner(#id) or hasAuthority('admin')")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Page<UserDTO>> getAll(@RequestParam(required = false) String name,
                                                @RequestParam(required = false) String surname,
                                                @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAll(name, surname, pageable));
    }

    @GetMapping("/by-email")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getByEmail(email));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwner(#id) or hasAuthority('admin')")
    public ResponseEntity<UserDTO> updateById(@PathVariable Long id,
                                              @RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateById(id, userDTO));
    }
    @GetMapping("/batch")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<UserDTO>> getUsersByIds(@RequestParam("ids") List<Long> userIds){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllById(userIds));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Void> changeUserStatus(@PathVariable Long id,
                                                 @RequestParam Boolean isActive) {
        userService.changeUserStatus(id, isActive);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
