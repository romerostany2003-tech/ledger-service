package com.parity.ledger.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.parity.ledger.dto.response.AccountBalanceResponse;
import com.parity.ledger.dto.response.LedgerTransactionResponse;
import com.parity.ledger.enums.LedgerDirection;
import com.parity.ledger.exception.TransactionNotFoundException;
import com.parity.ledger.repository.LedgerEntryRepository;
import com.parity.ledger.repository.LedgerTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class LedgerQueryService {

    private final LedgerEntryRepository entryRepository;
    private final LedgerTransactionRepository ledgerTransactionRepository;
    
    @Transactional(readOnly = true)
    public LedgerTransactionResponse getTransaction(UUID id) {
        return ledgerTransactionRepository.findWithEntriesById(id)
                .map(LedgerTransactionResponse::from)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    @Transactional(readOnly = true)   // org.springframework.transaction.annotation
    public AccountBalanceResponse getBalance(UUID accountId) {
        BigDecimal debits = entryRepository.sumByAccountAndDirection(accountId, LedgerDirection.DEBIT);
        BigDecimal credits = entryRepository.sumByAccountAndDirection(accountId, LedgerDirection.CREDIT);
        
        return new AccountBalanceResponse(accountId, debits, credits, credits.subtract(debits));
    }
}
