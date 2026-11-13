package com.parity.ledger.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.parity.ledger.entity.LedgerEntry;
import com.parity.ledger.enums.LedgerDirection;

public record LedgerEntryResponse(
    UUID id, 
    UUID accountId, 
    LedgerDirection direction, 
    BigDecimal amount
) {
    public static LedgerEntryResponse from(LedgerEntry e) {
        return new LedgerEntryResponse(e.getId(), e.getAccountId(), e.getDirection(), e.getAmount());
    }
}