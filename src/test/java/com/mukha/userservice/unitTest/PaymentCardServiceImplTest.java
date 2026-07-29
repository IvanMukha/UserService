package com.mukha.userservice.unitTest;

import com.mukha.userservice.dto.PaymentCardDTO;
import com.mukha.userservice.dto.UserDTO;
import com.mukha.userservice.exception.CardAlreadyExistsException;
import com.mukha.userservice.exception.CardLimitExceededException;
import com.mukha.userservice.exception.PaymentCardNotFoundException;
import com.mukha.userservice.mapper.PaymentCardMapper;
import com.mukha.userservice.mapper.UserMapper;
import com.mukha.userservice.model.PaymentCard;
import com.mukha.userservice.model.User;
import com.mukha.userservice.repository.PaymentCardRepository;
import com.mukha.userservice.service.UserService;
import com.mukha.userservice.service.impl.PaymentCardServiceImpl;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private PaymentCardMapper paymentCardMapper;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    private PaymentCard paymentCard;
    private PaymentCardDTO paymentCardDTO;
    private User user;
    private UserDTO userDTO;


    @BeforeEach
    void setUp() {
        paymentCardService.setSelf(paymentCardService);
        user = new User();
        user.setId(1L);
        userDTO=new UserDTO();
        userDTO.setId(1L);


        paymentCard = new PaymentCard();
        paymentCard.setId(1L);
        paymentCard.setUser(user);
        paymentCard.setNumber("5465865945696356");
        paymentCard.setHolder("Ivan Mukha");
        paymentCard.setExpirationDate(LocalDate.of(2027, Month.APRIL, 20));
        paymentCard.setActive(true);

        paymentCardDTO = new PaymentCardDTO();
        paymentCardDTO.setId(1L);
        paymentCardDTO.setUserId(1L);
        paymentCardDTO.setNumber("5465865945696356");
        paymentCardDTO.setHolder("Ivan Mukha");
        paymentCardDTO.setExpirationDate(LocalDate.of(2027, Month.APRIL, 20));
        paymentCardDTO.setActive(true);
    }

    @Test
    void save_shouldReturnPaymentCardDTO_whenCardLimitNotReachedAndCardNumberNotExists() {
        when(paymentCardRepository.countByUserId(paymentCardDTO.getUserId())).thenReturn(0L);
        when(paymentCardRepository.existsByNumber(paymentCardDTO.getNumber())).thenReturn(false);
        when(userService.getById(paymentCardDTO.getUserId())).thenReturn(userDTO);
        when(paymentCardMapper.toModel(paymentCardDTO)).thenReturn(paymentCard);
        when(paymentCardRepository.save(paymentCard)).thenReturn(paymentCard);
        when(paymentCardMapper.toDTO(paymentCard)).thenReturn(paymentCardDTO);

        PaymentCardDTO result = paymentCardService.save(paymentCardDTO);

        assertThat(result).isEqualTo(paymentCardDTO);
        verify(paymentCardRepository).countByUserId(paymentCardDTO.getUserId());
        verify(paymentCardRepository).existsByNumber(paymentCardDTO.getNumber());
        verify(userService).getById(paymentCardDTO.getUserId());
        verify(paymentCardRepository).save(paymentCard);
    }

    @Test
    void save_shouldThrowException_whenCardLimitReached() {
        when(paymentCardRepository.countByUserId(paymentCardDTO.getUserId())).thenReturn(6L);
        assertThatThrownBy(() -> paymentCardService.save(paymentCardDTO))
                .isInstanceOf(CardLimitExceededException.class)
                .hasMessageContaining(String.valueOf(paymentCardDTO.getUserId()));

        verify(paymentCardRepository, never()).save(any());
        verifyNoInteractions(paymentCardMapper);
    }

    @Test
    void save_shouldThrowException_whenCardNumberAlreadyExists() {
        when(paymentCardRepository.countByUserId(paymentCardDTO.getUserId())).thenReturn(0L);
        when(paymentCardRepository.existsByNumber(paymentCardDTO.getNumber())).thenReturn(true);
        assertThatThrownBy(() -> paymentCardService.save(paymentCardDTO))
                .isInstanceOf(CardAlreadyExistsException.class)
                .hasMessageContaining(paymentCardDTO.getNumber());

        verify(paymentCardRepository, never()).save(any());
        verifyNoInteractions(paymentCardMapper);
    }

    @Test
    void getById_shouldReturnPaymentCard_whenCardExists() {
        when(paymentCardRepository.findById(paymentCardDTO.getId())).thenReturn(Optional.of(paymentCard));
        when(paymentCardMapper.toDTO(paymentCard)).thenReturn(paymentCardDTO);

        PaymentCardDTO result = paymentCardService.getById(1L);
        assertThat(result).isEqualTo(paymentCardDTO);

    }

    @Test
    void getById_shouldThrowException_whenCardNotExists() {
        when(paymentCardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentCardService.getById(999L))
                .isInstanceOf(PaymentCardNotFoundException.class);

        verifyNoInteractions(paymentCardMapper);
    }

    @Test
    void getAll_shouldReturnPageOfCards() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PaymentCard> paymentCardPage = new PageImpl<>(List.of(paymentCard));
        when(paymentCardRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(paymentCardPage);
        when(paymentCardMapper.toDTO(paymentCard)).thenReturn(paymentCardDTO);

        Page<PaymentCardDTO> result = paymentCardService.getAll(1L, " Ivan Mukha", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(paymentCardDTO);
    }

    @Test
    void getAll_shouldReturnEmptyPage_whenNoPaymentCardMatch() {
        Pageable pageable = PageRequest.of(0, 10);
        when(paymentCardRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        Page<PaymentCardDTO> result = paymentCardService.getAll(999L, null, pageable);

        assertThat(result.getContent()).isEmpty();
        verifyNoInteractions(paymentCardMapper);
    }

    @Test
    void updateById_shouldReturnUpdatedPaymentCard_whenExists() {
        PaymentCardDTO updateRequestDTO = new PaymentCardDTO();
        updateRequestDTO.setNumber("546586594569999");
        updateRequestDTO.setHolder("Jonh Doe");
        updateRequestDTO.setUserId(1L);
        updateRequestDTO.setId(1L);

        PaymentCard updatedPaymentCard = new PaymentCard();
        updatedPaymentCard.setId(1L);
        updatedPaymentCard.setNumber("546586594569999");

        PaymentCardDTO updatedPaymentCardDTO = new PaymentCardDTO();
        updatedPaymentCardDTO.setId(1L);
        updatedPaymentCardDTO.setNumber("546586594569999");

        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        when(paymentCardMapper.toDTO(paymentCard)).thenReturn(paymentCardDTO);
        when(paymentCardMapper.toModel(paymentCardDTO)).thenReturn(paymentCard);
        when(paymentCardMapper.toModelUpdate(updateRequestDTO, paymentCard)).thenReturn(updatedPaymentCard);
        when(paymentCardRepository.save(updatedPaymentCard)).thenReturn(updatedPaymentCard);
        when(paymentCardMapper.toDTO(updatedPaymentCard)).thenReturn(updatedPaymentCardDTO);

        PaymentCardDTO result = paymentCardService.updateById(1L, updateRequestDTO);

        assertThat(result.getNumber()).isEqualTo("546586594569999");
        verify(paymentCardRepository).save(updatedPaymentCard);
    }

    @Test
    void updateById_shouldThrowException_whenNotFound() {
        assertThatThrownBy(() -> paymentCardService.updateById(999L, paymentCardDTO))
                .isInstanceOf(PaymentCardNotFoundException.class);

        verify(paymentCardRepository, never()).save(any());
        verifyNoInteractions(paymentCardMapper);
    }

    @Test
    void changePaymentCardStatus_shouldUpdateStatus() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        when(paymentCardMapper.toDTO(paymentCard)).thenReturn(paymentCardDTO);
        when(paymentCardMapper.toModel(paymentCardDTO)).thenReturn(paymentCard);
        when(paymentCardRepository.save(paymentCard)).thenReturn(paymentCard);

        paymentCardService.changePaymentCardStatus(1L, false);
        ArgumentCaptor<PaymentCard> captor = ArgumentCaptor.forClass(PaymentCard.class);
        verify(paymentCardRepository).save(captor.capture());
        assertThat(captor.getValue().getActive()).isFalse();
    }

    @Test
    void changePaymentCardStatus_shouldThrowException_whenNotFound() {
        assertThatThrownBy(() -> paymentCardService.changePaymentCardStatus(999L, true))
                .isInstanceOf(PaymentCardNotFoundException.class);

        verify(paymentCardRepository, never()).save(any());
    }
}