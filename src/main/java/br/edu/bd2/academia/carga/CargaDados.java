package br.edu.bd2.academia.carga;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;

import br.edu.bd2.academia.model.Aluno;
import br.edu.bd2.academia.model.enums.Cargo;
import br.edu.bd2.academia.model.CategoriaPlano;
import br.edu.bd2.academia.model.Endereco;
import br.edu.bd2.academia.model.FichaMedica;
import br.edu.bd2.academia.model.enums.FormaPagamento;
import br.edu.bd2.academia.model.Funcionario;
import br.edu.bd2.academia.model.Matricula;
import br.edu.bd2.academia.model.Modalidade;
import br.edu.bd2.academia.model.Pagamento;
import br.edu.bd2.academia.model.enums.Periodicidade;
import br.edu.bd2.academia.model.Pessoa;
import br.edu.bd2.academia.model.Plano;
import br.edu.bd2.academia.model.ServicoAdicional;
import br.edu.bd2.academia.model.enums.Sexo;
import br.edu.bd2.academia.model.enums.StatusMatricula;
import br.edu.bd2.academia.model.enums.StatusPagamento;
import br.edu.bd2.academia.model.Unidade;

/**
 * Gera dados realistas para a rede de academias "Vértice":
 * - 5 unidades (RN, PB, PE), 35 funcionários
 * - 10 modalidades, 5 categorias, 100 planos (5 categorias x 5 pacotes x 4
 * periodicidades)
 * - 5 serviços adicionais
 * - alunos suficientes para passar de 2.000 matrículas (mínimo 200 alunos)
 * - matrículas de 01/11/2024 até a data de referência (2 anos), com
 * renovações,
 *    cancelamentos, sazonalidade (pico em janeiro/março, queda em dezembro)
 * e parcelas.
 *
 * A semente do Random é fixa: toda execução gera exatamente os mesmos dados,
 * o que facilita comparar resultados entre os integrantes da equipe.
 */
public class CargaDados {

    /** "Hoje" para a simulação. Matrículas após esta data não são geradas.
     */
    public static final LocalDate DATA_REFERENCIA = LocalDate.of(2026, 11,
            13);
    public static final LocalDate INICIO_HISTORICO = LocalDate.of(2024, 11,
            1);

    private static final int MIN_MATRICULAS = 2100;
    private static final int MIN_ALUNOS = 200;
    private static final int ALUNOS_SEM_MATRICULA = 15;

    private final EntityManager em;
    private final Random rnd = new Random(2026);
    private final Set<String> cpfsUsados = new HashSet<>();

    private final List<Unidade> unidades = new ArrayList<>();
    private final List<List<Funcionario>> consultoresPorUnidade = new
            ArrayList<>();
    private final List<CategoriaPlano> categorias = new ArrayList<>();
    private final List<Plano> planos = new ArrayList<>();
    private final List<ServicoAdicional> servicos = new ArrayList<>();

    // ------------------------------------------------------------------
    // dados base

