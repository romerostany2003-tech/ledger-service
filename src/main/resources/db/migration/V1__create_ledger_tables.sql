CREATE TABLE ledger_transactions (
    id               BINARY(16)    NOT NULL,
    idempotency_key  VARCHAR(255)  NOT NULL,
    request_hash     CHAR(64)      NOT NULL,
    currency         CHAR(3)       NOT NULL,
    reference        VARCHAR(255)  NULL,
    description      VARCHAR(500)  NULL,
    created_at       DATETIME(6)   NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_ledger_transaction_idempotency UNIQUE (idempotency_key)
);

CREATE TABLE ledger_entries (
    id              BINARY(16)     NOT NULL,
    transaction_id  BINARY(16)     NOT NULL,
    account_id      BINARY(16)     NOT NULL,
    direction       VARCHAR(6)     NOT NULL,
    amount          DECIMAL(19,4)  NOT NULL,
    created_at      DATETIME(6)    NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_entry_transaction FOREIGN KEY (transaction_id) REFERENCES ledger_transactions (id),
    CONSTRAINT chk_entry_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_entry_direction CHECK (direction IN ('DEBIT', 'CREDIT')),
    INDEX idx_entry_account_history (account_id, created_at, id),
    INDEX idx_entry_transaction (transaction_id)
);