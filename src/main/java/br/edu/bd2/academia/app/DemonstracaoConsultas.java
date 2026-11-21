package br.edu.bd2.academia.app;

import br.edu.bd2.academia.consultas.ConsultasCriteria;
import br.edu.bd2.academia.consultas.ConsultasJPQL;
import br.edu.bd2.academia.consultas.FiltroMatricula;
import br.edu.bd2.academia.model.Matricula;
import br.edu.bd2.academia.model.Pagamento;
import br.edu.bd2.academia.model.Plano;
import br.edu.bd2.academia.model.enums.Periodicidade;
import br.edu.bd2.academia.model.enums.StatusMatricula;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Executa as consultas JPQL e Criteria com parâmetros ilustrativos. */
public class DemonstracaoConsultas {
    private final EntityManager em;
    private final ConsultasJPQL jpql;
    private final ConsultasCriteria criteria;

    public DemonstracaoConsultas(EntityManager em) {
        this.em = em;
        this.jpql = new ConsultasJPQL(em);
        this.criteria = new ConsultasCriteria(em);
    }

    public void executarJPQL(int consulta) {
        em.clear();
        titulo("JPQL Q" + consulta);
        switch (consulta) {
            case 1:
                jpql.q1AlunosAtivosPorModalidade().forEach(r ->
                        System.out.printf("%-20s %d alunos%n", r[0], r[1]));
                break;
            case 2:
                Long alunoId = em.createQuery("SELECT m.aluno.id FROM Matricula m GROUP BY m.aluno.id ORDER BY COUNT(m) DESC", Long.class)
                        .setMaxResults(1).getSingleResult();
                for (Matricula m : jpql.q2HistoricoDoAluno(alunoId)) {
                    System.out.println(m);
                }
                break;
            case 3:
                jpql.q3FaturamentoPorUnidade(new BigDecimal("150000")).forEach(r ->
                        System.out.printf("%-25s %d matrículas R$ %s%n", r[0], r[1], r[2]));
                break;
            case 4:
                jpql.q4PlanosMaisVendidos(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), 25)
                        .forEach(r -> System.out.printf("%-12s %-40s %d matrículas R$ %s%n", r[0], r[1], r[2], r[3]));
                break;
            case 5:
                for (Plano p : jpql.q5PlanosAcimaDaMediaDaCategoria()) {
                    System.out.printf("%-12s R$ %s%n", p.getNome(), p.getValorMensal());
                }
                break;
            case 6:
                jpql.q6AlunosSemMatricula().forEach(a -> System.out.println(a));
                break;
            case 7:
                LocalDate hoje = LocalDate.now();
                for (Pagamento p : jpql.q7ParcelasAtrasadas(hoje.minusDays(120), hoje)) {
                    System.out.println(p);
                }
                break;
            case 8:
                LocalDate hojeAVencer = LocalDate.now();
                for (Matricula m : jpql.q8MatriculasAVencer(hojeAVencer, 30, 0, 10)) {
                    System.out.println(m);
                }
                break;
            case 9:
                jpql.q9RecebimentosPorFormaPagamento(LocalDate.of(2025, 1, 1), LocalDate.of(2026, 12, 31))
                        .forEach(r -> System.out.printf("%-15s %d parcelas R$ %s%n", r[0], r[1], r[2]));
                break;
            case 10:
                jpql.q10RankingConsultores().forEach(System.out::println);
                break;
            default:
                System.out.println("Consulta inválida.");
        }
    }

    public void executarCriteria(int consulta) {
        em.clear();
        titulo("Criteria C" + consulta);
        switch (consulta) {
            case 1:
                FiltroMatricula[] filtros = {
                        new FiltroMatricula(),
                        new FiltroMatricula().unidade("Vértice Recife").status(StatusMatricula.ATIVA),
                        new FiltroMatricula().categoria("Premium")
                                .periodicidade(Periodicidade.ANUAL)
                                .dataDe(LocalDate.of(2025, 1, 1))
                                .dataAte(LocalDate.of(2025, 12, 31)),
                        new FiltroMatricula().nomeAluno("silva").valorMinimo(new BigDecimal("1500"))
                };
                for (FiltroMatricula filtro : filtros) {
                    System.out.println("Filtros: " + filtro);
                    System.out.println("Total: " + criteria.c1Contar(filtro));
                    for (Matricula m : criteria.c1BuscarMatriculas(filtro, 3)) {
                        System.out.println(m);
                    }
                }
                break;
            case 2:
                criteria.c2ResumoPorCategoria(new BigDecimal("50000")).forEach(r ->
                        System.out.printf("%-12s %d matrículas, receita R$ %s%n", r[0], r[1], r[3]));
                break;
            case 3:
                criteria.c3AtivosComParcelaAtrasada().forEach(a ->
                        System.out.printf("%-35s %s%n", a.getNome(), a.getTelefone()));
                break;
            default:
                System.out.println("Consulta inválida.");
        }
    }

    private static void titulo(String titulo) {
        System.out.println("\n=== " + titulo + " ===");
    }
}
