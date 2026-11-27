# Modelo floco de neve (schema `dwf`)

Mesmo assunto e mesma granularidade da estrela. Três dimensões foram normalizadas:

- Aluno → Cidade → Estado
- Unidade → Cidade → Estado (Cidade e Estado são compartilhadas)
- Plano → Categoria

As chaves substitutas de aluno, plano, unidade, consultor e tempo são as mesmas da estrela.

```mermaid
erDiagram
    DIM_TEMPO ||--o{ FATO_MATRICULA : ""
    DIM_ALUNO ||--o{ FATO_MATRICULA : ""
    DIM_PLANO ||--o{ FATO_MATRICULA : ""
    DIM_UNIDADE ||--o{ FATO_MATRICULA : ""
    DIM_CONSULTOR ||--o{ FATO_MATRICULA : ""
    DIM_CIDADE ||--o{ DIM_ALUNO : ""
    DIM_CIDADE ||--o{ DIM_UNIDADE : ""
    DIM_ESTADO ||--o{ DIM_CIDADE : ""
    DIM_CATEGORIA ||--o{ DIM_PLANO : ""

    FATO_MATRICULA {
        bigint id_matricula PK
        int sk_tempo FK
        int sk_aluno FK
        int sk_plano FK
        int sk_unidade FK
        int sk_consultor FK
        varchar status
        numeric valor_bruto
        numeric desconto
        numeric valor_liquido
    }
    DIM_ALUNO {
        int sk_aluno PK
        bigint id_origem
        varchar nome
        varchar faixa_etaria
        varchar bairro
        int sk_cidade FK
        date data_inicio
        date data_fim
        boolean atual
    }
    DIM_UNIDADE {
        int sk_unidade PK
        bigint id_origem
        varchar nome
        varchar bairro
        int sk_cidade FK
    }
    DIM_CIDADE {
        int sk_cidade PK
        varchar nome
        int sk_estado FK
    }
    DIM_ESTADO {
        int sk_estado PK
        char uf
        varchar nome_estado
        varchar regiao
    }
    DIM_PLANO {
        int sk_plano PK
        bigint id_origem
        varchar nome
        int sk_categoria FK
        varchar pacote
        varchar periodicidade
    }
    DIM_CATEGORIA {
        int sk_categoria PK
        varchar nome
    }
    DIM_TEMPO {
        int sk_tempo PK
        date data
        smallint mes
        smallint trimestre
        smallint ano
    }
    DIM_CONSULTOR {
        int sk_consultor PK
        varchar nome
    }
```

**Mesma consulta nos dois modelos** (receita por UF da unidade, categoria e ano):

| Modelo | Tabelas | Junções |
| --- | --- | --- |
| Estrela | fato, tempo, unidade, plano | 3 |
| Floco de neve | fato, tempo, unidade, cidade, estado, plano, categoria | 6 |
