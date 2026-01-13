package ru.em.cms.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.em.cms.model.type.Currency;
import ru.em.cms.service.ExchangeRateService;
import ru.em.cms.util.CurrencyRateStorage;

@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final CurrencyRateStorage storage;


    @Override
    public BigDecimal getRate(Currency from, Currency to) {
        if (from == to) {
            return BigDecimal.valueOf(1.0);
        }
        BigDecimal rate = storage.get(from, to);
        if (rate == null) {
            throw new IllegalStateException("Rate not found: " + from + " -> " + to);
        }
        return rate;
    }
}
