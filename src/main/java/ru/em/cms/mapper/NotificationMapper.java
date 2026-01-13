package ru.em.cms.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import ru.em.cms.model.dto.NotificationDto;
import ru.em.cms.model.entity.NotificationEntity;
import ru.em.cms.model.response.NotificationResponse;

@Mapper
public interface NotificationMapper {

    NotificationEntity dtoToEntity(NotificationDto dto);

    NotificationResponse entityToResponse(NotificationEntity entity);

    List<NotificationResponse> entityListToResponseList(List<NotificationEntity> entities);
}
