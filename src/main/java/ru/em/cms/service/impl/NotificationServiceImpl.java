package ru.em.cms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.em.cms.exception.NotFoundException;
import ru.em.cms.mapper.NotificationMapper;
import ru.em.cms.model.dto.NotificationDto;
import ru.em.cms.model.request.GetNotificationRequest;
import ru.em.cms.model.response.NotificationResponse;
import ru.em.cms.model.response.PageableResponse;
import ru.em.cms.repository.NotificationRepository;
import ru.em.cms.service.NotificationService;
import ru.em.cms.specification.NotificationSpecification;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    @Override
    public void notifyAdmin(NotificationDto dto) {
        var entity = mapper.dtoToEntity(dto);
        repository.save(entity);
    }

    @Override
    public PageableResponse<NotificationResponse> getNotifications(GetNotificationRequest request, Pageable pageable) {
        var spec = new NotificationSpecification(request);
        var notifications = repository.findAll(spec, pageable);
        var paged = notifications.map(mapper::entityToResponse);
        return PageableResponse.of(paged);
    }

    @Override
    public NotificationResponse getNotification(Long id) {
        var notification = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("notification_not_found",
                        String.format("Notification with id %s not found", id)));
        notification.setRead(true);
        repository.save(notification);
        return mapper.entityToResponse(notification);
    }
}
