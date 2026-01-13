package ru.em.cms.model.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.em.cms.model.type.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetTransferRequest {
    Long id;
    Long userId;
    Long fromCard;
    Long toCard;
    String status;
    Currency currency;
    LocalDateTime dateFrom;
    LocalDateTime dateTo;
}
