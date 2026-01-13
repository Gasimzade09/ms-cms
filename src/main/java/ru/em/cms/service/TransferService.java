package ru.em.cms.service;

import org.springframework.data.domain.Pageable;
import ru.em.cms.model.request.CreateTransferRequest;
import ru.em.cms.model.request.GetTransferRequest;
import ru.em.cms.model.response.PageableResponse;
import ru.em.cms.model.response.TransferResponse;

public interface TransferService {

    TransferResponse createTransfer(CreateTransferRequest request);

    PageableResponse<TransferResponse> getTransfers(GetTransferRequest request, Pageable pageable);
}
