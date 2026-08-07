package com.mukha.userservice.service;

import com.mukha.userservice.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDTO save(UserDTO userDTO);

    UserDTO getById(Long id);

    Page<UserDTO> getAll(String name, String surname, Pageable pageable);

    UserDTO updateById(Long id, UserDTO userDTO);

    UserDTO changeUserStatus(Long id, Boolean isActive);

    UserDTO getByEmail(String email);

    List<UserDTO> getAllById(List<Long> userIds);
}
