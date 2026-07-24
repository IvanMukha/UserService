package com.IvanMukha.UserService.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
@Getter
@Setter
@FieldNameConstants
public class PaymentCard extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_card_seq_generator")
    @SequenceGenerator(name = "payment_card_seq_generator", sequenceName = "payment_card_seq", allocationSize = 1)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "number", nullable = false, unique = true, length = 19)
    private String number;
    @Column(name ="holder")
    private String holder;
    @Column(name = "expiration_date",nullable = false)
    private LocalDate expirationDate;
    @Column(name="active", nullable = false)
    private Boolean active;
}
