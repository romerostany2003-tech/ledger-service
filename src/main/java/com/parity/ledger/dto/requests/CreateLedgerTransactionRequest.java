package com.parity.ledger.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import com.parity.ledger.enums.CurrencyCode;

public record CreateLedgerTransactionRequest(
        @NotNull CurrencyCode currency,
        @Size(max = 255) String reference,
        @Size(max = 500) String description,
        @NotNull @Size(min = 2, message = "A transaction needs at least two entries")
        List<@NotNull @Valid LedgerEntryRequest> entries
) {}