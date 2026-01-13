package ru.em.cms.model.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.em.cms.model.type.CardType;
import ru.em.cms.model.type.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCardRequest {
    Long userId;
    Currency currency;
    CardType type;
}
