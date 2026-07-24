package com.IvanMukha.UserService.mapper;

import com.IvanMukha.UserService.DTO.PaymentCardDTO;
import com.IvanMukha.UserService.model.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentCardMapper {
    PaymentCardDTO toDTO(PaymentCard paymentCard);

    PaymentCard toModel(PaymentCardDTO paymentCardDTO);

    PaymentCard toModelUpdate(PaymentCardDTO paymentCardDTO, @MappingTarget PaymentCard paymentCard);

    List<PaymentCardDTO> toDTOList(List<PaymentCard> paymentCardList);

    List<PaymentCard> toModelList(List<PaymentCardDTO> paymentCardDTOList);
}
