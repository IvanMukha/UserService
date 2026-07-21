package com.IvanMukha.UserService.service;

import com.IvanMukha.UserService.DTO.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDTO save(UserDTO userDTO);

    UserDTO getById(Long id);

    Page<UserDTO> getAll(String name, String surname,Pageable pageable);

    UserDTO updateById(Long id, UserDTO userDTO);

    void changeUserStatus(Long id, boolean isActive);

}
