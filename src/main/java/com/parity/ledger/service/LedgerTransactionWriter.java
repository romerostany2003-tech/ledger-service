package com.parity.ledger.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.parity.ledger.entity.LedgerTransaction;
import com.parity.ledger.repository.LedgerTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LedgerTransactionWriter {

    private final LedgerTransactionRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LedgerTransaction insert(LedgerTransaction transaction) {
        return repository.saveAndFlush(transaction);
    }
}