    private static final String[] NOMES_M = {"João", "Pedro", "Lucas",
            "Gabriel", "Matheus", "Rafael", "Gustavo",
            "Felipe", "Bruno", "Thiago", "Daniel", "Rodrigo", "Diego", "Carlos",
            "André", "Marcos", "Leonardo",
            "Vinícius", "Eduardo", "Ricardo", "Henrique", "Caio", "Igor",
            "Samuel", "Arthur", "Davi", "Heitor",
            "Bernardo", "Miguel", "Otávio", "José", "Antônio", "Francisco",
            "Paulo", "Renan"};
    private static final String[] NOMES_F = {"Maria", "Ana", "Juliana",
            "Mariana", "Camila", "Fernanda", "Beatriz",

            "Larissa", "Amanda", "Letícia", "Gabriela", "Bruna", "Carolina",
            "Patrícia", "Aline", "Natália",
            "Isabela", "Raquel", "Vanessa", "Débora", "Luana", "Yasmin", "Sofia",
            "Helena", "Alice", "Laura",
            "Manuela", "Clara", "Lívia", "Cecília", "Jéssica", "Priscila",
            "Tatiane", "Renata", "Bianca"};
    private static final String[] SOBRENOMES = {"Silva", "Santos",
            "Oliveira", "Souza", "Lima", "Pereira",
            "Ferreira", "Costa", "Rodrigues", "Almeida", "Nascimento", "Alves",
            "Carvalho", "Araújo", "Ribeiro",
            "Gomes", "Martins", "Barbosa", "Rocha", "Dantas", "Medeiros",
            "Fernandes", "Cavalcanti", "Bezerra",
            "Freitas", "Moura", "Teixeira", "Monteiro", "Cardoso", "Batista",
            "Macedo", "Queiroz", "Galvão",
            "Pinheiro", "Farias", "Maia", "Tavares", "Correia", "Diniz",
            "Lopes"};
    private static final String[] RUAS = {"Rua das Flores", "Av. Prudente de Morais", "Rua Seridó", "Av. Hermes da Fonseca",
            "Rua Mossoró", "Av. Senador Salgado Filho", "Rua Apodi", "Av. Engenheiro Roberto Freire", "Rua Jaguarari",
            "Rua João Pessoa", "Av. Epitácio Pessoa", "Rua Dom Pedro II", "Av. Boa Viagem", "Rua da Aurora",
            "Av. Rio Branco", "Rua Felipe Camarão", "Rua Ceará-Mirim", "Av. Bernardo Vieira"};
    private static final String[] RESTRICOES = {null, null, null, null, null,
            "Hipertensão controlada",
            "Lesão no joelho (LCA)", "Hérnia de disco lombar", "Asma leve",
            "Diabetes tipo 2", "Tendinite no ombro"};
    // cidade, UF, bairros
    private static final String[][] CIDADES = {
            {"Natal", "RN", "Ponta Negra", "Capim Macio", "Lagoa Nova", "Tirol",
                    "Petrópolis", "Candelária", "Neópolis"},
            {"Natal", "RN", "Tirol", "Petrópolis", "Lagoa Nova", "Alecrim",
                    "Barro Vermelho", "Cidade Alta", "Lagoa Seca"},
            {"Mossoró", "RN", "Centro", "Nova Betânia", "Abolição", "Alto de São Manoel", "Santo Antônio", "Aeroporto", "Planalto 13 de Maio"},
            {"João Pessoa", "PB", "Manaíra", "Tambaú", "Cabo Branco", "Bessa",
                    "Bancários", "Miramar", "Altiplano"},
            {"Recife", "PE", "Boa Viagem", "Casa Forte", "Espinheiro", "Graças",
                    "Pina", "Madalena", "Torre"}
    };

    public CargaDados(EntityManager em) {
        this.em = em;
    }

    /** Executa a carga completa. Retorna o número de matrículas geradas. */
    public int executar() {
        long t0 = System.currentTimeMillis();
        em.getTransaction().begin();
        criarUnidadesEFuncionarios();
        List<Modalidade> modalidades = criarModalidades();
        criarCategorias();
        criarPlanos(modalidades);
        criarServicos();
        em.getTransaction().commit();
        System.out.printf(" Cadastros base: %d unidades, %d modalidades, %d categorias, %d planos, %d serviços%n",
                unidades.size(), modalidades.size(), categorias.size(),
                planos.size(), servicos.size());

        int alunos = 0;
        int matriculas = 0;
        int pagamentos = 0;
        em.getTransaction().begin();
        while (matriculas < MIN_MATRICULAS || alunos < MIN_ALUNOS) {
            int unidadeCasa = escolherUnidade();
            Aluno a = criarAluno(unidadeCasa, sortearInicioAluno());
            em.persist(a);
            alunos++;
            List<Matricula> geradas = gerarHistoricoDeMatriculas(a,
                    unidadeCasa);
            for (Matricula m : geradas) {
                em.persist(m); // cascade ALL grava as parcelas junto
                pagamentos += m.getPagamentos().size();
            }
            matriculas += geradas.size();

            if (alunos % 50 == 0) { // descarrega o contexto de persistência
                // para não acumular memória
                em.flush();
                em.clear();
                recarregarReferencias();
                em.getTransaction().commit();
                em.getTransaction().begin();
                System.out.printf(" ... %d alunos, %d matrículas%n", alunos,
                        matriculas);
            }
        }
        // alguns alunos que só se cadastraram (usados na consulta
        // Aluno.semMatricula)
        for (int i = 0; i < ALUNOS_SEM_MATRICULA; i++) {

            LocalDate cad = DATA_REFERENCIA.minusDays(1 + rnd.nextInt(90));
            em.persist(criarAluno(escolherUnidade(), cad));
            alunos++;
        }
        em.getTransaction().commit();
        em.clear();

        System.out.printf(" Alunos: %d | Matrículas: %d | Parcelas: %d | Tempo: %.1f s%n",
                alunos, matriculas, pagamentos, (System.currentTimeMillis() -
                        t0) / 1000.0);
        return matriculas;
    }

