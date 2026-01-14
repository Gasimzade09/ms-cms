package ru.em.cms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.em.cms.model.dto.UserDto;
import ru.em.cms.model.request.UserRegistrationRequest;
import ru.em.cms.model.entity.UserEntity;

@Mapper
public interface UserMapper {

    UserEntity dtoToEntity(UserRegistrationRequest request);

    UserDto entityToDto(UserEntity entity);
}
