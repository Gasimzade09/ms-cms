package ru.em.cms.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.em.cms.exception.NotEnoughBalanceException;
import ru.em.cms.exception.NotFoundException;
import ru.em.cms.mapper.TransferMapper;
import ru.em.cms.model.entity.CardEntity;
import ru.em.cms.model.entity.TransferEntity;
import ru.em.cms.model.request.CreateTransferRequest;
import ru.em.cms.model.request.GetTransferRequest;
import ru.em.cms.model.response.PageableResponse;
import ru.em.cms.model.response.TransferResponse;
import ru.em.cms.model.type.Role;
import ru.em.cms.model.type.Status;
import ru.em.cms.repository.CardRepository;
import ru.em.cms.repository.TransferRepository;
import ru.em.cms.service.ExchangeRateService;
import ru.em.cms.service.TransferService;
import ru.em.cms.specification.TransferSpecification;
import ru.em.cms.util.CurrentUserProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final CurrentUserProvider provider;
    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;
    private final TransferMapper mapper;
    private final ExchangeRateService exchangeRateService;

    @Override
    @Transactional
    public TransferResponse createTransfer(CreateTransferRequest request) {
        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        var user = provider.get();
        if (request.getFromCard().equals(request.getToCard())) {
            throw new IllegalArgumentException("Cannot transfer to the same card");
        }
        CardEntity fromCard = cardRepository
                .findByIdAndUserIdAndStatus(request.getFromCard(), user.getId(), Status.ACTIVE)
                .orElseThrow(() -> new NotFoundException("card_not_found", "No active from card found"));
        CardEntity toCard = cardRepository
                .findByIdAndUserIdAndStatus(request.getToCard(), user.getId(), Status.ACTIVE)
                .orElseThrow(() -> new NotFoundException("card_not_found", "No active to card found"));
        checkBalance(fromCard, request.getAmount());
        BigDecimal rate = exchangeRateService.getRate(fromCard.getCurrency(), toCard.getCurrency());
        BigDecimal convertedAmount = request.getAmount().multiply(rate).setScale(4, RoundingMode.HALF_UP);

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));

        toCard.setBalance(toCard.getBalance().add(convertedAmount));
        TransferEntity transfer = mapper.requestToEntity(request, fromCard, toCard, rate, convertedAmount);
        transferRepository.save(transfer);
        return mapper.entityToResponse(transfer);
    }

    @Override
    public PageableResponse<TransferResponse> getTransfers(GetTransferRequest request, Pageable pageable) {
        var user = provider.get();
        if (!Role.ADMIN.equals(user.getRole())) {
            request.setUserId(user.getId());
        }
        var spec = new TransferSpecification(request);
        var page = transferRepository.findAll(spec, pageable);
        var pagedResponse = page.map(mapper::entityToResponse);
        return PageableResponse.of(pagedResponse);
    }

    private void checkBalance(CardEntity card, BigDecimal amount) {
        if (card.getBalance().compareTo(amount) < 0) {
            throw new NotEnoughBalanceException("insufficient_balance", "Insufficient balance on card");
        }
    }
}
