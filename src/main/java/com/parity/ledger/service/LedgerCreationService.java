package com.parity.ledger.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.parity.ledger.dto.requests.CreateLedgerTransactionRequest;
import com.parity.ledger.dto.requests.LedgerEntryRequest;
import com.parity.ledger.dto.response.CreateLedgerTransactionResult;
import com.parity.ledger.dto.response.LedgerTransactionResponse;
import com.parity.ledger.entity.LedgerEntry;
import com.parity.ledger.entity.LedgerTransaction;
import com.parity.ledger.exception.IdempotencyConflictException;
import com.parity.ledger.repository.LedgerTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LedgerCreationService {

    private final LedgerTransactionWriter transactionWriter;
    private final LedgerTransactionRepository ledgerTransactionRepository;

    public CreateLedgerTransactionResult create(String idempotencyKey, CreateLedgerTransactionRequest request){

        String hash = fingerprint(request);

        var existing = ledgerTransactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent())
            return replay(existing.get(), hash, idempotencyKey);

        LedgerTransaction transaction = buildTransaction(
                idempotencyKey, hash, request);

        try {
            LedgerTransaction saved = transactionWriter.insert(transaction);

            return new CreateLedgerTransactionResult(
                LedgerTransactionResponse.from(saved),false
            );
        } catch (DataIntegrityViolationException exception) {
            LedgerTransaction winner = ledgerTransactionRepository
                    .findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> exception);

            return replay(winner, hash, idempotencyKey);
        }
    }

    private CreateLedgerTransactionResult replay(
            LedgerTransaction transaction,
            String requestHash,
            String idempotencyKey) {

        if (!transaction.getRequestHash().equals(requestHash)) {
            throw new IdempotencyConflictException(idempotencyKey);
        }

        return new CreateLedgerTransactionResult(
                LedgerTransactionResponse.from(transaction),
                true
        );
    }

    private LedgerTransaction buildTransaction(
            String idempotencyKey,
            String hash,
            CreateLedgerTransactionRequest request) {

        List<LedgerEntry> entries = request.entries().stream()
                .map(entry -> LedgerEntry.of(
                        entry.accountId(),
                        entry.direction(),
                        entry.amount()))
                .toList();

        return LedgerTransaction.create(
                idempotencyKey,
                hash,
                request.currency(),
                request.reference(),
                request.description(),
                entries);
    }

    private static String fingerprint(CreateLedgerTransactionRequest r) {
        StringBuilder canonical = new StringBuilder();
        appendField(canonical, r.currency().name());
        appendField(canonical, r.reference());
        appendField(canonical, r.description());
        appendField(canonical, Integer.toString(r.entries().size()));

        r.entries().stream()
            .sorted(Comparator
                .comparing((LedgerEntryRequest e) -> e.accountId().toString())
                .thenComparing(e -> e.direction().name())
                .thenComparing(e -> e.amount().stripTrailingZeros().toPlainString()))
            .forEach(entry -> {
                appendField(canonical, entry.accountId().toString());
                appendField(canonical, entry.direction().name());
                appendField(canonical, entry.amount().stripTrailingZeros().toPlainString());
            });
    
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(canonical.toString().getBytes(StandardCharsets.UTF_8));
        
            return HexFormat.of().formatHex(digest);
        
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void appendField(StringBuilder target, String value) {
        if (value == null) {
            target.append("-1:");
            return;
        }
        target.append(value.length()).append(':').append(value);
    }
}