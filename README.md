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
