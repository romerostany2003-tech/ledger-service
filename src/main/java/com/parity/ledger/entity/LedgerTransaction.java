package com.parity.ledger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.parity.ledger.enums.CurrencyCode;
import com.parity.ledger.enums.LedgerDirection;

@Entity
@Table(name = "ledger_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, updatable = false, length = 255)
    private String idempotencyKey;

    @Column(name = "request_hash", nullable = false, updatable = false, length = 64)
    private String requestHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "currency", nullable = false, updatable = false, length = 3)
    private CurrencyCode currency;

    @Column(name = "reference", updatable = false, length = 255)
    private String reference;

    @Column(name = "description", updatable = false, length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.PERSIST)
    private List<LedgerEntry> entries = new ArrayList<>();

    public static LedgerTransaction create(String idempotencyKey, String requestHash,
                                           CurrencyCode currency, String reference,
                                           String description, List<LedgerEntry> entries) {
        if (entries == null || entries.size() < 2) {
            throw new IllegalArgumentException("A transaction needs at least two entries");
        }
        BigDecimal debits = total(entries, LedgerDirection.DEBIT);
        BigDecimal credits = total(entries, LedgerDirection.CREDIT);
        if (debits.compareTo(credits) != 0) {
            throw new IllegalArgumentException("Debits must equal credits");
        }

        LedgerTransaction tx = new LedgerTransaction();
        tx.idempotencyKey = idempotencyKey;
        tx.requestHash = requestHash;
        tx.currency = currency;
        tx.reference = reference;
        tx.description = description;
        entries.forEach(e -> {
            e.attachTo(tx);
            tx.entries.add(e);
        });
        return tx;
    }

    public List<LedgerEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    private static BigDecimal total(List<LedgerEntry> entries, LedgerDirection direction) {
        return entries.stream()
                .filter(e -> e.getDirection() == direction)
                .map(LedgerEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}