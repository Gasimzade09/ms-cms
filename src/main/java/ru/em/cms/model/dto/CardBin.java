package ru.em.cms.model.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.em.cms.exception.NotSupportedException;

@Component
@RequiredArgsConstructor
public class CardBin {
    @Value("${application.bin.visa}")
    private String visa;
    @Value("${application.bin.master}")
    private String master;
    @Value("${application.bin.mir}")
    private String mir;

    public String getBin(String type) {
        switch (type) {
            case "VISA" -> {
                return visa;
            }
            case "MASTER" -> {
                return master;
            }
            case "MIR" -> {
                return mir;
            }
            default -> throw new NotSupportedException("not_supported_type", "Card type is not supported");
        }
    }

}
