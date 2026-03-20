package com.bookstore.mapper;

import com.bookstore.dto.request.UserRegisterRequestDto;
import com.bookstore.dto.response.UserResponseDto;
import com.bookstore.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "shoppingCart", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    User toEntity(UserRegisterRequestDto dto);
}