package ru.em.cms.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.em.cms.exception.NotFoundException;
import ru.em.cms.mapper.CardMapper;
import ru.em.cms.model.dto.CardBin;
import ru.em.cms.model.dto.CardDto;
import ru.em.cms.model.dto.NotificationDto;
import ru.em.cms.model.entity.CardEntity;
import ru.em.cms.model.entity.UserEntity;
import ru.em.cms.model.request.ChangeCardStatusRequest;
import ru.em.cms.model.request.CreateCardRequest;
import ru.em.cms.model.request.GetCardRequest;
import ru.em.cms.model.type.Currency;
import ru.em.cms.model.type.Role;
import ru.em.cms.repository.CardRepository;
import ru.em.cms.repository.UserRepository;
import ru.em.cms.service.ExchangeRateService;
import ru.em.cms.service.NotificationService;
import ru.em.cms.specification.CardSpecification;
import ru.em.cms.util.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {
    @Mock
    private CurrentUserProvider provider;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardBin cardBin;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardMapper mapper;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ExchangeRateService exchangeRateService;
    @InjectMocks
    private CardServiceImpl service;
    private EnhancedRandom randomizer = EnhancedRandomBuilder.aNewEnhancedRandom();

    @Test
    void createCard() {
        //given
        var request = randomizer.nextObject(CreateCardRequest.class);
        var user = randomizer.nextObject(UserEntity.class);
        var expected = randomizer.nextObject(CardDto.class);
        var bin = "400000";

        //when
        doReturn(Optional.of(user)).when(userRepository).findById(request.getUserId());
        doReturn(bin).when(cardBin).getBin(request.getType().name());
        doReturn(expected).when(mapper).entityToDto(any());

        //then
        var actual = service.createCard(request);
        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(userRepository, times(1)).findById(request.getUserId());
        verify(cardBin, times(1)).getBin(request.getType().name());
        verify(mapper, times(1)).entityToDto(any());
    }

    @Test
    void createCard_userNotFound() {
        //given
        var request = randomizer.nextObject(CreateCardRequest.class);

        //when
        doReturn(Optional.empty()).when(userRepository).findById(request.getUserId());

        //then
        assertThrows(NotFoundException.class,
                () -> service.createCard(request));

        verify(cardRepository, never()).save(any());
    }

    @Test
    void createCard_duplicateOnce_thenSuccess() {
        //given
        var request = randomizer.nextObject(CreateCardRequest.class);
        var user = randomizer.nextObject(UserEntity.class);
        var bin = "400000";
        var expected = randomizer.nextObject(CardDto.class);

        //when
        doReturn(Optional.of(user)).when(userRepository).findById(request.getUserId());
        doReturn(bin).when(cardBin).getBin(request.getType().name());

        when(cardRepository.save(any(CardEntity.class)))
                .thenThrow(DataIntegrityViolationException.class)
                .thenAnswer(inv -> inv.getArgument(0));
        doReturn(expected).when(mapper).entityToDto(any());

        //then
        CardDto result = service.createCard(request);

        assertNotNull(result);
        verify(cardRepository, times(2)).save(any());
    }

    @Test
    void createCard_allAttemptsFailed() {
        //given
        var request = randomizer.nextObject(CreateCardRequest.class);
        var user = randomizer.nextObject(UserEntity.class);
        var bin = "400000";

        // when
        doReturn(Optional.of(user)).when(userRepository).findById(request.getUserId());
        doReturn(bin).when(cardBin).getBin(request.getType().name());
        doThrow(DataIntegrityViolationException.class).when(cardRepository).save(any(CardEntity.class));

        //then
        assertThrows(IllegalStateException.class, () -> service.createCard(request));

        verify(cardRepository, times(5)).save(any());
    }

    @Test
    void getCards_admin_shouldNotOverrideUserId() {
        // given
        var user = randomizer.nextObject(UserEntity.class);
        var request = new GetCardRequest();
        var card = randomizer.nextObject(CardEntity.class);
        var pageable = Pageable.ofSize(10);
        user.setRole(Role.ADMIN);
        Page<CardEntity> page = new PageImpl<>(List.of(card));

        // when
        doReturn(user).when(provider).get();
        doReturn(page).when(cardRepository).findAll(any(CardSpecification.class), any(Pageable.class));

        // then
        service.getCards(request, pageable);

        assertNull(request.getUserId());
    }

    @Test
    void getCards_user_shouldOverrideUserId() {
        // given
        var request = new GetCardRequest();
        var user = randomizer.nextObject(UserEntity.class);
        var card = randomizer.nextObject(CardEntity.class);
        var pageable = Pageable.ofSize(10);
        user.setRole(Role.USER);
        user.setId(10L);
        Page<CardEntity> page = new PageImpl<>(List.of(card));

        // when
        when(provider.get()).thenReturn(user);
        when(cardRepository.findAll(any(CardSpecification.class), any(Pageable.class))).thenReturn(page);

        // then
        service.getCards(request, pageable);

        assertEquals(10L, request.getUserId());
    }



    @Test
    void getCards() {
        // given
        var request = randomizer.nextObject(GetCardRequest.class);
        var pageReq = Pageable.ofSize(10);
        var user = randomizer.nextObject(UserEntity.class);
        var cardEntity = randomizer.nextObject(CardEntity.class);
        var cardDto = randomizer.nextObject(CardDto.class);
        List<CardEntity> cards = List.of(cardEntity);
        Page<CardEntity> page = new PageImpl<CardEntity>(cards);

        // when
        doReturn(user).when(provider).get();
        doReturn(page).when(cardRepository).findAll(any(CardSpecification.class), any(Pageable.class));
        doReturn(cardDto).when(mapper).entityToDto(cardEntity);

        // then
        var actual = service.getCards(request, pageReq);
        assertNotNull(actual);
    }

    @Test
    void deleteCard() {
        //given
        Long cardId = 1L;
        var card = new CardEntity();

        //when
        doReturn(Optional.of(card)).when(cardRepository).findById(cardId);
        doNothing().when(cardRepository).delete(card);

        //then
        service.deleteCard(cardId);

        verify(cardRepository, times(1)).findById(cardId);
    }

    @Test
    void deleteCard_cardNotFound() {
        //given
        Long cardId = 1L;

        // when
        doReturn(Optional.empty()).when(cardRepository).findById(cardId);

        // then
        var ex = assertThrows(NotFoundException.class, () -> service.deleteCard(cardId));
        assertEquals("card_not_found", ex.getCode());
        assertEquals("Card with id 1 not found", ex.getMessage());
    }

    @Test
    void changeCardStatus() {
        //given

        var req = randomizer.nextObject(ChangeCardStatusRequest.class);
        var card = randomizer.nextObject(CardEntity.class);
        var savedCard = randomizer.nextObject(CardEntity.class);
        var expected = randomizer.nextObject(CardDto.class);

        // when
        doReturn(Optional.of(card)).when(cardRepository).findById(req.getId());
        doReturn(savedCard).when(cardRepository).save(any());
        doReturn(expected).when(mapper).entityToDto(savedCard);

        //then
        var actual = service.changeCardStatus(req);

        assertNotNull(actual);
        assertEquals(actual, expected);

        verify(cardRepository, times(1)).findById(req.getId());
        verify(cardRepository, times(1)).save(any());
        verify(mapper, times(1)).entityToDto(savedCard);

    }

    @Test
    void changeCardStatus_cardNotFound() {
        // given
        var req = randomizer.nextObject(ChangeCardStatusRequest.class);
        String message = String.format("Card with id %s not found", req.getId());
        String code = "card_not_found";

        // when
        doReturn(Optional.empty()).when(cardRepository).findById(req.getId());

        // then
        var ex = assertThrows(NotFoundException.class, () -> service.changeCardStatus(req));
        assertEquals(code, ex.getCode());
        assertEquals(message, ex.getMessage());
    }

    @Test
    void requestToBlockCard() {
        //given
        var id = 1L;
        var user = randomizer.nextObject(UserEntity.class);
        var card = randomizer.nextObject(CardEntity.class);

        //when
        doReturn(user).when(provider).get();
        doReturn(Optional.of(card)).when(cardRepository).findByIdAndUserId(id, user.getId());
        doNothing().when(notificationService).notifyAdmin(any(NotificationDto.class));

        //then
        service.requestToBlockCard(id);

        verify(provider, times(1)).get();
        verify(cardRepository, times(1)).findByIdAndUserId(id, user.getId());
        verify(notificationService, times(1)).notifyAdmin(any(NotificationDto.class));
    }

    @Test
    void requestToBlockCard_cardNotFound() {
        //given
        var id = 1L;
        var user = randomizer.nextObject(UserEntity.class);
        String message = String.format("Card with id %s not found", id);
        String code = "card_not_found";

        //when
        doReturn(user).when(provider).get();
        doReturn(Optional.empty()).when(cardRepository).findByIdAndUserId(id, user.getId());

        //then
        var ex = assertThrows(NotFoundException.class, () -> service.requestToBlockCard(id));
        assertEquals(code, ex.getCode());
        assertEquals(message, ex.getMessage());
        verify(provider, times(1)).get();
        verify(cardRepository, times(1)).findByIdAndUserId(id, user.getId());
        verify(notificationService, times(0)).notifyAdmin(any());
    }

    @Test
    void getTotalBalance() {
        //given
        Currency currency = Currency.EUR;
        var card = randomizer.nextObject(CardEntity.class);
        var cards = List.of(card);
        var rate = BigDecimal.valueOf(1.5);
        var user = randomizer.nextObject(UserEntity.class);
        var expected = rate.multiply(card.getBalance());

        // when
        doReturn(user).when(provider).get();
        doReturn(cards).when(cardRepository).findByUserId(user.getId());
        doReturn(rate).when(exchangeRateService).getRate(card.getCurrency(), currency);

        // then
        var actual = service.getTotalBalance(currency);
        assertEquals(actual.getTotalBalance(), expected);

        verify(provider, times(1)).get();
        verify(cardRepository, times(1)).findByUserId(user.getId());
        verify(exchangeRateService, times(1)).getRate(card.getCurrency(), currency);
    }
}