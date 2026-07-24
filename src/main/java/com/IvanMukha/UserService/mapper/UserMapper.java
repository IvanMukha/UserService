package com.IvanMukha.UserService.mapper;

import com.IvanMukha.UserService.DTO.UserDTO;
import com.IvanMukha.UserService.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDTO toDTO(User user);

    User toModel(UserDTO userDTO);

    User toModelUpdate(UserDTO userDTO, @MappingTarget User user);

    List<UserDTO> toDTOList(List<User> userList);

    List<User> toModelList(List<UserDTO> userDTOList);
}
