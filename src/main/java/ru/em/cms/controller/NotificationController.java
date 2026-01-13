package ru.em.cms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.em.cms.model.request.GetNotificationRequest;
import ru.em.cms.model.response.NotificationResponse;
import ru.em.cms.model.response.PageableResponse;
import ru.em.cms.service.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/notification")
public class NotificationController {
    private final NotificationService service;

    @GetMapping
    public ResponseEntity<PageableResponse<NotificationResponse>> getNotifications(
            GetNotificationRequest request,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getNotifications(request, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable Long id) {
        return ResponseEntity.ok(service.getNotification(id));
    }
}
