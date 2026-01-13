package ru.em.cms.service;

import java.math.BigDecimal;
import ru.em.cms.model.type.Currency;

public interface ExchangeRateService {
    BigDecimal getRate(Currency from, Currency to);
}
