DROP TABLE IF EXISTS product, contract, revenue_recognition;

CREATE TABLE IF NOT EXISTS product (
    id   SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS contract (
    id         SERIAL NOT NULL PRIMARY KEY,
    product    INT REFERENCES product (id) NOT NULL,
    revenue    NUMERIC(15,6),
    dateSigned DATE
);

CREATE TABLE IF NOT EXISTS revenue_recognition (
    contract     INT REFERENCES contract (id) NOT NULL,
    amount       NUMERIC(15,6),
    recognizedOn DATE,
    PRIMARY KEY (contract, recognizedOn)
);
