package com.IvanMukha.userservice.service;

import com.IvanMukha.userservice.DTO.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDTO save(UserDTO userDTO);

    UserDTO getById(Long id);

    Page<UserDTO> getAll(String name, String surname, Pageable pageable);

    UserDTO updateById(Long id, UserDTO userDTO);

    UserDTO changeUserStatus(Long id, Boolean isActive);

}
