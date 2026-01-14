package ru.em.cms.controller;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.em.cms.model.dto.CardDto;
import ru.em.cms.model.request.ChangeCardStatusRequest;
import ru.em.cms.model.request.CreateCardRequest;
import ru.em.cms.model.request.GetCardRequest;
import ru.em.cms.model.response.PageableResponse;
import ru.em.cms.model.response.TotalBalanceResponse;
import ru.em.cms.model.type.Currency;
import ru.em.cms.service.CardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/cards")
public class CardController {
    private final CardService service;

    @PostMapping
    public ResponseEntity<CardDto> createCard(@RequestBody CreateCardRequest request) {
        return ResponseEntity.ok(service.createCard(request));
    }

    @GetMapping
    public ResponseEntity<PageableResponse<CardDto>> getCards(
            @ParameterObject GetCardRequest request,
            @ParameterObject @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getCards(request, pageable));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        service.deleteCard(cardId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping
    public ResponseEntity<CardDto> changeCardStatus(@RequestBody ChangeCardStatusRequest request) {
        return ResponseEntity.ok(service.changeCardStatus(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> requestToBlockCard(@PathVariable Long id) {
        service.requestToBlockCard(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance/{currency}")
    public ResponseEntity<TotalBalanceResponse> getTotalBalance(@PathVariable Currency currency) {
        return ResponseEntity.ok(service.getTotalBalance(currency));
    }

}
