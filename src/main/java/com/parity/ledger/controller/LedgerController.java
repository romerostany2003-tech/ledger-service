package com.parity.ledger.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parity.ledger.dto.requests.CreateLedgerTransactionRequest;
import com.parity.ledger.dto.response.AccountBalanceResponse;
import com.parity.ledger.dto.response.CreateLedgerTransactionResult;
import com.parity.ledger.dto.response.LedgerTransactionResponse;
import com.parity.ledger.service.LedgerCreationService;
import com.parity.ledger.service.LedgerQueryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerCreationService ledgerService;
    private final LedgerQueryService ledgerQueryService;

    @PostMapping
    public ResponseEntity<LedgerTransactionResponse> create(
        @Size(max = 255) 
        @RequestHeader("Idempotency-Key") 
        @NotBlank(message = "Idempotency-Key header must not be blank")
        String idempotencyKey,
        @Valid @RequestBody CreateLedgerTransactionRequest request
    ) {
        CreateLedgerTransactionResult result = ledgerService.create(idempotencyKey, request);
        
        HttpStatus status = result.replayed() 
                            ? HttpStatus.OK 
                            : HttpStatus.CREATED;

        return ResponseEntity.status(status).body(result.transaction());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LedgerTransactionResponse> get(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(ledgerQueryService.getTransaction(id));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountBalanceResponse> balance(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(ledgerQueryService.getBalance(id));
    }
}