# Rede de Academias Vértice

Projeto acadêmico de persistência objeto-relacional com JPA 2.1/Hibernate 5.2 e PostgreSQL.

## Pré-requisitos

- JDK 8 ou superior
- Maven
- PostgreSQL

## Configuração

A conexão pode ser ajustada em `src/main/resources/META-INF/persistence.xml`:

- banco: `academia`
- usuário padrão: `postgres`
- senha padrão: `postgres`

O Hibernate usa `hibernate.hbm2ddl.auto=update` para criar/atualizar o esquema.

## Executar

```bash
mvn compile
mvn exec:java
```

Modos automáticos:

```bash
mvn exec:java -Dexec.args="carga"
mvn exec:java -Dexec.args="consultas"
```

## Funcionalidades

- Modelo JPA com herança JOINED, classes embutidas e relacionamentos 1:N, 1:1 e N:N
- DAOs genérico e específicos
- 10 consultas JPQL
- 3 consultas com Criteria API
- CRUD de alunos e planos em console
- Carga automatizada de dados

## Estrutura

- `model`: entidades e enums
- `dao`: acesso a dados
- `consultas`: consultas JPQL e Criteria
- `carga`: geração dos dados
- `app`: demonstração e menu de console
- `sql/ddl_oltp.sql`: definição das tabelas principais do OLTP

## Etapa 2: Data Warehouse, ETL, OLAP e mineração

Tudo roda no mesmo PostgreSQL do OLTP (banco `academia`), em três schemas novos:
`stg` (staging e procedures do ETL), `dw` (modelo estrela) e `dwf` (floco de neve).

### Como rodar

Pré-requisito: o banco `academia` já carregado pela Etapa 1 (menu, opção 5).

```
psql -U postgres -d academia -f sql/dw/instalar.sql            # cria esquemas, tabelas e procedures
psql -U postgres -d academia -c "CALL stg.executar_etl();"      # 1ª vez = carga inicial
psql -U postgres -d academia -f sql/dw/11_validacao.sql         # prova da carga (contagens e somas)
psql -U postgres -d academia -f sql/olap/01_consultas_olap.sql  # 10 consultas OLAP
psql -U postgres -d academia -f sql/olap/02_comparacao_tempo.sql
pip install -r mineracao/requirements.txt
python mineracao/apriori.py                                     # regras de associação
```

Rodar `CALL stg.executar_etl();` de novo faz a **carga incremental**: só entram matrículas novas,
mudanças de status e de endereço (SCD tipo 2 em `dw.dim_aluno`).

### Modelagem

- **Granularidade:** uma linha por matrícula (um plano vendido a um aluno, numa unidade, numa data).
- **Fato:** `dw.fato_matricula`, com 9 medidas (2 derivadas) e a matrícula como dimensão degenerada.
- **Dimensões:** Tempo, Aluno (SCD 2), Plano, Unidade e Consultor, mais Serviço via tabela ponte.
- **Floco de neve:** Aluno/Unidade → Cidade → Estado e Plano → Categoria.
- Diagramas: [docs/modelo_estrela.md](docs/modelo_estrela.md) e [docs/modelo_floco.md](docs/modelo_floco.md).

### Onde está cada requisito do item 4.3

| Requisito | Onde |
|---|---|
| Estrela (1 fato, 3+ medidas, 4+ dimensões, Tempo) | `sql/dw/02_ddl_estrela.sql` |
| Floco de neve (2+ dimensões normalizadas) | `sql/dw/03_ddl_floco.sql` |
| Diagramas | `docs/modelo_estrela.md`, `docs/modelo_floco.md` |
| Staging | `sql/dw/04_staging.sql` |
| Dimensão Tempo gerada | `sql/dw/05_dim_tempo.sql` |
| Chaves substitutas | colunas `sk_*` (SERIAL) em todas as dimensões |
| Nulos e padronização | `sql/dw/07_transformacao_dimensoes.sql` (`stg.padronizar`, membro -1) |
| Medidas derivadas | `valor_servicos`, `valor_plano`, `valor_mensal_liquido`, `percentual_desconto` |
| Carga incremental | `sql/dw/06_extracao.sql` + `stg.execucao_etl` |
| SCD tipo 2 | `dw.dim_aluno` (bairro, cidade, UF) |
| Contagens antes/depois | `stg.log_etl`, `sql/dw/11_validacao.sql` |
| OLAP (2 por operação, ROLLUP/CUBE) | `sql/olap/01_consultas_olap.sql` |
| Tempo OLTP x estrela x floco | `sql/olap/02_comparacao_tempo.sql` |
| Mineração (Apriori) | `mineracao/apriori.py`, `base_cestas.csv`, `regras.csv` |
| Demonstração (JPA → DW) | `sql/dw/12_demo_incremental.sql` |
