package com.IvanMukha.UserService.service.impl;

import com.IvanMukha.UserService.DTO.UserDTO;
import com.IvanMukha.UserService.exception.EmailAlreadyExistsException;
import com.IvanMukha.UserService.exception.UserNotFoundException;
import com.IvanMukha.UserService.mapper.UserMapper;
import com.IvanMukha.UserService.model.User;
import com.IvanMukha.UserService.repository.UserRepository;
import com.IvanMukha.UserService.service.UserService;
import com.IvanMukha.UserService.specification.UserSpecification;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException(userDTO.getEmail());
        }
        return userMapper.toDTO(userRepository.save(userMapper.toModel(userDTO)));
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public UserDTO getById(Long id) {
        User foundUser = userRepository.findByIdWithPaymentCards(id).orElseThrow(() -> new UserNotFoundException(id));
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
    @CachePut(value = "users",key = "#id")
    public UserDTO updateById(Long id, UserDTO userDTO) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException(userDTO.getEmail());
        }
        User updatedUser = userMapper.toModelUpdate(userDTO, foundUser);
        return userMapper.toDTO(userRepository.save(updatedUser));

    }

    @Override
    @Transactional
    @CacheEvict(value = "users",key = "#id")
    public void changeUserStatus(Long id, Boolean isActive) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        foundUser.setActive(isActive);
        userRepository.save(foundUser);
    }
}
