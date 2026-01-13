package ru.em.cms.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.em.cms.exception.NotFoundException;
import ru.em.cms.mapper.CardMapper;
import ru.em.cms.model.dto.CardBin;
import ru.em.cms.model.dto.CardDto;
import ru.em.cms.model.dto.NotificationDto;
import ru.em.cms.model.request.ChangeCardStatusRequest;
import ru.em.cms.model.request.CreateCardRequest;
import ru.em.cms.model.entity.CardEntity;
import ru.em.cms.model.request.GetCardRequest;
import ru.em.cms.model.response.PageableResponse;
import ru.em.cms.model.response.TotalBalanceResponse;
import ru.em.cms.model.type.Currency;
import ru.em.cms.model.type.Role;
import ru.em.cms.model.type.Status;
import ru.em.cms.repository.CardRepository;
import ru.em.cms.repository.UserRepository;
import ru.em.cms.service.CardService;
import ru.em.cms.service.ExchangeRateService;
import ru.em.cms.service.NotificationService;
import ru.em.cms.specification.CardSpecification;
import ru.em.cms.util.CardUtil;
import ru.em.cms.util.CurrentUserProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CurrentUserProvider provider;
    private final UserRepository userRepository;
    private final CardBin cardBin;
    private final CardRepository cardRepository;
    private final CardMapper mapper;
    private final NotificationService notificationService;
    private final ExchangeRateService exchangeRateService;

    @Override
    @Transactional
    public CardDto createCard(CreateCardRequest request) {
        log.info("ActionLog.createCard.Start");
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("user_not_found",
                        String.format("User with id %s not found", request.getUserId())));
        var cardHolder = String.format("%s %s", user.getFirstname(), user.getLastname()).toUpperCase();
        var bin = cardBin.getBin(request.getType().name());
        for (int i = 0; i < 5; i++) {
            try {
                String number = CardUtil.generateCardNumber(bin);
                CardEntity card = new CardEntity();
                card.setCardNumber(number);
                card.setBalance(BigDecimal.ZERO);
                card.setUser(user);
                card.setExpiryDate(LocalDate.now().plusYears(3));
                card.setStatus(Status.ACTIVE);
                card.setType(request.getType());
                card.setCardHolder(cardHolder);
                card.setCurrency(request.getCurrency());
                cardRepository.save(card);
                return mapper.entityToDto(card);
            } catch (DataIntegrityViolationException ignored) {}
        }

        throw new IllegalStateException("Unable to generate unique card number");
    }

    @Override
    public PageableResponse<CardDto> getCards(GetCardRequest request, Pageable pageable) {
        var user = provider.get();
        if (!user.getRole().equals(Role.ADMIN)) {
            request.setUserId(user.getId());
        }
        var cards = cardRepository.findAll(new CardSpecification(request), pageable);
        var mapped = cards.map(mapper::entityToDto);
        return PageableResponse.of(mapped);
    }

    @Override
    public void deleteCard(Long cardId) {
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("card_not_found",
                        String.format("Card with id %s not found", cardId)));
        cardRepository.delete(card);
    }

    @Override
    public CardDto changeCardStatus(ChangeCardStatusRequest request) {
        var card = cardRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("card_not_found",
                        String.format("Card with id %s not found", request.getId())));
        card.setStatus(request.getStatus());
        card = cardRepository.save(card);
        return mapper.entityToDto(card);
    }

    @Override
    public void requestToBlockCard(Long id) {
        var user = provider.get();
        var card = cardRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("card_not_found",
                        String.format("Card with id %s not found", id)));
        NotificationDto dto = new NotificationDto("BLOCK_CARD", String.format("Please block card with id %s", id));
        notificationService.notifyAdmin(dto);
    }

    @Override
    public TotalBalanceResponse getTotalBalance(Currency currency) {
        var user = provider.get();
        var cards = cardRepository.findByUserId(user.getId());
        var totalBalance = cards.stream()
                .map(card -> exchangeRateService.getRate(card.getCurrency(), currency).multiply(card.getBalance()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TotalBalanceResponse(currency, totalBalance);
    }
}
