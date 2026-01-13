package ru.em.cms.service;

import java.util.Set;
import org.springframework.data.domain.Pageable;
import ru.em.cms.model.dto.CardDto;
import ru.em.cms.model.request.ChangeCardStatusRequest;
import ru.em.cms.model.request.CreateCardRequest;
import ru.em.cms.model.request.GetCardRequest;
import ru.em.cms.model.response.PageableResponse;
import ru.em.cms.model.response.TotalBalanceResponse;
import ru.em.cms.model.type.Currency;

public interface CardService {

    CardDto createCard(CreateCardRequest request);

    PageableResponse<CardDto> getCards(GetCardRequest request, Pageable pageable);

    void deleteCard(Long cardId);

    CardDto changeCardStatus(ChangeCardStatusRequest request);

    void requestToBlockCard(Long id);

    TotalBalanceResponse getTotalBalance(Currency currency);
}
