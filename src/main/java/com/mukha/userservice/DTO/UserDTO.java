package com.mukha.userservice.DTO;

import com.mukha.userservice.model.PaymentCard;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UserDTO implements Serializable {
    private Long id;
    @NotBlank(message = "name cannot be empty")
    private String name;
    @NotBlank(message = "surname cannot be empty")
    private String surname;
    @NotNull(message = "birth date cannot be null")
    @Past(message = "birth day must be in the past")
    private LocalDate birthDate;
    @NotBlank(message = "email cannot be empty")
    @Email(message = "email must be valid")
    private String email;
    @NotNull(message = "active status cannot be null")
    private Boolean active;
    private List<PaymentCardDTO> paymentCardsDTO;
}
