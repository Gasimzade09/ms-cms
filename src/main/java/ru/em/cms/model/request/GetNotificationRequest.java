package ru.em.cms.model.request;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetNotificationRequest {
    Long id;
    String type;
    Boolean isRead;
    LocalDateTime dateFrom;
    LocalDateTime dateTo;
}
