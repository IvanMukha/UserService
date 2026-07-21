package com.IvanMukha.UserService.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(min = 13, max = 19, message = "Card number must be from 13 to 19 characters")
    @Pattern(regexp = "^[0-9]+$", message = "Card number must contain digits only")
    private String number;
    private String holder;
    @NotNull(message = "expirationDate cannot be null")
    @FutureOrPresent(message = "Card has expired")
    private LocalDate expirationDate;
    @NotNull(message = "active status cannot be null")
    private Boolean active;
}
