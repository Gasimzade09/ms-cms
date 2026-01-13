package ru.em.cms.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.em.cms.model.dto.CardDto;
import ru.em.cms.model.type.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferResponse {
    Long id;
    String fromCard;
    String toCard;
    String status;
    BigDecimal amount;
    BigDecimal rate;
    BigDecimal amountTo;
    Currency currency;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
