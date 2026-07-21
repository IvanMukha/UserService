package com.IvanMukha.UserService.mapper;

import com.IvanMukha.UserService.DTO.UserDTO;
import com.IvanMukha.UserService.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserListMapper {
    List<UserDTO> toDTOList(List<User> userList);

    List<User> toModelList(List<UserDTO> userDTOList);
}
