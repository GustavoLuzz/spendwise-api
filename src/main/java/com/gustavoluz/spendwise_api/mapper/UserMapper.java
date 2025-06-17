package com.gustavoluz.spendwise_api.mapper;

import com.gustavoluz.spendwise_api.dto.user.UserLoginDto;
import com.gustavoluz.spendwise_api.dto.user.UserRequestDto;
import com.gustavoluz.spendwise_api.dto.user.UserResponseDto;
import com.gustavoluz.spendwise_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserRequestDto dto);

    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserLoginDto dto);
}