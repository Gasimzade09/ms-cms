package ru.em.cms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.em.cms.model.request.CreateTransferRequest;
import ru.em.cms.model.request.GetTransferRequest;
import ru.em.cms.model.response.PageableResponse;
import ru.em.cms.model.response.TransferResponse;
import ru.em.cms.service.TransferService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/transfer")
public class TransferController {
    private final TransferService service;

    @PostMapping
    public ResponseEntity<TransferResponse> createTransfer(@RequestBody CreateTransferRequest request) {
        return ResponseEntity.ok(service.createTransfer(request));
    }

    @GetMapping
    public ResponseEntity<PageableResponse<TransferResponse>> getTransfers(
            GetTransferRequest request,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getTransfers(request, pageable));
    }
}
