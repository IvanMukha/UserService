package com.mukha.userservice.mapper;

import com.mukha.userservice.dto.PaymentCardDTO;
import com.mukha.userservice.model.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentCardMapper {
    @Mapping(source = "user.id", target = "userId")
    PaymentCardDTO toDTO(PaymentCard paymentCard);

    @Mapping(source = "userId", target = "user.id")
    PaymentCard toModel(PaymentCardDTO paymentCardDTO);

    PaymentCard toModelUpdate(PaymentCardDTO paymentCardDTO, @MappingTarget PaymentCard paymentCard);

    List<PaymentCardDTO> toDTOList(List<PaymentCard> paymentCardList);

    List<PaymentCard> toModelList(List<PaymentCardDTO> paymentCardDTOList);
}
