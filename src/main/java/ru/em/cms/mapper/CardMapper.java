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
import ru.em.cms.model.request.CreateCardRequest;

@Mapper(componentModel = "spring", imports = java.time.LocalDate.class)
public interface CardMapper {

    @Mapping(target = "maskedPan", source = "cardNumber", qualifiedByName = "maskPan")
    @Mapping(target = "expireAt", source = "expiryDate", qualifiedByName = "getExpDate")
    @Mapping(target = "cardHolder", source = "cardHolder")
    CardDto entityToDto(CardEntity entity);

    Set<CardDto> entityListToDtoList(Set<CardEntity> entities);

    @Mapping(target = "balance", constant = "10.0")
    @Mapping(target = "expiryDate", expression = "java(LocalDate.now().plusYears(3))")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    CardEntity createCard(String cardNumber, String cardHolder, UserEntity user, CreateCardRequest request);

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
