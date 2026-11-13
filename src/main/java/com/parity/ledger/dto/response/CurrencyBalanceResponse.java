package com.parity.ledger.dto.response;

import java.math.BigDecimal;

import com.parity.ledger.enums.CurrencyCode;

public record CurrencyBalanceResponse(
        CurrencyCode currency,
        BigDecimal totalDebits,
        BigDecimal totalCredits,
        BigDecimal balance
) {}