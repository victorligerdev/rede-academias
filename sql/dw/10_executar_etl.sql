SET client_encoding = 'UTF8';
CREATE OR REPLACE PROCEDURE stg.executar_etl()
LANGUAGE plpgsql AS $$
DECLARE
    v_exec   INT;
    v_ultimo BIGINT;
    v_tipo   VARCHAR(12);
    v_antes  JSONB;
BEGIN
    SELECT max(ultimo_id_matricula) INTO v_ultimo FROM stg.execucao_etl WHERE fim IS NOT NULL;
    v_tipo   := CASE WHEN v_ultimo IS NULL THEN 'INICIAL' ELSE 'INCREMENTAL' END;
    v_ultimo := COALESCE(v_ultimo, 0);

    INSERT INTO stg.execucao_etl (tipo, ultimo_id_matricula)
    VALUES (v_tipo, v_ultimo) RETURNING id_execucao INTO v_exec;

    v_antes := stg.contagens(ARRAY['stg.aluno','stg.unidade','stg.plano','stg.consultor',
                                   'stg.servico','stg.matricula','stg.matricula_servico']);
    CALL stg.extrair(v_ultimo);
    CALL stg.registrar_etapa(v_exec, '1-extracao', v_antes);
    v_antes := stg.contagens(ARRAY['dw.dim_tempo','dw.dim_aluno','dw.dim_plano','dw.dim_unidade',
                                   'dw.dim_consultor','dw.dim_servico']);
    CALL stg.transformar();
    CALL stg.carregar_dimensoes();
    CALL stg.registrar_etapa(v_exec, '2-dimensoes', v_antes);

    v_antes := stg.contagens(ARRAY['dw.fato_matricula','dw.ponte_matricula_servico']);
    CALL stg.carregar_fato();
    CALL stg.registrar_etapa(v_exec, '3-fato', v_antes);

    v_antes := stg.contagens(ARRAY['dwf.dim_estado','dwf.dim_cidade','dwf.dim_categoria',
                                   'dwf.dim_tempo','dwf.dim_aluno','dwf.dim_unidade',
                                   'dwf.dim_plano','dwf.dim_consultor','dwf.fato_matricula']);
    CALL stg.carregar_floco();
    CALL stg.registrar_etapa(v_exec, '4-floco', v_antes);

    UPDATE stg.execucao_etl
       SET fim = clock_timestamp(),
           ultimo_id_matricula = (SELECT COALESCE(max(id_matricula), 0) FROM dw.fato_matricula)
     WHERE id_execucao = v_exec;

    RAISE NOTICE 'ETL % (execução %) concluído: % matrículas na fato, % alunos (versões) na dimensão.',
        v_tipo, v_exec, stg.contar('dw.fato_matricula'), stg.contar('dw.dim_aluno');
END $$;
