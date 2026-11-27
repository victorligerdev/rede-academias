SET client_encoding = 'UTF8';
SET client_min_messages = warning;
DROP TABLE IF EXISTS dwf.fato_matricula, dwf.dim_aluno, dwf.dim_unidade,
    dwf.dim_plano, dwf.dim_consultor, dwf.dim_tempo, dwf.dim_categoria,
    dwf.dim_cidade, dwf.dim_estado CASCADE;

CREATE TABLE dwf.dim_estado (
    sk_estado   SERIAL PRIMARY KEY,
    uf          CHAR(2)     NOT NULL UNIQUE,
    nome_estado VARCHAR(40) NOT NULL,
    regiao      VARCHAR(20) NOT NULL
);

CREATE TABLE dwf.dim_cidade (
    sk_cidade   SERIAL PRIMARY KEY,
    nome        VARCHAR(80) NOT NULL,
    sk_estado   INTEGER     NOT NULL REFERENCES dwf.dim_estado,
    UNIQUE (nome, sk_estado)
);

CREATE TABLE dwf.dim_categoria (
    sk_categoria SERIAL PRIMARY KEY,
    nome         VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE dwf.dim_tempo (LIKE dw.dim_tempo INCLUDING ALL);

CREATE TABLE dwf.dim_aluno (
    sk_aluno      INTEGER PRIMARY KEY,
    id_origem     BIGINT       NOT NULL,
    nome          VARCHAR(120) NOT NULL,
    sexo          VARCHAR(15)  NOT NULL,
    faixa_etaria  VARCHAR(20)  NOT NULL,
    bairro        VARCHAR(60)  NOT NULL,
    sk_cidade     INTEGER      NOT NULL REFERENCES dwf.dim_cidade,
    data_cadastro DATE         NOT NULL,
    data_inicio   DATE         NOT NULL,
    data_fim      DATE         NOT NULL,
    atual         BOOLEAN      NOT NULL
);

CREATE TABLE dwf.dim_unidade (
    sk_unidade        INTEGER PRIMARY KEY,
    id_origem         BIGINT      NOT NULL UNIQUE,
    nome              VARCHAR(80) NOT NULL,
    bairro            VARCHAR(60) NOT NULL,
    sk_cidade         INTEGER     NOT NULL REFERENCES dwf.dim_cidade,
    capacidade_alunos INTEGER,
    data_inauguracao  DATE
);

CREATE TABLE dwf.dim_plano (
    sk_plano      INTEGER PRIMARY KEY,
    id_origem     BIGINT        NOT NULL UNIQUE,
    codigo        VARCHAR(20)   NOT NULL,
    nome          VARCHAR(120)  NOT NULL,
    sk_categoria  INTEGER       NOT NULL REFERENCES dwf.dim_categoria,
    pacote        VARCHAR(40)   NOT NULL,
    periodicidade VARCHAR(12)   NOT NULL,
    meses         SMALLINT      NOT NULL,
    valor_mensal  NUMERIC(10,2) NOT NULL,
    ativo         BOOLEAN       NOT NULL
);

CREATE TABLE dwf.dim_consultor (LIKE dw.dim_consultor INCLUDING CONSTRAINTS);
ALTER TABLE dwf.dim_consultor ADD PRIMARY KEY (sk_consultor);

CREATE TABLE dwf.fato_matricula (
    id_matricula          BIGINT PRIMARY KEY,
    sk_tempo              INTEGER  NOT NULL REFERENCES dwf.dim_tempo,
    sk_aluno              INTEGER  NOT NULL REFERENCES dwf.dim_aluno,
    sk_plano              INTEGER  NOT NULL REFERENCES dwf.dim_plano,
    sk_unidade            INTEGER  NOT NULL REFERENCES dwf.dim_unidade,
    sk_consultor          INTEGER  NOT NULL REFERENCES dwf.dim_consultor,
    status                VARCHAR(12)   NOT NULL,
    quantidade            SMALLINT      NOT NULL,
    meses                 SMALLINT      NOT NULL,
    qtd_servicos          SMALLINT      NOT NULL,
    valor_plano           NUMERIC(12,2) NOT NULL,
    valor_servicos        NUMERIC(12,2) NOT NULL,
    valor_bruto           NUMERIC(12,2) NOT NULL,
    desconto              NUMERIC(12,2) NOT NULL,
    valor_liquido         NUMERIC(12,2) NOT NULL,
    valor_mensal_liquido  NUMERIC(12,2) NOT NULL,
    percentual_desconto   NUMERIC(5,2)  NOT NULL
);
CREATE INDEX ix_fatof_tempo   ON dwf.fato_matricula (sk_tempo);
CREATE INDEX ix_fatof_aluno   ON dwf.fato_matricula (sk_aluno);
CREATE INDEX ix_fatof_plano   ON dwf.fato_matricula (sk_plano);
CREATE INDEX ix_fatof_unidade ON dwf.fato_matricula (sk_unidade);
