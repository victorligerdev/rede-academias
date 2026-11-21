package br.edu.bd2.academia.consultas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import br.edu.bd2.academia.model.Aluno;
import br.edu.bd2.academia.model.Matricula;
import br.edu.bd2.academia.model.Pagamento;
import br.edu.bd2.academia.model.Plano;
import br.edu.bd2.academia.model.enums.StatusMatricula;
import br.edu.bd2.academia.model.enums.StatusPagamento;


public class ConsultasJPQL {

    private final EntityManager em;

    public ConsultasJPQL(EntityManager em) {
        this.em = em;
    }


    public List<Object[]> q1AlunosAtivosPorModalidade() {
        return em.createQuery(
                        "SELECT md.nome, COUNT(DISTINCT a.id) "
                                + "FROM Matricula m "
                                + "JOIN m.aluno a "
                                + "JOIN m.plano p "
                                + "JOIN p.modalidades md "
                                + "WHERE m.status = :status "
                                + "GROUP BY md.nome "
                                + "ORDER BY COUNT(DISTINCT a.id) DESC", Object[].class)
                .setParameter("status", StatusMatricula.ATIVA)
                .getResultList();

    }

    public List<Matricula> q2HistoricoDoAluno(Long alunoId) {
        return em.createQuery(
                        "SELECT DISTINCT m FROM Matricula m "
                                + "JOIN FETCH m.plano p "
                                + "JOIN FETCH m.unidade u "
                                + "LEFT JOIN FETCH m.pagamentos pg "
                                + "WHERE m.aluno.id = :alunoId "
                                + "ORDER BY m.dataMatricula", Matricula.class)
                .setParameter("alunoId", alunoId)
                .getResultList();
    }

    public List<Object[]> q3FaturamentoPorUnidade(BigDecimal minimo) {
        return em.createNamedQuery("Matricula.faturamentoPorUnidade",
                        Object[].class)
                .setParameter("cancelada", StatusMatricula.CANCELADA)
                .setParameter("minimo", minimo)
                .getResultList();
    }

    public List<Object[]> q4PlanosMaisVendidos(LocalDate inicio, LocalDate
            fim, long minimoMatriculas) {
        return em.createQuery(
                        "SELECT p.codigo, p.nome, COUNT(m), SUM(m.valorLiquido) "
                                + "FROM Matricula m JOIN m.plano p "
                                + "WHERE m.dataMatricula BETWEEN :inicio AND :fim "

                                + "GROUP BY p.codigo, p.nome "
                                + "HAVING COUNT(m) >= :minimo "
                                + "ORDER BY COUNT(m) DESC", Object[].class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .setParameter("minimo", minimoMatriculas)
                .getResultList();
    }


    public List<Plano> q5PlanosAcimaDaMediaDaCategoria() {
        return em.createQuery(
                        "SELECT p FROM Plano p JOIN FETCH p.categoria c "
                                + "WHERE p.valorMensal > ("
                                + "   SELECT AVG(p2.valorMensal) FROM Plano p2 WHERE p2.categoria = p.categoria"
                                + ") ORDER BY c.nome, p.valorMensal DESC", Plano.class)
                .getResultList();
    }


    public List<Aluno> q6AlunosSemMatricula() {
        return em.createNamedQuery("Aluno.semMatricula",
                Aluno.class).getResultList();
    }


    public List<Pagamento> q7ParcelasAtrasadas(LocalDate de, LocalDate ate) {
        return em.createQuery(
                        "SELECT pg FROM Pagamento pg "
                                + "JOIN FETCH pg.matricula m "
                                + "JOIN FETCH m.aluno a "
                                + "WHERE pg.status = :status "

                                + "AND pg.dataVencimento BETWEEN :de AND :ate "
                                + "ORDER BY pg.dataVencimento, a.nome", Pagamento.class)
                .setParameter("status", StatusPagamento.ATRASADO)
                .setParameter("de", de)
                .setParameter("ate", ate)
                .getResultList();
    }


    public List<Matricula> q8MatriculasAVencer(LocalDate hoje, int dias, int
            pagina, int tamanhoPagina) {
        return em.createQuery(
                        "SELECT m FROM Matricula m "
                                + "JOIN FETCH m.aluno a JOIN FETCH m.plano p "
                                + "WHERE m.status = :ativa AND m.dataFim BETWEEN :hoje AND :limite "
                                + "ORDER BY m.dataFim, a.nome", Matricula.class)
                .setParameter("ativa", StatusMatricula.ATIVA)
                .setParameter("hoje", hoje)
                .setParameter("limite", hoje.plusDays(dias))
                .setFirstResult(pagina * tamanhoPagina)
                .setMaxResults(tamanhoPagina)
                .getResultList();
    }

    public long q8ContarMatriculasAVencer(LocalDate hoje, int dias) {
        return em.createQuery(
                        "SELECT COUNT(m) FROM Matricula m "
                                + "WHERE m.status = :ativa AND m.dataFim BETWEEN :hoje AND :limite", Long.class)
                .setParameter("ativa", StatusMatricula.ATIVA)
                .setParameter("hoje", hoje)
                .setParameter("limite", hoje.plusDays(dias))
                .getSingleResult();
    }

    public List<Object[]> q9RecebimentosPorFormaPagamento(LocalDate inicio,
                                                          LocalDate fim) {
        return em.createQuery(
                        "SELECT pg.formaPagamento, COUNT(pg), SUM(pg.valor), AVG(pg.valor), MIN(pg.valor), MAX(pg.valor) "
                                + "FROM Pagamento pg "
                                + "WHERE pg.status = :pago AND pg.dataPagamento BETWEEN :inicio AND :fim "
                                + "GROUP BY pg.formaPagamento "
                                + "ORDER BY SUM(pg.valor) DESC", Object[].class)
                .setParameter("pago", StatusPagamento.PAGO)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    public List<ResumoConsultor> q10RankingConsultores() {
        return em.createQuery(
                        "SELECT NEW br.edu.bd2.academia.consultas.ResumoConsultor("
                                + "   f.nome, u.nome, COUNT(m), SUM(m.valorLiquido)) "
                                + "FROM Matricula m JOIN m.consultor f JOIN f.unidade u "
                                + "WHERE m.status <> :cancelada "
                                + "GROUP BY f.id, f.nome, u.nome "
                                + "ORDER BY SUM(m.valorLiquido) DESC", ResumoConsultor.class)
                .setParameter("cancelada", StatusMatricula.CANCELADA)
                .getResultList();
    }
}