package ru.em.cms.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.em.cms.model.dto.CardDto;
import ru.em.cms.model.entity.CardEntity;
import ru.em.cms.model.entity.UserEntity;

@Mapper
public interface CardMapper {

    @Mapping(target = "maskedPan", source = "cardNumber", qualifiedByName = "maskPan")
    @Mapping(target = "expireAt", source = "expiryDate", qualifiedByName = "getExpDate")
    @Mapping(target = "cardHolder", source = "cardHolder")
    CardDto entityToDto(CardEntity entity);

    Set<CardDto> entityListToDtoList(Set<CardEntity> entities);

    @Named("maskPan")
    default String maskPan(String pan) {
        String firstSymbols = "**** **** **** ";
        return firstSymbols + pan.substring(12);
    }

    @Named("getExpDate")
    default String getExpDate(LocalDate expDate) {
        var result = String.format("%s/%s", expDate.getMonth().getValue(), expDate.getYear() - 2000);
        if (result.length() < 5) {
            return "0" + result;
        }
        return result;
    }
}