    /** Apaga todos os dados (TRUNCATE em todas as tabelas do OLTP). */
    public void limpar() {
        em.getTransaction().begin();
        em.createNativeQuery("TRUNCATE TABLE pagamento, matricula_servico, matricula, plano_modalidade, plano, "
                + "categoria_plano, modalidade, servico_adicional, aluno, funcionario, ficha_medica, pessoa, unidade "
                + "RESTART IDENTITY CASCADE").executeUpdate();
        em.getTransaction().commit();
        em.clear();
    }

    /** Depois de em.clear() as entidades base ficam detached: buscamos de
     * novo as referências. */
    private void recarregarReferencias() {
        for (int i = 0; i < unidades.size(); i++) {
            unidades.set(i, em.getReference(Unidade.class,
                    unidades.get(i).getId()));
            List<Funcionario> cons = consultoresPorUnidade.get(i);
            for (int j = 0; j < cons.size(); j++) {
                cons.set(j, em.getReference(Funcionario.class,
                        cons.get(j).getId()));
            }
        }
        for (int i = 0; i < planos.size(); i++) {
            Plano antigo = planos.get(i);
            Plano ref = em.find(Plano.class, antigo.getId());
            planos.set(i, ref);
        }
        for (int i = 0; i < servicos.size(); i++) {
            servicos.set(i, em.getReference(ServicoAdicional.class,
                    servicos.get(i).getId()));

        }
    }

    // ------------------------------------------------------------------
    // cadastros base

    private void criarUnidadesEFuncionarios() {
        String[][] dados = {
                {"Vértice Ponta Negra", "Av. Engenheiro Roberto Freire", "2015-03-02", "900"},
                {"Vértice Tirol", "Av. Hermes da Fonseca", "2017-08-14", "650"},
                {"Vértice Mossoró", "Av. Rio Branco", "2019-02-11", "500"},
                {"Vértice João Pessoa", "Av. Epitácio Pessoa", "2021-06-07",
                        "700"},
                {"Vértice Recife", "Av. Boa Viagem", "2023-01-09", "800"}
        };
        for (int i = 0; i < dados.length; i++) {
            String[] c = CIDADES[i];
            Endereco end = new Endereco(dados[i][1], String.valueOf(100 +
                    rnd.nextInt(3000)), c[2], c[0], c[1], cep(c[1]));
            Unidade u = new Unidade(dados[i][0], cnpj(i),
                    Integer.valueOf(dados[i][3]), LocalDate.parse(dados[i][2]), end);
            em.persist(u);
            unidades.add(u);

            List<Funcionario> consultores = new ArrayList<>();
            Cargo[] quadro = {Cargo.GERENTE, Cargo.CONSULTOR_VENDAS,
                    Cargo.CONSULTOR_VENDAS,
                    Cargo.INSTRUTOR, Cargo.INSTRUTOR, Cargo.INSTRUTOR,
                    Cargo.RECEPCIONISTA};
            for (Cargo cargo : quadro) {
                Funcionario f = new Funcionario();
                preencherPessoa(f, i, LocalDate.of(1975 + rnd.nextInt(28), 1
                        + rnd.nextInt(12), 1 + rnd.nextInt(28)));
                f.setCargo(cargo);
                f.setSalario(salario(cargo));
                LocalDate adm =
                        u.getDataInauguracao().plusDays(rnd.nextInt(700));
                f.setDataAdmissao(adm);
                f.setUnidade(u);
                u.getFuncionarios().add(f);
                em.persist(f);
                if (cargo == Cargo.CONSULTOR_VENDAS) {
                    consultores.add(f);
                }
            }

            consultoresPorUnidade.add(consultores);
        }
    }

