package ru.em.cms.util;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import ru.em.cms.model.dto.CurrencyPair;
import ru.em.cms.model.type.Currency;

@Component
public class CurrencyRateStorage {
    private final Map<CurrencyPair, BigDecimal> rates = new ConcurrentHashMap<>();

    public void put(Currency from, Currency to, BigDecimal rate) {
        rates.put(new CurrencyPair(from, to), rate);
    }

    public BigDecimal get(Currency from, Currency to) {
        return rates.get(new CurrencyPair(from, to));
    }

    public boolean contains(Currency from, Currency to) {
        return rates.containsKey(new CurrencyPair(from, to));
    }
}
