package com.IvanMukha.UserService.DTO;

import com.IvanMukha.UserService.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PaymentCardDTO {
    private Long id;
    @NotNull(message = "user_id cannot be null")
    private Long userId;
    @NotBlank(message = "card number cannot be empty")
    private String number;
    private String holder;
    @NotNull(message = "expirationDate cannot be null")
    private LocalDate expirationDate;
    @NotNull(message = "active status cannot be null")
    private Boolean active;
}
