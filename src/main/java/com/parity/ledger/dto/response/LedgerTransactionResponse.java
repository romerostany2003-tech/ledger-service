package com.parity.ledger.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.parity.ledger.entity.LedgerTransaction;
import com.parity.ledger.enums.CurrencyCode;

public record LedgerTransactionResponse(
    UUID id, 
    CurrencyCode currency, 
    String reference,
    String description, 
    Instant createdAt,
    List<LedgerEntryResponse> entries
) {
    public static LedgerTransactionResponse from(LedgerTransaction t) {
        return new LedgerTransactionResponse(
                t.getId(), t.getCurrency(), t.getReference(), t.getDescription(), t.getCreatedAt(),
                t.getEntries().stream().map(LedgerEntryResponse::from).toList()
            );
    }
}