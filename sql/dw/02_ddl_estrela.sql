SET client_encoding = 'UTF8';
SET client_min_messages = warning;

DROP TABLE IF EXISTS dw.ponte_matricula_servico, dw.fato_matricula,
    dw.dim_tempo, dw.dim_aluno, dw.dim_plano, dw.dim_unidade,
    dw.dim_consultor, dw.dim_servico CASCADE;

CREATE TABLE dw.dim_tempo (
    sk_tempo        INTEGER PRIMARY KEY,
    data            DATE        NOT NULL UNIQUE,
    dia             SMALLINT    NOT NULL,
    mes             SMALLINT    NOT NULL,
    nome_mes        VARCHAR(15) NOT NULL,
    trimestre       SMALLINT    NOT NULL,
    semestre        SMALLINT    NOT NULL,
    ano             SMALLINT    NOT NULL,
    ano_mes         CHAR(7)     NOT NULL,
    dia_semana      VARCHAR(15) NOT NULL,
    num_dia_semana  SMALLINT    NOT NULL,
    fim_de_semana   BOOLEAN     NOT NULL
);

CREATE TABLE dw.dim_aluno (
    sk_aluno        SERIAL PRIMARY KEY,
    id_origem       BIGINT       NOT NULL,
    nome            VARCHAR(120) NOT NULL,
    sexo            VARCHAR(15)  NOT NULL,
    faixa_etaria    VARCHAR(20)  NOT NULL,
    bairro          VARCHAR(60)  NOT NULL,
    cidade          VARCHAR(80)  NOT NULL,
    uf              CHAR(2)      NOT NULL,
    data_cadastro   DATE         NOT NULL,
    data_inicio     DATE         NOT NULL,
    data_fim        DATE         NOT NULL DEFAULT DATE '9999-12-31',
    atual           BOOLEAN      NOT NULL DEFAULT TRUE
);
CREATE UNIQUE INDEX ux_dim_aluno_atual ON dw.dim_aluno (id_origem) WHERE atual;
CREATE INDEX ix_dim_aluno_origem ON dw.dim_aluno (id_origem, data_inicio, data_fim);

CREATE TABLE dw.dim_plano (
    sk_plano        SERIAL PRIMARY KEY,
    id_origem       BIGINT       NOT NULL UNIQUE,
    codigo          VARCHAR(20)  NOT NULL,
    nome            VARCHAR(120) NOT NULL,
    categoria       VARCHAR(40)  NOT NULL,
    pacote          VARCHAR(40)  NOT NULL,
    periodicidade   VARCHAR(12)  NOT NULL,
    meses           SMALLINT     NOT NULL,
    valor_mensal    NUMERIC(10,2) NOT NULL,
    ativo           BOOLEAN      NOT NULL
);

CREATE TABLE dw.dim_unidade (
    sk_unidade        SERIAL PRIMARY KEY,
    id_origem         BIGINT      NOT NULL UNIQUE,
    nome              VARCHAR(80) NOT NULL,
    bairro            VARCHAR(60) NOT NULL,
    cidade            VARCHAR(80) NOT NULL,
    uf                CHAR(2)     NOT NULL,
    capacidade_alunos INTEGER,
    data_inauguracao  DATE
);

CREATE TABLE dw.dim_consultor (
    sk_consultor    SERIAL PRIMARY KEY,
    id_origem       BIGINT UNIQUE,
    nome            VARCHAR(120) NOT NULL,
    cargo           VARCHAR(20)  NOT NULL,
    unidade_lotacao VARCHAR(80)  NOT NULL
);
INSERT INTO dw.dim_consultor (sk_consultor, id_origem, nome, cargo, unidade_lotacao)
VALUES (-1, NULL, 'Não informado', 'Não informado', 'Não informado');

CREATE TABLE dw.dim_servico (
    sk_servico      SERIAL PRIMARY KEY,
    id_origem       BIGINT        NOT NULL UNIQUE,
    nome            VARCHAR(60)   NOT NULL,
    valor_mensal    NUMERIC(10,2) NOT NULL
);

CREATE TABLE dw.fato_matricula (
    id_matricula          BIGINT PRIMARY KEY,
    sk_tempo              INTEGER  NOT NULL REFERENCES dw.dim_tempo,
    sk_aluno              INTEGER  NOT NULL REFERENCES dw.dim_aluno,
    sk_plano              INTEGER  NOT NULL REFERENCES dw.dim_plano,
    sk_unidade            INTEGER  NOT NULL REFERENCES dw.dim_unidade,
    sk_consultor          INTEGER  NOT NULL REFERENCES dw.dim_consultor,
    status                VARCHAR(12) NOT NULL,
    quantidade            SMALLINT      NOT NULL DEFAULT 1,
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
CREATE INDEX ix_fato_tempo   ON dw.fato_matricula (sk_tempo);
CREATE INDEX ix_fato_aluno   ON dw.fato_matricula (sk_aluno);
CREATE INDEX ix_fato_plano   ON dw.fato_matricula (sk_plano);
CREATE INDEX ix_fato_unidade ON dw.fato_matricula (sk_unidade);

CREATE TABLE dw.ponte_matricula_servico (
    id_matricula BIGINT  NOT NULL REFERENCES dw.fato_matricula ON DELETE CASCADE,
    sk_servico   INTEGER NOT NULL REFERENCES dw.dim_servico,
    PRIMARY KEY (id_matricula, sk_servico)
);
