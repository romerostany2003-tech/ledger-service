package com.parity.ledger.dto.response;

public record CreateLedgerTransactionResult(
        LedgerTransactionResponse transaction,
        boolean replayed
) {}