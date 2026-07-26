package com.mukha.userservice.unitTest;

import com.mukha.userservice.DTO.UserDTO;
import com.mukha.userservice.exception.EmailAlreadyExistsException;
import com.mukha.userservice.exception.UserNotFoundException;
import com.mukha.userservice.mapper.UserMapper;
import com.mukha.userservice.model.User;
import com.mukha.userservice.repository.UserRepository;
import com.mukha.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setSurname("Mukha");
        user.setEmail("myEmail@gmail.com");
        user.setBirthDate(LocalDate.of(2000, Month.APRIL, 20));
        user.setActive(true);

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Ivan");
        userDTO.setSurname("Mukha");
        userDTO.setEmail("myEmail@gmail.com");
        userDTO.setBirthDate(LocalDate.of(2000, Month.APRIL, 20));
        userDTO.setActive(true);
    }

    @Test
    void save_shouldReturnSavedUser_whenEmailNotExists() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userMapper.toModel(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.save(userDTO);

        assertThat(result).isEqualTo(userDTO);
        verify(userRepository).existsByEmail(userDTO.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void save_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.save(userDTO))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(userDTO.getEmail());

        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }


    @Test
    void getById_shouldReturnUser_whenUserExists() {
        when(userRepository.findByIdWithPaymentCards(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getById(1L);

        assertThat(result).isEqualTo(userDTO);
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(userRepository.findByIdWithPaymentCards(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(999L))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(userMapper);
    }

    @Test
    void getAll_shouldReturnPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(userPage);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        Page<UserDTO> result = userService.getAll("Ivan", "Mukha", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(userDTO);
    }

    @Test
    void getAll_shouldReturnEmptyPage_whenNoUsersMatch() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        Page<UserDTO> result = userService.getAll("Unknown", null, pageable);

        assertThat(result.getContent()).isEmpty();
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateById_shouldReturnUpdatedUser_whenExists() {
        UserDTO updateRequest = new UserDTO();
        updateRequest.setName("Updated");
        updateRequest.setSurname("Mukha");
        updateRequest.setBirthDate(LocalDate.of(2000, Month.APRIL, 20));
        updateRequest.setEmail("myEmail@gmail.com");
        updateRequest.setActive(true);

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated");

        UserDTO updatedDTO = new UserDTO();
        updatedDTO.setId(1L);
        updatedDTO.setName("Updated");

        when(userRepository.findByIdWithPaymentCards(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        when(userMapper.toModel(userDTO)).thenReturn(user);
        when(userRepository.existsByEmail(updateRequest.getEmail())).thenReturn(false);
        when(userMapper.toModelUpdate(updateRequest, user)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toDTO(updatedUser)).thenReturn(updatedDTO);

        UserDTO result = userService.updateById(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Updated");
        verify(userRepository).save(updatedUser);
    }

    @Test
    void updateById_shouldThrowException_whenNotFound() {
        assertThatThrownBy(() -> userService.updateById(999L, userDTO))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    void changeUserStatus_shouldUpdateStatus() {
        when(userRepository.findByIdWithPaymentCards(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        when(userMapper.toModel(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        userService.changeUserStatus(1L, false);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getActive()).isFalse();
    }

    @Test
    void changeUserStatus_shouldThrowException_whenNotFound() {
        assertThatThrownBy(() -> userService.changeUserStatus(999L, true))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
    }
}
