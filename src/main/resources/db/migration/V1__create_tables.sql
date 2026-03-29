CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE vendor_category_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_pattern VARCHAR(100) NOT NULL,
    match_type VARCHAR(10) NOT NULL,
    priority INT NOT NULL DEFAULT 1,
    category_id UUID NOT NULL REFERENCES categories(id)
);

CREATE TABLE upload_batches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    filename VARCHAR(255) NOT NULL,
    total_rows INT NOT NULL DEFAULT 0,
    success_rows INT NOT NULL DEFAULT 0,
    failed_rows INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PROCESSING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE expenses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_date DATE NOT NULL,
    amount NUMERIC(10,2) NOT NULL CHECK (amount > 0),
    vendor_name VARCHAR(255) NOT NULL,
    description TEXT,
    source VARCHAR(10) NOT NULL,
    is_anomaly BOOLEAN NOT NULL DEFAULT FALSE,
    category_id UUID NOT NULL REFERENCES categories(id),
    upload_batch_id UUID REFERENCES upload_batches(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_expenses_category ON expenses(category_id);
CREATE INDEX idx_expenses_date ON expenses(expense_date);
CREATE INDEX idx_expenses_anomaly ON expenses(is_anomaly);