    private List<Modalidade> criarModalidades() {
        String[][] dados = {
                {"Musculação", "Treino de força com pesos livres e aparelhos"},
                {"Funcional", "Treino em circuito com o peso do corpo"},
                {"Spinning", "Aula de bicicleta indoor"},
                {"Natação", "Aulas em piscina semiolímpica aquecida"},
                {"Hidroginástica", "Exercícios aeróbicos na água"},
                {"Pilates", "Pilates solo e com aparelhos"},
                {"Yoga", "Hatha e vinyasa yoga"},
                {"Muay Thai", "Arte marcial tailandesa"},
                {"Jiu-Jitsu", "Arte marcial de solo"},
                {"Dança", "Ritmos, zumba e fit dance"}
        };
        List<Modalidade> lista = new ArrayList<>();
        for (String[] d : dados) {
            Modalidade m = new Modalidade(d[0], d[1]);
            em.persist(m);
            lista.add(m);
        }
        return lista;
    }

    private void criarCategorias() {
        String[][] dados = {
                {"Estudante", "Desconto para estudantes com comprovante de matrícula"},
                {"Básico", "Acesso em horário comercial à unidade de origem"},
                {"Plus", "Acesso livre à unidade de origem e avaliação física trimestral"},
                {"Premium", "Acesso a todas as unidades da rede e avaliação mensal"},
                {"Corporativo", "Convênio com empresas parceiras"}
        };
        for (String[] d : dados) {
            CategoriaPlano c = new CategoriaPlano(d[0], d[1]);
            em.persist(c);
            categorias.add(c);
        }
    }

    // pacotes de modalidades: nome, sigla, preço base mensal, índices das
    // modalidades

