# Modelo estrela (schema `dw`)

**Assunto:** matrículas da rede de academias.
**Granularidade:** uma linha por matrícula (um plano vendido a um aluno, em uma unidade, em uma data).
**Medidas:** quantidade, meses, qtd_servicos, valor_plano, valor_servicos, valor_bruto, desconto, valor_liquido e as derivadas valor_mensal_liquido e percentual_desconto.

```mermaid
erDiagram
    DIM_TEMPO ||--o{ FATO_MATRICULA : "data da matrícula"
    DIM_ALUNO ||--o{ FATO_MATRICULA : "cliente"
    DIM_PLANO ||--o{ FATO_MATRICULA : "item"
    DIM_UNIDADE ||--o{ FATO_MATRICULA : "local"
    DIM_CONSULTOR ||--o{ FATO_MATRICULA : "vendedor"
    FATO_MATRICULA ||--o{ PONTE_MATRICULA_SERVICO : "serviços"
    DIM_SERVICO ||--o{ PONTE_MATRICULA_SERVICO : ""

    FATO_MATRICULA {
        bigint id_matricula PK "dimensão degenerada"
        int sk_tempo FK
        int sk_aluno FK
        int sk_plano FK
        int sk_unidade FK
        int sk_consultor FK
        varchar status
        smallint quantidade
        smallint meses
        smallint qtd_servicos
        numeric valor_plano
        numeric valor_servicos
        numeric valor_bruto
        numeric desconto
        numeric valor_liquido
        numeric valor_mensal_liquido
        numeric percentual_desconto
    }
    DIM_TEMPO {
        int sk_tempo PK "AAAAMMDD"
        date data
        smallint dia
        smallint mes
        varchar nome_mes
        smallint trimestre
        smallint semestre
        smallint ano
        varchar dia_semana
        boolean fim_de_semana
    }
    DIM_ALUNO {
        int sk_aluno PK
        bigint id_origem "pessoa_id no OLTP"
        varchar nome
        varchar sexo
        varchar faixa_etaria
        varchar bairro "SCD 2"
        varchar cidade "SCD 2"
        char uf "SCD 2"
        date data_inicio
        date data_fim
        boolean atual
    }
    DIM_PLANO {
        int sk_plano PK
        bigint id_origem
        varchar codigo
        varchar nome
        varchar categoria
        varchar pacote
        varchar periodicidade
        smallint meses
        numeric valor_mensal
    }
    DIM_UNIDADE {
        int sk_unidade PK
        bigint id_origem
        varchar nome
        varchar bairro
        varchar cidade
        char uf
    }
    DIM_CONSULTOR {
        int sk_consultor PK "-1 = Não informado"
        bigint id_origem
        varchar nome
        varchar cargo
        varchar unidade_lotacao
    }
    DIM_SERVICO {
        int sk_servico PK
        bigint id_origem
        varchar nome
        numeric valor_mensal
    }
    PONTE_MATRICULA_SERVICO {
        bigint id_matricula PK
        int sk_servico PK
    }
```
