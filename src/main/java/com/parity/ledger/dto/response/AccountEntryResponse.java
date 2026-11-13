package com.parity.ledger.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.parity.ledger.entity.LedgerEntry;
import com.parity.ledger.enums.CurrencyCode;
import com.parity.ledger.enums.LedgerDirection;

public record AccountEntryResponse(
        UUID id,
        UUID transactionId,
        UUID accountId,
        CurrencyCode currency,
        LedgerDirection direction,
        BigDecimal amount,
        Instant createdAt
) {
    public static AccountEntryResponse from(LedgerEntry entry) {
        return new AccountEntryResponse(
                entry.getId(),
                entry.getTransaction().getId(),
                entry.getAccountId(),
                entry.getTransaction().getCurrency(),
                entry.getDirection(),
                entry.getAmount(),
                entry.getCreatedAt());
    }
}