    private static final Object[][] PACOTES = {
            {"Musculação", "MUS", "89.90", new int[]{0}},
            {"Musculação + Funcional", "MFU", "119.90", new int[]{0, 1, 2}},
            {"Aquático", "AQU", "129.90", new int[]{3, 4}},
            {"Lutas", "LUT", "139.90", new int[]{7, 8, 1}},
            {"Total", "TOT", "189.90", new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}}
    };
    private static final double[] FATOR_CATEGORIA = {0.80, 1.00, 1.25, 1.60,
            0.90};
    private static final String[] SIGLA_CATEGORIA = {"EST", "BAS", "PLU",
            "PRE", "COR"};

    private void criarPlanos(List<Modalidade> modalidades) {
        for (int c = 0; c < categorias.size(); c++) {
            for (Object[] pac : PACOTES) {
                for (Periodicidade per : Periodicidade.values()) {
                    double descontoPeriodo;
                    switch (per) {
                        case TRIMESTRAL: descontoPeriodo = 0.95; break;
                        case SEMESTRAL: descontoPeriodo = 0.90; break;
                        case ANUAL:      descontoPeriodo = 0.80; break;
                        default:             descontoPeriodo = 1.00;
                    }
                    BigDecimal valor = new BigDecimal((String) pac[2])
                            .multiply(BigDecimal.valueOf(FATOR_CATEGORIA[c] *
                                    descontoPeriodo))
                            .setScale(0, RoundingMode.HALF_UP)
                            .subtract(new BigDecimal("0.10")); // preço
                    // "psicológico": 99,90
                    Plano p = new Plano();
                    p.setCodigo(SIGLA_CATEGORIA[c] + "-" + pac[1] + "-" +
                            per.name().substring(0, 3));
                    p.setNome(categorias.get(c).getNome() + " " + pac[0] + " " + capitalizar(per.name()));
                    p.setPeriodicidade(per);
                    p.setValorMensal(valor);
                    p.setCategoria(categorias.get(c));
                    for (int idx : (int[]) pac[3]) {
                        p.adicionarModalidade(modalidades.get(idx));
                    }
                    // alguns planos antigos foram descontinuados
                    p.setAtivo(!(c == 4 && per == Periodicidade.MENSAL));
                    em.persist(p);
                    planos.add(p);
                }
            }

        }
    }

    private void criarServicos() {
        String[][] dados = {
                {"Personal Trainer", "250.00"},
                {"Armário Fixo", "30.00"},
                {"Acompanhamento Nutricional", "90.00"},
                {"Avaliação Física Mensal", "40.00"},
                {"Toalha e Kit Banho", "15.00"}
        };
        for (String[] d : dados) {
            ServicoAdicional s = new ServicoAdicional(d[0], new
                    BigDecimal(d[1]));
            em.persist(s);
            servicos.add(s);
        }
    }

    // ------------------------------------------------------------------
    // alunos e matrículas

    private Aluno criarAluno(int unidadeCasa, LocalDate dataCadastro) {
        Aluno a = new Aluno();
        int idade = sortearIdade();
        LocalDate nasc =
                dataCadastro.minusYears(idade).minusDays(rnd.nextInt(365));
        preencherPessoa(a, unidadeCasa, nasc);
        a.setDataCadastro(dataCadastro);
        if (rnd.nextDouble() < 0.85) { // 85% fizeram avaliação
            BigDecimal peso = BigDecimal.valueOf(50 + rnd.nextInt(60) +
                    rnd.nextInt(10) / 10.0).setScale(1, RoundingMode.HALF_UP);
            int altura = a.getSexo() == Sexo.FEMININO ? 152 + rnd.nextInt(26)
                    : 162 + rnd.nextInt(30);
            String restr = RESTRICOES[rnd.nextInt(RESTRICOES.length)];
            a.setFichaMedica(new FichaMedica(peso, altura, restr,
                    rnd.nextDouble() > 0.02, dataCadastro));
        }
        return a;
    }

    /**
     * Simula a "vida" do aluno na academia: matrícula inicial, renovações
     * (às vezes trocando
     * de plano), desistência e eventual retorno meses depois.
     */

    private List<Matricula> gerarHistoricoDeMatriculas(Aluno a, int
            unidadeCasa) {
        List<Matricula> lista = new ArrayList<>();
        LocalDate inicio = a.getDataCadastro();
        Plano plano = sortearPlano(a);
        while (!inicio.isAfter(DATA_REFERENCIA)) {
            // 8% das matrículas acontecem em outra unidade (aluno Premium ou
            // mudança)
            int u = rnd.nextDouble() < 0.92 ? unidadeCasa :
                    rnd.nextInt(unidades.size());
            Matricula m = criarMatricula(a, plano, u, inicio);
            lista.add(m);

            if (m.getStatus() == StatusMatricula.CANCELADA) {
                break;
            }
            LocalDate proxima = m.getDataFim().plusDays(1);
            double r = rnd.nextDouble();
            if (r < 0.78) {
                // renova; 25% de chance de mudar de plano
                proxima = proxima.plusDays(rnd.nextInt(6));
                if (rnd.nextDouble() < 0.25) plano = sortearPlano(a);
            } else if (r < 0.90) {
                // para por alguns meses e volta
                proxima = proxima.plusMonths(2 + rnd.nextInt(6));
                plano = sortearPlano(a);
            } else {
                break; // desistiu de vez
            }
            inicio = ajustarSazonalidade(proxima);
        }
        return lista;
    }

    private Matricula criarMatricula(Aluno a, Plano plano, int idxUnidade,
                                     LocalDate dataInicio) {
        Matricula m = new Matricula();
        m.setAluno(a);
        m.setPlano(plano);
        m.setUnidade(unidades.get(idxUnidade));
        List<Funcionario> cons = consultoresPorUnidade.get(idxUnidade);
        m.setConsultor(cons.get(rnd.nextInt(cons.size())));
        m.setDataMatricula(dataInicio.minusDays(rnd.nextInt(3)));
        m.setDataInicio(dataInicio);
        int meses = plano.getPeriodicidade().getMeses();
        m.setDataFim(dataInicio.plusMonths(meses).minusDays(1));

        // serviços adicionais (com correlação: quem contrata personal
        // costuma pegar nutricionista)
        String cat = plano.getCategoria().getNome();
        boolean premium = cat.equals("Premium");
        if (rnd.nextDouble() < (premium ? 0.40 : 0.08)) {
            m.getServicos().add(servicos.get(0));                          // Personal
            if (rnd.nextDouble() < 0.55)
                m.getServicos().add(servicos.get(2)); // Nutricionista
            if (rnd.nextDouble() < 0.45)
                m.getServicos().add(servicos.get(3)); // Avaliação mensal
        } else if (rnd.nextDouble() < 0.10) {
            m.getServicos().add(servicos.get(2));
        }
        if (rnd.nextDouble() < (premium ? 0.35 : 0.12)) {
            m.getServicos().add(servicos.get(1));                  // Armário
            if (rnd.nextDouble() < 0.60)
                m.getServicos().add(servicos.get(4)); // Toalha
        }

        BigDecimal mensalServicos = BigDecimal.ZERO;
        for (ServicoAdicional s : m.getServicos()) {
            mensalServicos = mensalServicos.add(s.getValorMensal());
        }
        BigDecimal bruto =
                plano.getValorTotal().add(mensalServicos.multiply(BigDecimal.valueOf(meses)))
                ;

        // forma de pagamento e parcelamento
        FormaPagamento forma = sortearForma();
        boolean aVista = meses > 1 && (forma == FormaPagamento.PIX || forma
                == FormaPagamento.DINHEIRO
                || rnd.nextDouble() < 0.20);
        int parcelas = aVista ? 1 : meses;

        // desconto: promoções + 5% extra para pagamento à vista
        double pct = new double[]{0, 0, 0, 0.05, 0.05, 0.10, 0.10, 0.15}
                [rnd.nextInt(8)];
        if (aVista) pct += 0.05;
        if (dataInicio.getMonthValue() == 1 && rnd.nextDouble() < 0.5) pct +=
                0.05; // promoção de verão
        BigDecimal desconto =
                bruto.multiply(BigDecimal.valueOf(pct)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal liquido = bruto.subtract(desconto);

        m.setValorBruto(bruto);
        m.setDesconto(desconto);
        m.setValorLiquido(liquido);

        // status da matrícula
        boolean cancela = rnd.nextDouble() < 0.06;
        LocalDate dataCancelamento = null;
        if (cancela) {
            long dias = ChronoUnit.DAYS.between(dataInicio, m.getDataFim());
            dataCancelamento = dataInicio.plusDays(5 + (long)
                    (rnd.nextDouble() * Math.max(1, dias - 5)));
            if (dataCancelamento.isAfter(DATA_REFERENCIA)) {
                cancela = false;
                dataCancelamento = null;
            }
        }
        if (cancela) {
            m.setStatus(StatusMatricula.CANCELADA);
        } else if (m.getDataFim().isBefore(DATA_REFERENCIA)) {
            m.setStatus(StatusMatricula.ENCERRADA);
        } else {
            m.setStatus(StatusMatricula.ATIVA);
        }

        // parcelas
        BigDecimal valorParcela =
                liquido.divide(BigDecimal.valueOf(parcelas), 2, RoundingMode.DOWN);
        BigDecimal resto =
                liquido.subtract(valorParcela.multiply(BigDecimal.valueOf(parcelas)));
        boolean mauPagador = rnd.nextDouble() < 0.07;
        for (int i = 1; i <= parcelas; i++) {
            Pagamento p = new Pagamento();
            p.setNumeroParcela(i);
            LocalDate venc = dataInicio.plusMonths(i - 1);
            p.setDataVencimento(venc);
            p.setValor(i == 1 ? valorParcela.add(resto) : valorParcela);
            p.setFormaPagamento(forma);

            if (dataCancelamento != null && venc.isAfter(dataCancelamento)) {
                p.setStatus(StatusPagamento.CANCELADO);
            } else if (venc.isAfter(DATA_REFERENCIA)) {
                p.setStatus(StatusPagamento.PENDENTE);
            } else if (mauPagador && i > 1 && rnd.nextDouble() < 0.5
                    && ChronoUnit.DAYS.between(venc, DATA_REFERENCIA) < 120)
            {
                p.setStatus(StatusPagamento.ATRASADO);

            } else {
                p.setStatus(StatusPagamento.PAGO);
                int atraso = mauPagador ? rnd.nextInt(25) : (rnd.nextDouble()
                        < 0.8 ? 0 : rnd.nextInt(6));
                LocalDate pg = venc.plusDays(atraso);
                p.setDataPagamento(pg.isAfter(DATA_REFERENCIA) ?
                        DATA_REFERENCIA : pg);
            }
            p.setRegistradoEm(java.sql.Timestamp.valueOf(
                    (p.getDataPagamento() != null ? p.getDataPagamento() :
                            venc).atTime(8 + rnd.nextInt(13), rnd.nextInt(60))));
            m.adicionarPagamento(p);
        }
        return m;
    }

    private int escolherUnidade() {
        double r = rnd.nextDouble();
        if (r < 0.30) return 0;
        if (r < 0.52) return 1;
        if (r < 0.66) return 2;
        if (r < 0.83) return 3;
        return 4;
    }

    private static final double[] PESO_MES = {1.8, 1.3, 1.5, 1.0, 0.9, 0.8,
            1.0, 1.1, 0.9, 0.9, 0.8, 0.5};

    private LocalDate sortearInicioAluno() {
        long dias = ChronoUnit.DAYS.between(INICIO_HISTORICO,
                DATA_REFERENCIA);
        while (true) {
            LocalDate d = INICIO_HISTORICO.plusDays((long) (rnd.nextDouble()
                    * dias));
            if (rnd.nextDouble() * 1.8 < PESO_MES[d.getMonthValue() - 1]) {
                return d;
            }
        }
    }

    private LocalDate ajustarSazonalidade(LocalDate d) {
        if (d.getMonthValue() == 12 && rnd.nextDouble() < 0.5) {
            return LocalDate.of(d.getYear() + 1, 1, 2 + rnd.nextInt(20));
        }
        return d;
    }

    private Plano sortearPlano(Aluno a) {
        int idade = a.getDataCadastro().getYear() -
                a.getDataNascimento().getYear();
        // categoria
        int cat;
        double r = rnd.nextDouble();
        if (idade <= 24 && r < 0.45) cat = 0;                     // estudante
        else if (r < 0.50) cat = 1;                       // básico
        else if (r < 0.72) cat = 2;                       // plus
        else if (r < 0.88) cat = 3;                       // premium
        else cat = 4;                                     // corporativo
        // pacote
        int pac;
        r = rnd.nextDouble();
        if (idade >= 55 && r < 0.45) pac = 2;             // aquático para 55+
        else if (r < 0.42) pac = 0;
        else if (r < 0.66) pac = 1;
        else if (r < 0.76) pac = 2;
        else if (r < 0.87) pac = 3;
        else pac = 4;
        // periodicidade
        int per;
        r = rnd.nextDouble();
        if (r < 0.45) per = 0;
        else if (r < 0.65) per = 1;
        else if (r < 0.80) per = 2;
        else per = 3;
        Plano p = planos.get(cat * 20 + pac * 4 + per);
        if (!p.isAtivo()) {
            p = planos.get(cat * 20 + pac * 4 + 1); // descontinuado ->
            // trimestral
        }
        return p;
    }

    private FormaPagamento sortearForma() {
        double r = rnd.nextDouble();
        if (r < 0.38) return FormaPagamento.CARTAO_CREDITO;

        if (r < 0.70) return FormaPagamento.PIX;
        if (r < 0.84) return FormaPagamento.CARTAO_DEBITO;
        if (r < 0.95) return FormaPagamento.BOLETO;
        return FormaPagamento.DINHEIRO;
    }

    private int sortearIdade() {
        double r = rnd.nextDouble();
        if (r < 0.25) return 16 + rnd.nextInt(9);     // 16-24
        if (r < 0.60) return 25 + rnd.nextInt(10);    // 25-34
        if (r < 0.82) return 35 + rnd.nextInt(10);    // 35-44
        if (r < 0.93) return 45 + rnd.nextInt(10);    // 45-54
        return 55 + rnd.nextInt(20);                  // 55-74
    }

    private void preencherPessoa(Pessoa p, int idxCidade, LocalDate nasc) {
        boolean fem = rnd.nextBoolean();
        String primeiro = fem ? NOMES_F[rnd.nextInt(NOMES_F.length)] :
                NOMES_M[rnd.nextInt(NOMES_M.length)];
        String nome = primeiro + " " +
                SOBRENOMES[rnd.nextInt(SOBRENOMES.length)] + " " +
                SOBRENOMES[rnd.nextInt(SOBRENOMES.length)];
        p.setNome(nome);
        p.setSexo(fem ? Sexo.FEMININO : Sexo.MASCULINO);
        p.setCpf(gerarCpf());
        p.setDataNascimento(nasc);
        p.setEmail(removerAcentos(primeiro.toLowerCase()) + "." +
                removerAcentos(nome.substring(nome.lastIndexOf(' ') + 1).toLowerCase())
                + rnd.nextInt(1000) + "@" + new String[]{"gmail.com",
                "hotmail.com", "outlook.com", "yahoo.com.br"}[rnd.nextInt(4)]);
        String[] c = CIDADES[idxCidade];
        String ddd = c[1].equals("RN") ? "84" : c[1].equals("PB") ? "83" :
                "81";
        p.setTelefone(String.format("(%s) 9%04d-%04d", ddd,
                rnd.nextInt(10000), rnd.nextInt(10000)));
        String bairro = c[2 + rnd.nextInt(c.length - 2)];
        p.setEndereco(new Endereco(RUAS[rnd.nextInt(RUAS.length)],
                String.valueOf(1 + rnd.nextInt(2500)),
                bairro, c[0], c[1], cep(c[1])));
    }

    private String gerarCpf() {

        while (true) {
            int[] d = new int[11];
            for (int i = 0; i < 9; i++) d[i] = rnd.nextInt(10);
            d[9] = digitoCpf(d, 9);
            d[10] = digitoCpf(d, 10);
            String cpf = String.format("%d%d%d.%d%d%d.%d%d%d-%d%d", d[0],
                    d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8], d[9], d[10]);
            if (cpfsUsados.add(cpf)) return cpf;
        }
    }

    private static int digitoCpf(int[] d, int n) {
        int soma = 0;
        for (int i = 0; i < n; i++) soma += d[i] * (n + 1 - i);
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    private String cep(String uf) {
        String prefixo = uf.equals("RN") ? "59" : uf.equals("PB") ? "58" :
                "5";
        int resto = uf.equals("PE") ? 1000 + rnd.nextInt(8999) :
                rnd.nextInt(1000);
        String base = uf.equals("PE") ? prefixo + resto : prefixo +
                String.format("%03d", resto);
        return base + "-" + String.format("%03d", rnd.nextInt(1000));
    }

    private static String cnpj(int i) {
        return String.format("12.345.678/%04d-%02d", i + 1, 10 + i * 7);
    }

    private BigDecimal salario(Cargo c) {
        int base;
        switch (c) {
            case GERENTE: base = 6500; break;
            case INSTRUTOR: base = 2800; break;
            case CONSULTOR_VENDAS: base = 2400; break;
            default: base = 1800;
        }
        return BigDecimal.valueOf(base + rnd.nextInt(800)).setScale(2);
    }

    private static String capitalizar(String s) {
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    private static String removerAcentos(String s) {
        return java.text.Normalizer.normalize(s,
                java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}