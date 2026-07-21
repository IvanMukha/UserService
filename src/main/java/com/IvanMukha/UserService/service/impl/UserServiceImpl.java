package com.IvanMukha.UserService.service.impl;

import com.IvanMukha.UserService.DTO.UserDTO;
import com.IvanMukha.UserService.mapper.UserMapper;
import com.IvanMukha.UserService.model.User;
import com.IvanMukha.UserService.repository.UserRepository;
import com.IvanMukha.UserService.service.UserService;
import com.IvanMukha.UserService.specification.UserSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO save(UserDTO userDTO) {
        return userMapper.toDTO(userRepository.save(userMapper.toModel(userDTO)));
    }

    @Override
    public UserDTO getById(Long id) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with this id:" + id + "not found"));
        return userMapper.toDTO(foundUser);
    }

    @Override
    public Page<UserDTO> getAll(String name, String surname, Pageable pageable) {
        Specification<User> spec = Specification.where(UserSpecification.hasName(name))
                .and(UserSpecification.hasSurname(surname));
        Page<User> foundUsers = userRepository.findAll(spec, pageable);
        return foundUsers.map(userMapper::toDTO);
    }

    @Override
    @Transactional
    public UserDTO updateById(Long id, UserDTO userDTO) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with this" + id + "not found"));
        User updatedUser = userMapper.toModelUpdate(userDTO, foundUser);
        return userMapper.toDTO(userRepository.save(updatedUser));

    }

    @Override
    @Transactional
    public void changeUserStatus(Long id, boolean isActive) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with this" + id + "not found"));
        foundUser.setActive(isActive);
        userRepository.save(foundUser);
    }
}
