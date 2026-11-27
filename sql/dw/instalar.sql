SET client_encoding = 'UTF8';
\set ON_ERROR_STOP on
\ir 01_esquemas.sql
\ir 02_ddl_estrela.sql
\ir 03_ddl_floco.sql
\ir 04_staging.sql
\ir 05_dim_tempo.sql
\ir 06_extracao.sql
\ir 07_transformacao_dimensoes.sql
\ir 08_carga_fato.sql
\ir 09_carga_floco.sql
\ir 10_executar_etl.sql
CALL stg.gerar_dim_tempo(DATE '2024-01-01', DATE '2027-12-31');
\echo 'DW instalado. Rode: CALL stg.executar_etl();'
