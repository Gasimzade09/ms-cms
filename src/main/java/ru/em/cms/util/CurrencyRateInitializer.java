package ru.em.cms.util;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.em.cms.model.type.Currency;

@Component
@RequiredArgsConstructor
public class CurrencyRateInitializer {
    private final CurrencyRateStorage storage;

    @PostConstruct
    public void init() {
        List<Currency> currencies = List.of(
                Currency.AZN,
                Currency.USD,
                Currency.EUR,
                Currency.RUB
        );

        Random random = new Random();

        for (Currency from : currencies) {
            for (Currency to : currencies) {
                if (from == to) {
                    storage.put(from, to, BigDecimal.ONE);
                } else {
                    BigDecimal rate = BigDecimal.valueOf(
                            0.5 + random.nextDouble()
                    ).setScale(4, RoundingMode.HALF_UP);

                    storage.put(from, to, rate);
                }
            }
        }
    }
}
