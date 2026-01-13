package ru.em.cms.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.em.cms.exception.NotEnoughBalanceException;
import ru.em.cms.exception.NotFoundException;
import ru.em.cms.mapper.TransferMapper;
import ru.em.cms.model.entity.CardEntity;
import ru.em.cms.model.entity.TransferEntity;
import ru.em.cms.model.entity.UserEntity;
import ru.em.cms.model.request.CreateTransferRequest;
import ru.em.cms.model.request.GetTransferRequest;
import ru.em.cms.model.response.TransferResponse;
import ru.em.cms.model.type.Currency;
import ru.em.cms.model.type.Status;
import ru.em.cms.repository.CardRepository;
import ru.em.cms.repository.TransferRepository;
import ru.em.cms.service.ExchangeRateService;
import ru.em.cms.specification.TransferSpecification;
import ru.em.cms.util.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {
    @Mock
    private CurrentUserProvider provider;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private TransferMapper mapper;
    @Mock
    private ExchangeRateService exchangeRateService;
    @InjectMocks
    private TransferServiceImpl service;
    private EnhancedRandom randomizer = EnhancedRandomBuilder.aNewEnhancedRandom();

    @Test
    void createTransfer_amountIsNull() {
        // given
        var request = randomizer.nextObject(CreateTransferRequest.class);
        request.setAmount(null);

        // then
        var ex = assertThrows(IllegalArgumentException.class, () -> service.createTransfer(request));
        assertEquals(ex.getMessage(), "Amount must be positive");

    }

    @Test
    void createTransfer_amountIsZero() {
        // given
        var request = randomizer.nextObject(CreateTransferRequest.class);
        request.setAmount(BigDecimal.ZERO);

        // then
        var ex = assertThrows(IllegalArgumentException.class, () -> service.createTransfer(request));
        assertEquals(ex.getMessage(), "Amount must be positive");

    }

    @Test
    void createTransfer_sameCard_shouldThrow() {
        // given
        var request = randomizer.nextObject(CreateTransferRequest.class);
        request.setFromCard(request.getToCard());
        var user = randomizer.nextObject(UserEntity.class);

        //when
        doReturn(user).when(provider).get();


        // then
        var ex = assertThrows(IllegalArgumentException.class, () -> service.createTransfer(request));
        assertEquals(ex.getMessage(), "Cannot transfer to the same card");

        verify(provider, times(1)).get();
        verify(cardRepository, times(0)).findByIdAndUserIdAndStatus(request.getToCard(), user.getId(), Status.ACTIVE);
        verify(cardRepository, times(0)).findByIdAndUserIdAndStatus(request.getFromCard(), user.getId(), Status.ACTIVE);
    }

    @Test
    void createTransfer_fromCardNotFound() {
        //given
        var request = randomizer.nextObject(CreateTransferRequest.class);
        var user = randomizer.nextObject(UserEntity.class);
        var code = "card_not_found";
        var message = "No active from card found";

        //when
        doReturn(user).when(provider).get();
        doReturn(Optional.empty()).when(cardRepository)
                .findByIdAndUserIdAndStatus(request.getFromCard(), user.getId(), Status.ACTIVE);

        // then
        var ex = assertThrows(NotFoundException.class, () -> service.createTransfer(request));

        assertEquals(code, ex.getCode());
        assertEquals(message, ex.getMessage());
        verify(provider, times(1)).get();
        verify(cardRepository, times(0)).findByIdAndUserIdAndStatus(request.getToCard(), user.getId(), Status.ACTIVE);
        verify(cardRepository, times(1)).findByIdAndUserIdAndStatus(request.getFromCard(), user.getId(), Status.ACTIVE);
    }

    @Test
    void createTransfer_toCardNotFound() {
        //given
        var request = randomizer.nextObject(CreateTransferRequest.class);
        var user = randomizer.nextObject(UserEntity.class);
        var card = randomizer.nextObject(CardEntity.class);
        var code = "card_not_found";
        var message = "No active to card found";

        //when
        doReturn(user).when(provider).get();
        doReturn(Optional.of(card)).when(cardRepository)
                .findByIdAndUserIdAndStatus(request.getFromCard(), user.getId(), Status.ACTIVE);

        doReturn(Optional.empty()).when(cardRepository)
                .findByIdAndUserIdAndStatus(request.getToCard(), user.getId(), Status.ACTIVE);


        // then
        var ex = assertThrows(NotFoundException.class, () -> service.createTransfer(request));

        assertEquals(code, ex.getCode());
        assertEquals(message, ex.getMessage());
        verify(provider, times(1)).get();
        verify(cardRepository, times(1)).findByIdAndUserIdAndStatus(request.getToCard(), user.getId(), Status.ACTIVE);
        verify(cardRepository, times(1)).findByIdAndUserIdAndStatus(request.getFromCard(), user.getId(), Status.ACTIVE);
    }

    @Test
    void createTransfer_insufficientBalance() {
        //given
        var request = randomizer.nextObject(CreateTransferRequest.class);
        request.setAmount(BigDecimal.TEN);
        var user = randomizer.nextObject(UserEntity.class);
        var fromCard = randomizer.nextObject(CardEntity.class);
        fromCard.setBalance(BigDecimal.ONE);
        var toCard = randomizer.nextObject(CardEntity.class);
        var code = "insufficient_balance";
        var message = "Insufficient balance on card";

        //when
        doReturn(user).when(provider).get();
        doReturn(Optional.of(fromCard)).when(cardRepository)
                .findByIdAndUserIdAndStatus(request.getFromCard(), user.getId(), Status.ACTIVE);

        doReturn(Optional.of(toCard)).when(cardRepository)
                .findByIdAndUserIdAndStatus(request.getToCard(), user.getId(), Status.ACTIVE);

        //then
        var ex = assertThrows(NotEnoughBalanceException.class, () -> service.createTransfer(request));

        assertEquals(ex.getCode(), code);
        assertEquals(ex.getMessage(), message);
        verify(provider, times(1)).get();
        verify(cardRepository, times(1)).findByIdAndUserIdAndStatus(request.getToCard(), user.getId(), Status.ACTIVE);
        verify(cardRepository, times(1)).findByIdAndUserIdAndStatus(request.getFromCard(), user.getId(), Status.ACTIVE);
    }

    @Test
    void createTransfer_success() {
        //given
        var request = randomizer.nextObject(CreateTransferRequest.class);
        var user = randomizer.nextObject(UserEntity.class);
        var fromCard = randomizer.nextObject(CardEntity.class);
        fromCard.setBalance(fromCard.getBalance().add(request.getAmount()));
        var toCard = randomizer.nextObject(CardEntity.class);
        var transfer = randomizer.nextObject(TransferEntity.class);
        var expected = randomizer.nextObject(TransferResponse.class);
        var rate = new BigDecimal("0.9");

        //when
        doReturn(user).when(provider).get();
        doReturn(Optional.of(fromCard)).when(cardRepository).findByIdAndUserIdAndStatus(request.getFromCard(), user.getId(), Status.ACTIVE);
        doReturn(Optional.of(toCard)).when(cardRepository).findByIdAndUserIdAndStatus(request.getToCard(), user.getId(), Status.ACTIVE);
        doReturn(rate).when(exchangeRateService).getRate(fromCard.getCurrency(), toCard.getCurrency());
        doReturn(transfer).when(transferRepository).save(transfer);
        doReturn(transfer).when(mapper).requestToEntity(any(), any(), any(), any(), any());
        doReturn(expected).when(mapper).entityToResponse(transfer);

        //then
        TransferResponse actual = service.createTransfer(request);

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(provider, times(1)).get();
        verify(cardRepository, times(1)).findByIdAndUserIdAndStatus(request.getToCard(), user.getId(), Status.ACTIVE);
        verify(cardRepository, times(1)).findByIdAndUserIdAndStatus(request.getFromCard(), user.getId(), Status.ACTIVE);
        verify(exchangeRateService, times(1)).getRate(fromCard.getCurrency(), toCard.getCurrency());
        verify(mapper, times(1)).requestToEntity(any(), any(), any(), any(), any());
        verify(mapper, times(1)).entityToResponse(transfer);
        verify(transferRepository, times(1)).save(transfer);

    }

    @Test
    void getTransfers() {
        // given
        var request = randomizer.nextObject(GetTransferRequest.class);
        var pageReq = Pageable.ofSize(10);
        var user = randomizer.nextObject(UserEntity.class);
        var transferEntity = randomizer.nextObject(TransferEntity.class);
        var transferResponse = randomizer.nextObject(TransferResponse.class);
        Page<TransferEntity> page = new PageImpl<>(List.of(transferEntity));

        // when
        doReturn(user).when(provider).get();
        doReturn(page).when(transferRepository).findAll(any(TransferSpecification.class), any(Pageable.class));
        doReturn(transferResponse).when(mapper).entityToResponse(transferEntity);

        // then
        var actual = service.getTransfers(request, pageReq);

        assertNotNull(actual);
        assertEquals(actual.getData().getFirst(), transferResponse);

        verify(provider, times(1)).get();
        verify(transferRepository, times(1)).findAll(any(TransferSpecification.class), any(Pageable.class));
        verify(mapper, times(1)).entityToResponse(transferEntity);
    }
}