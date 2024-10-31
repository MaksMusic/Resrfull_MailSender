package org.example.mailsender.mapper;

import org.example.mailsender.model.dto.UserRegDto;
import org.example.mailsender.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRegDto userRegDto);
}
