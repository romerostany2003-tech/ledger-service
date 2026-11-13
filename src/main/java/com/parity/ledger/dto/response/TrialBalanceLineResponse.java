package com.parity.ledger.dto.response;

import java.math.BigDecimal;

import com.parity.ledger.enums.CurrencyCode;

public record TrialBalanceLineResponse(
        CurrencyCode currency,
        BigDecimal totalDebits,
        BigDecimal totalCredits,
        boolean balanced
) {}