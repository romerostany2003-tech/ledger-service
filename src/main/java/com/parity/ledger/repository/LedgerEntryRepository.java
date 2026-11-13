package com.parity.ledger.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.parity.ledger.entity.LedgerEntry;
import com.parity.ledger.enums.LedgerDirection;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {

    //Page<LedgerEntry> findByAccountIdOrderByCreatedAtDescIdDesc(UUID accountId, Pageable pageable);

    @Query("select coalesce(sum(e.amount), 0) from LedgerEntry e " +
           "where e.accountId = :accountId and e.direction = :direction")
    BigDecimal sumByAccountAndDirection(UUID accountId, LedgerDirection direction);

    @Query("select coalesce(sum(e.amount), 0) from LedgerEntry e where e.direction = :direction")
    BigDecimal sumByDirection(LedgerDirection direction);
}