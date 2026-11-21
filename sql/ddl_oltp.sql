-- DDL PostgreSQL do banco OLTP da Rede de Academias Vértice.
-- Gerado a partir das entidades JPA do projeto.

CREATE TABLE IF NOT EXISTS pessoa (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    data_nascimento DATE,
    sexo VARCHAR(10),
    email VARCHAR(120),
    telefone VARCHAR(20),
    logradouro VARCHAR(120),
    numero VARCHAR(10),
    bairro VARCHAR(60),
    cidade VARCHAR(80),
    uf VARCHAR(2),
    cep VARCHAR(9)
);

CREATE TABLE IF NOT EXISTS ficha_medica (
    id BIGSERIAL PRIMARY KEY,
    peso_kg NUMERIC(5,1),
    altura_cm INTEGER,
    restricoes VARCHAR(255),
    apto_atividade BOOLEAN NOT NULL,
    data_avaliacao DATE
);

CREATE TABLE IF NOT EXISTS aluno (
    pessoa_id BIGINT PRIMARY KEY REFERENCES pessoa(id),
    data_cadastro DATE NOT NULL,
    ficha_medica_id BIGINT UNIQUE REFERENCES ficha_medica(id)
);

CREATE TABLE IF NOT EXISTS unidade (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(80) NOT NULL UNIQUE,
    cnpj VARCHAR(18),
    capacidade_alunos INTEGER,
    data_inauguracao DATE,
    logradouro VARCHAR(120),
    numero VARCHAR(10),
    bairro VARCHAR(60),
    cidade VARCHAR(80),
    uf VARCHAR(2),
    cep VARCHAR(9)
);

CREATE TABLE IF NOT EXISTS funcionario (
    pessoa_id BIGINT PRIMARY KEY REFERENCES pessoa(id),
    cargo VARCHAR(20) NOT NULL,
    salario NUMERIC(10,2),
    data_admissao DATE,
    unidade_id BIGINT REFERENCES unidade(id)
);

CREATE TABLE IF NOT EXISTS categoria_plano (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(40) NOT NULL UNIQUE,
    descricao VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS modalidade (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(60) NOT NULL UNIQUE,
    descricao VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS plano (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nome VARCHAR(120) NOT NULL,
    periodicidade VARCHAR(12) NOT NULL,
    valor_mensal NUMERIC(10,2) NOT NULL,
    ativo BOOLEAN NOT NULL,
    categoria_id BIGINT NOT NULL REFERENCES categoria_plano(id)
);

CREATE TABLE IF NOT EXISTS plano_modalidade (
    plano_id BIGINT NOT NULL REFERENCES plano(id),
    modalidade_id BIGINT NOT NULL REFERENCES modalidade(id),
    PRIMARY KEY (plano_id, modalidade_id)
);

CREATE TABLE IF NOT EXISTS servico_adicional (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(60) NOT NULL UNIQUE,
    valor_mensal NUMERIC(10,2) NOT NULL,
    ativo BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS matricula (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL REFERENCES aluno(pessoa_id),
    plano_id BIGINT NOT NULL REFERENCES plano(id),
    unidade_id BIGINT NOT NULL REFERENCES unidade(id),
    consultor_id BIGINT REFERENCES funcionario(pessoa_id),
    data_matricula DATE NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    status VARCHAR(12) NOT NULL,
    valor_bruto NUMERIC(10,2) NOT NULL,
    desconto NUMERIC(10,2),
    valor_liquido NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS matricula_servico (
    matricula_id BIGINT NOT NULL REFERENCES matricula(id),
    servico_id BIGINT NOT NULL REFERENCES servico_adicional(id),
    PRIMARY KEY (matricula_id, servico_id)
);

CREATE TABLE IF NOT EXISTS pagamento (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL REFERENCES matricula(id),
    numero_parcela INTEGER NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    valor NUMERIC(10,2) NOT NULL,
    forma_pagamento VARCHAR(15) NOT NULL,
    status VARCHAR(10) NOT NULL,
    registrado_em TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_matricula_aluno ON matricula(aluno_id);
CREATE INDEX IF NOT EXISTS idx_matricula_plano ON matricula(plano_id);
CREATE INDEX IF NOT EXISTS idx_matricula_unidade ON matricula(unidade_id);
CREATE INDEX IF NOT EXISTS idx_pagamento_matricula ON pagamento(matricula_id);
CREATE INDEX IF NOT EXISTS idx_pagamento_vencimento ON pagamento(data_vencimento);
