package com.parity.ledger.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountBalanceResponse(
        UUID accountId,
        BigDecimal totalDebits,
        BigDecimal totalCredits,
        BigDecimal balance
) { }