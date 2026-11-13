package com.parity.ledger.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

import com.parity.ledger.enums.LedgerDirection;

public record LedgerEntryRequest(
        @NotNull UUID accountId,
        @NotNull LedgerDirection direction,
        @NotNull @DecimalMin("0.01")
        @Digits(integer = 15, fraction = 2)
        BigDecimal amount
) { }