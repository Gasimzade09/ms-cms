package ru.em.cms.model.dto;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.em.cms.model.type.CardType;
import ru.em.cms.model.type.Status;
import ru.em.cms.model.type.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardDto {
    Long id;
    String maskedPan;
    String expireAt;
    String cardHolder;
    Status status;
    Currency currency;
    BigDecimal balance;
    CardType type;
}
