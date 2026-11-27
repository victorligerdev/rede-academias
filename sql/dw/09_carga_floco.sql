SET client_encoding = 'UTF8';
CREATE OR REPLACE PROCEDURE stg.carregar_floco()
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO dwf.dim_estado (uf, nome_estado, regiao)
    SELECT DISTINCT x.uf, COALESCE(r.nome, 'Não informado'), COALESCE(r.regiao, 'Não informada')
    FROM (SELECT uf FROM dw.dim_aluno UNION SELECT uf FROM dw.dim_unidade) x
    LEFT JOIN (VALUES
        ('AC','Acre','Norte'), ('AL','Alagoas','Nordeste'), ('AP','Amapá','Norte'),
        ('AM','Amazonas','Norte'), ('BA','Bahia','Nordeste'), ('CE','Ceará','Nordeste'),
        ('DF','Distrito Federal','Centro-Oeste'), ('ES','Espírito Santo','Sudeste'),
        ('GO','Goiás','Centro-Oeste'), ('MA','Maranhão','Nordeste'),
        ('MT','Mato Grosso','Centro-Oeste'), ('MS','Mato Grosso do Sul','Centro-Oeste'),
        ('MG','Minas Gerais','Sudeste'), ('PA','Pará','Norte'), ('PB','Paraíba','Nordeste'),
        ('PR','Paraná','Sul'), ('PE','Pernambuco','Nordeste'), ('PI','Piauí','Nordeste'),
        ('RJ','Rio de Janeiro','Sudeste'), ('RN','Rio Grande do Norte','Nordeste'),
        ('RS','Rio Grande do Sul','Sul'), ('RO','Rondônia','Norte'), ('RR','Roraima','Norte'),
        ('SC','Santa Catarina','Sul'), ('SP','São Paulo','Sudeste'), ('SE','Sergipe','Nordeste'),
        ('TO','Tocantins','Norte')
    ) AS r(uf, nome, regiao) ON r.uf = x.uf
    ON CONFLICT (uf) DO NOTHING;

    INSERT INTO dwf.dim_cidade (nome, sk_estado)
    SELECT DISTINCT x.cidade, e.sk_estado
    FROM (SELECT cidade, uf FROM dw.dim_aluno UNION SELECT cidade, uf FROM dw.dim_unidade) x
    JOIN dwf.dim_estado e ON e.uf = x.uf
    ON CONFLICT (nome, sk_estado) DO NOTHING;

    INSERT INTO dwf.dim_categoria (nome)
    SELECT DISTINCT categoria FROM dw.dim_plano
    ON CONFLICT (nome) DO NOTHING;

    INSERT INTO dwf.dim_tempo SELECT * FROM dw.dim_tempo ON CONFLICT DO NOTHING;

    INSERT INTO dwf.dim_aluno (sk_aluno, id_origem, nome, sexo, faixa_etaria, bairro, sk_cidade,
                               data_cadastro, data_inicio, data_fim, atual)
    SELECT a.sk_aluno, a.id_origem, a.nome, a.sexo, a.faixa_etaria, a.bairro, c.sk_cidade,
           a.data_cadastro, a.data_inicio, a.data_fim, a.atual
    FROM dw.dim_aluno a
    JOIN dwf.dim_estado e ON e.uf = a.uf
    JOIN dwf.dim_cidade c ON c.nome = a.cidade AND c.sk_estado = e.sk_estado
    ON CONFLICT (sk_aluno) DO UPDATE SET
        nome = EXCLUDED.nome, sexo = EXCLUDED.sexo, faixa_etaria = EXCLUDED.faixa_etaria,
        bairro = EXCLUDED.bairro, sk_cidade = EXCLUDED.sk_cidade,
        data_cadastro = EXCLUDED.data_cadastro, data_inicio = EXCLUDED.data_inicio,
        data_fim = EXCLUDED.data_fim, atual = EXCLUDED.atual;

    INSERT INTO dwf.dim_unidade (sk_unidade, id_origem, nome, bairro, sk_cidade,
                                 capacidade_alunos, data_inauguracao)
    SELECT u.sk_unidade, u.id_origem, u.nome, u.bairro, c.sk_cidade,
           u.capacidade_alunos, u.data_inauguracao
    FROM dw.dim_unidade u
    JOIN dwf.dim_estado e ON e.uf = u.uf
    JOIN dwf.dim_cidade c ON c.nome = u.cidade AND c.sk_estado = e.sk_estado
    ON CONFLICT (sk_unidade) DO UPDATE SET
        nome = EXCLUDED.nome, bairro = EXCLUDED.bairro, sk_cidade = EXCLUDED.sk_cidade,
        capacidade_alunos = EXCLUDED.capacidade_alunos,
        data_inauguracao = EXCLUDED.data_inauguracao;

    INSERT INTO dwf.dim_plano (sk_plano, id_origem, codigo, nome, sk_categoria, pacote,
                               periodicidade, meses, valor_mensal, ativo)
    SELECT p.sk_plano, p.id_origem, p.codigo, p.nome, c.sk_categoria, p.pacote,
           p.periodicidade, p.meses, p.valor_mensal, p.ativo
    FROM dw.dim_plano p
    JOIN dwf.dim_categoria c ON c.nome = p.categoria
    ON CONFLICT (sk_plano) DO UPDATE SET
        codigo = EXCLUDED.codigo, nome = EXCLUDED.nome, sk_categoria = EXCLUDED.sk_categoria,
        pacote = EXCLUDED.pacote, periodicidade = EXCLUDED.periodicidade,
        meses = EXCLUDED.meses, valor_mensal = EXCLUDED.valor_mensal, ativo = EXCLUDED.ativo;

    INSERT INTO dwf.dim_consultor SELECT * FROM dw.dim_consultor
    ON CONFLICT (sk_consultor) DO UPDATE SET
        nome = EXCLUDED.nome, cargo = EXCLUDED.cargo, unidade_lotacao = EXCLUDED.unidade_lotacao;

    INSERT INTO dwf.fato_matricula SELECT * FROM dw.fato_matricula
    ON CONFLICT (id_matricula) DO UPDATE SET
        status = EXCLUDED.status, desconto = EXCLUDED.desconto,
        valor_liquido = EXCLUDED.valor_liquido,
        valor_mensal_liquido = EXCLUDED.valor_mensal_liquido,
        percentual_desconto = EXCLUDED.percentual_desconto;
END $$;
