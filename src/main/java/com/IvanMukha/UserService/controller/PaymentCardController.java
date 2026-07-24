package com.IvanMukha.UserService.controller;

import com.IvanMukha.UserService.DTO.PaymentCardDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/cards")
public interface PaymentCardController {

    @PostMapping()
    ResponseEntity<PaymentCardDTO> createCard(@Valid @RequestBody PaymentCardDTO paymentCardDTO);

    @GetMapping("/{id}")
    ResponseEntity<PaymentCardDTO> getById(@PathVariable Long id);

    @GetMapping
    ResponseEntity<Page<PaymentCardDTO>> getAll(@RequestParam(required = false) Long userId,
                                                @RequestParam(required = false) String holder,
                                                @PageableDefault(size = 50) Pageable pageable);

    @PatchMapping("/{id}")
    ResponseEntity<PaymentCardDTO> updateById(@PathVariable Long id,
                                              @Valid @RequestBody PaymentCardDTO paymentCardDTO);

    @PatchMapping("/{id}/status")
    ResponseEntity<PaymentCardDTO> changePaymentCardStatus(@PathVariable Long id,
                                                 @RequestParam Boolean isActive);
}
