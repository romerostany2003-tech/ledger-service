package com.parity.ledger.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.parity.ledger.entity.LedgerTransaction;

public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, UUID> {

    @EntityGraph(attributePaths = "entries")
    Optional<LedgerTransaction> findByIdempotencyKey(String idempotencyKey);

    @EntityGraph(attributePaths = "entries")
    Optional<LedgerTransaction> findWithEntriesById(UUID id);
}