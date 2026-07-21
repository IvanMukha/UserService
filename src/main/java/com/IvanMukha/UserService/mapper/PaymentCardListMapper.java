package com.IvanMukha.UserService.mapper;

import com.IvanMukha.UserService.DTO.PaymentCardDTO;
import com.IvanMukha.UserService.model.PaymentCard;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = PaymentCardMapper.class)
public interface PaymentCardListMapper {
    List<PaymentCardDTO> toDTOList(List<PaymentCard> paymentCardList);

    List<PaymentCard> toModelList(List<PaymentCardDTO> paymentCardDTOList);
}
