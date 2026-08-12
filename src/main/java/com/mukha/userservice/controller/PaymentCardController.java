package com.mukha.userservice.controller;

import com.mukha.userservice.dto.PaymentCardDTO;
import com.mukha.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/api/cards")
public class PaymentCardController {
    private final PaymentCardService paymentCardService;

    @PostMapping()
    @PreAuthorize("@userSecurity.isOwner(#paymentCardDTO.userId) or hasAuthority('admin')")
    public ResponseEntity<PaymentCardDTO> createCard(@Valid @RequestBody PaymentCardDTO paymentCardDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardService.save(paymentCardDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@cardSecurity.isOwner(#id) or hasAuthority('admin')")
    public ResponseEntity<PaymentCardDTO> getById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentCardService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Page<PaymentCardDTO>> getAll(@RequestParam(required = false) Long userId,
                                                       @RequestParam(required = false) String holder,
                                                       @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentCardService.getAll(userId,holder,pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@cardSecurity.isOwner(#id) or hasAuthority('admin')")
    public ResponseEntity<PaymentCardDTO> updateById(@PathVariable Long id,
                                                     @Valid @RequestBody PaymentCardDTO paymentCardDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentCardService.updateById(id,paymentCardDTO));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@cardSecurity.isOwner(#id) or hasAuthority('admin')")
    public ResponseEntity<PaymentCardDTO> changePaymentCardStatus(@PathVariable Long id,
                                                                  @RequestParam Boolean isActive) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentCardService.changePaymentCardStatus(id, isActive));
    }
}
