package ru.em.cms.mapper;

import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.em.cms.model.entity.CardEntity;
import ru.em.cms.model.entity.TransferEntity;
import ru.em.cms.model.request.CreateTransferRequest;
import ru.em.cms.model.response.TransferResponse;

@Mapper
public interface TransferMapper {

    @Mapping(target = "status", constant = "COMPLETED")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusDescription", constant = "Success")
    @Mapping(target = "amountFrom", source = "request.amount")
    @Mapping(target = "amountTo", source = "convertedAmount")
    @Mapping(target = "currency", source = "fromCard.currency")
    @Mapping(target = "fromCard", source = "fromCard")
    @Mapping(target = "toCard", source = "toCard")
    @Mapping(target = "userId", source = "fromCard.user.id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TransferEntity requestToEntity(CreateTransferRequest request, CardEntity fromCard, CardEntity toCard,
                                   BigDecimal rate, BigDecimal convertedAmount);

    @Mapping(target = "fromCard", source = "fromCard", qualifiedByName = "setMaskedPan")
    @Mapping(target = "toCard", source = "toCard", qualifiedByName = "setMaskedPan")
    @Mapping(target = "amount", source = "amountFrom")
    TransferResponse entityToResponse(TransferEntity entity);

    @Named("setMaskedPan")
    default String setMaskedPan(CardEntity entity) {
        String firstSymbols = "**** **** **** ";
        return firstSymbols + entity.getCardNumber().substring(12);
    }
}
