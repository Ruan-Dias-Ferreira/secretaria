CREATE TABLE IF NOT EXISTS usuario(
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL
    );

CREATE TABLE IF NOT EXISTS aluno(
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    rg VARCHAR(255) NOT NULL UNIQUE,
    cpf VARCHAR(255) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(255) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    nome_mae VARCHAR(255),
    nome_pai VARCHAR(255),
    usuario_id BIGINT NOT NULL REFERENCES usuario(id)
    );

CREATE TABLE IF NOT EXISTS turma(
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    ano_letivo INTEGER NOT NULL,
    turno VARCHAR(255) NOT NULL,
    curso VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS disciplina(
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    carga_horaria INTEGER NOT NULL,
    turma_id BIGINT NOT NULL REFERENCES turma(id)
    );

CREATE TABLE IF NOT EXISTS matricula(
    id BIGSERIAL PRIMARY KEY,
    ano_letivo INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    aluno_id BIGINT NOT NULL REFERENCES aluno(id),
    turma_id BIGINT NOT NULL REFERENCES turma(id)
    );

CREATE TABLE IF NOT EXISTS nota(
    id BIGSERIAL PRIMARY KEY,
    bimestre INTEGER NOT NULL,
    valor NUMERIC(4,2) NOT NULL,
    situacao VARCHAR(30) NOT NULL,
    aluno_id BIGINT NOT NULL REFERENCES aluno(id),
    disciplina_id BIGINT NOT NULL REFERENCES disciplina(id)
    );

CREATE TABLE IF NOT EXISTS frequencia(
    id BIGSERIAL PRIMARY KEY,
    data DATE NOT NULL,
    presente BOOLEAN NOT NULL,
    aluno_id BIGINT NOT NULL REFERENCES aluno(id),
    disciplina_id BIGINT NOT NULL REFERENCES disciplina(id)
    );

CREATE TABLE IF NOT EXISTS documento(
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(30) NOT NULL,
    data_emissao DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    arquivo VARCHAR(255),
    aluno_id BIGINT NOT NULL REFERENCES aluno(id)
    );