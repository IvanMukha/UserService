package com.mukha.userservice.DTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentCardDTO implements Serializable {
    private Long id;
    @NotNull(message = "user_id cannot be null")
    private Long userId;
    @NotBlank(message = "card number cannot be empty")
    private String number;
    private String holder;
    @NotNull(message = "expirationDate cannot be null")
    @FutureOrPresent(message = "Card has expired")
    private LocalDate expirationDate;
    @NotNull(message = "active status cannot be null")
    private Boolean active;
}
