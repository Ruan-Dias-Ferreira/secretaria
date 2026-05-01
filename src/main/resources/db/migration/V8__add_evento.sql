CREATE TABLE evento (
    id BIGSERIAL PRIMARY KEY,
    data DATE NOT NULL UNIQUE,
    tipo VARCHAR(32) NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT
);
