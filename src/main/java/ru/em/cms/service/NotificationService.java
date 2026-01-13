package ru.em.cms.service;

import org.springframework.data.domain.Pageable;
import ru.em.cms.model.dto.NotificationDto;
import ru.em.cms.model.request.GetNotificationRequest;
import ru.em.cms.model.response.NotificationResponse;
import ru.em.cms.model.response.PageableResponse;

public interface NotificationService {

    void notifyAdmin(NotificationDto dto);

    PageableResponse<NotificationResponse> getNotifications(GetNotificationRequest request, Pageable pageable);

    NotificationResponse getNotification(Long id);
}
