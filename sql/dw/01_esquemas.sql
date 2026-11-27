-- =============================================================================
-- Etapa 2 - Esquemas do Data Warehouse (PostgreSQL, mesmo banco "academia")
--   public : OLTP criado pelo Hibernate na Etapa 1 (não é alterado aqui)
--   stg    : área de staging e procedures do ETL
--   dw     : modelo ESTRELA
--   dwf    : modelo FLOCO DE NEVE (mesmo assunto, dimensões normalizadas)
-- Rodar com: psql -U postgres -d academia -f sql/dw/01_esquemas.sql
-- =============================================================================
SET client_encoding = 'UTF8';   -- arquivos salvos em UTF-8 (evita acentos quebrados no Windows)
CREATE SCHEMA IF NOT EXISTS stg;
CREATE SCHEMA IF NOT EXISTS dw;
CREATE SCHEMA IF NOT EXISTS dwf;

COMMENT ON SCHEMA stg IS 'Área de staging e procedures do ETL';
COMMENT ON SCHEMA dw  IS 'Data Warehouse - modelo estrela (fato matrícula)';
COMMENT ON SCHEMA dwf IS 'Data Warehouse - modelo floco de neve (fato matrícula)';
