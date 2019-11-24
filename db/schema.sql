CREATE TABLE IF NOT EXISTS payments
(
    id                   VARCHAR(32),
    client_id            VARCHAR(32),
    date                 DATE,
    currency             VARCHAR(3),
    total_amount         DECIMAL(30, 2),
    total_discounts      DECIMAL(30, 2),
    total_with_discounts DECIMAL(30, 2),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS discounts
(
    id         VARCHAR(32),
    amount     DECIMAL(30, 2),
    type       VARCHAR(12),
    payment_id VARCHAR(32),
    PRIMARY KEY (id),
    FOREIGN KEY (payment_id) REFERENCES payments (id)
);


CREATE TABLE IF NOT EXISTS transactions
(
    id         VARCHAR(32),
    amount     DECIMAL(30, 2),
    type       VARCHAR(12),
    payment_id VARCHAR(32),
    PRIMARY KEY (id),
    FOREIGN KEY (payment_id) REFERENCES payments (id)
);

