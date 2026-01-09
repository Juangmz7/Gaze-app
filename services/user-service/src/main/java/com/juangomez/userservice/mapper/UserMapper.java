package com.juangomez.userservice.mapper;

import com.juangomez.userservice.model.dto.RegisterUserResponse;
import com.juangomez.userservice.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    RegisterUserResponse toResponse (User user);

}
