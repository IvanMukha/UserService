package com.mukha.userservice.service.impl;

import com.mukha.userservice.dto.UserDTO;
import com.mukha.userservice.exception.EmailAlreadyExistsException;
import com.mukha.userservice.exception.UserNotFoundException;
import com.mukha.userservice.mapper.UserMapper;
import com.mukha.userservice.model.User;
import com.mukha.userservice.repository.UserRepository;
import com.mukha.userservice.service.UserService;
import com.mukha.userservice.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private UserService self;

    @Autowired
    public void setSelf(@Lazy UserService self) {
        this.self = self;
    }

    @Override
    @Transactional
    public UserDTO save(UserDTO userDTO) {
        log.debug("Attempting to save new user with email: {}", userDTO.getEmail());
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.warn("Failed to save user. Email already exists: {}", userDTO.getEmail());
            throw new EmailAlreadyExistsException(userDTO.getEmail());
        }
        User savedUser = userRepository.save(userMapper.toModel(userDTO));
        log.debug("Successfully saved user with id: {}", savedUser.getId());
        return userMapper.toDTO(savedUser);
    }

    @Override
    @Cacheable(value = "users_with_card", key = "#id")
    public UserDTO getById(Long id) {
        log.debug("Fetching user by id: {}", id);
        User foundUser = userRepository.findByIdWithPaymentCards(id)
                .orElseThrow(() -> {
                    log.warn("User with id: {} not found", id);
                    return new UserNotFoundException(id);
                });
        return userMapper.toDTO(foundUser);
    }

    @Override
    public Page<UserDTO> getAll(String name, String surname, Pageable pageable) {
        log.debug("Fetching pageable users. Filters - name: {}, surname: {}", name, surname);
        Specification<User> spec = Specification.where(UserSpecification.hasName(name))
                .and(UserSpecification.hasSurname(surname));
        Page<User> foundUsers = userRepository.findAll(spec, pageable);
        return foundUsers.map(userMapper::toDTO);
    }

    @Override
    @Transactional
    @CachePut(value = "users_with_card", key = "#id")
    public UserDTO updateById(Long id, UserDTO userDTO) {
        log.debug("Updating user with id: {}", id);
        User foundUser = userMapper.toModel(self.getById(id));
            if (userDTO.getEmail()!=null && userRepository.existsByEmail(userDTO.getEmail())) {
                log.warn("Failed to update user id: {}. Email already exists: {}", id, userDTO.getEmail());
                throw new EmailAlreadyExistsException(userDTO.getEmail());
        }
        User updatedUser = userMapper.toModelUpdate(userDTO, foundUser);
        UserDTO result = userMapper.toDTO(userRepository.save(updatedUser));
        log.debug("Successfully updated user with id: {}", id);
        return result;

    }

    @Override
    @Transactional
    @CacheEvict(value = "users_with_card", key = "#id")
    public UserDTO changeUserStatus(Long id, Boolean isActive) {
        log.debug("Changing status of user id: {} to active={}", id, isActive);
        User foundUser = userMapper.toModel(self.getById(id));
        foundUser.setActive(isActive);
        UserDTO result = userMapper.toDTO(userRepository.save(foundUser));
        log.debug("Status of user id: {} changed to active={}", id, isActive);
        return result;
    }
}
