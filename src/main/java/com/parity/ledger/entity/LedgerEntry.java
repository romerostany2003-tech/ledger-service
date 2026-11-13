package com.parity.ledger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.parity.ledger.enums.LedgerDirection;

@Entity
@Table(name = "ledger_entries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, updatable = false)
    private LedgerTransaction transaction;

    @Column(name = "account_id", nullable = false, updatable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, updatable = false, length = 6)
    private LedgerDirection direction;

    @Column(name = "amount", nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static LedgerEntry of(UUID accountId, LedgerDirection direction, BigDecimal amount) {
        if (accountId == null || direction == null) {
            throw new IllegalArgumentException("Account and direction are required");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        LedgerEntry e = new LedgerEntry();
        e.accountId = accountId;
        e.direction = direction;
        e.amount = amount;
        return e;
    }

    void attachTo(LedgerTransaction transaction) {
        this.transaction = transaction;
    }
}