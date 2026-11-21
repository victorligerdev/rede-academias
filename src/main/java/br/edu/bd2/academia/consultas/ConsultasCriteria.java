package br.edu.bd2.academia.consultas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.edu.bd2.academia.model.Aluno;
import br.edu.bd2.academia.model.CategoriaPlano;
import br.edu.bd2.academia.model.Matricula;
import br.edu.bd2.academia.model.Pagamento;
import br.edu.bd2.academia.model.Plano;
import br.edu.bd2.academia.model.enums.StatusMatricula;
import br.edu.bd2.academia.model.enums.StatusPagamento;
import br.edu.bd2.academia.model.Unidade;

public class ConsultasCriteria {

    private final EntityManager em;

    public ConsultasCriteria(EntityManager em) {
        this.em = em;
    }

    public List<Matricula> c1BuscarMatriculas(FiltroMatricula f, int
            maxResultados) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Matricula> cq = cb.createQuery(Matricula.class);
        Root<Matricula> m = cq.from(Matricula.class);

        // JOIN FETCH: carrega aluno, plano e unidade no mesmo SELECT (evita
        // N+1 na listagem)
        m.fetch("aluno");
        m.fetch("plano");
        m.fetch("unidade");

        // Caminhos usados nos filtros (navegação por atributos, como na
        // JPQL: m.plano.categoria.nome)
        Path<Plano> plano = m.get("plano");
        Path<Aluno> aluno = m.get("aluno");
        Path<Unidade> unidade = m.get("unidade");

        List<Predicate> filtros = new ArrayList<>();

        if (f.unidade != null) {
            filtros.add(cb.equal(unidade.get("nome"), f.unidade));
        }
        if (f.categoria != null) {
            filtros.add(cb.equal(plano.get("categoria").get("nome"),
                    f.categoria));
        }

        if (f.periodicidade != null) {
            filtros.add(cb.equal(plano.get("periodicidade"),
                    f.periodicidade));
        }
        if (f.status != null) {
            filtros.add(cb.equal(m.get("status"), f.status));
        }
        if (f.dataDe != null) {
            filtros.add(cb.greaterThanOrEqualTo(m.get("dataMatricula"),
                    f.dataDe));
        }
        if (f.dataAte != null) {
            filtros.add(cb.lessThanOrEqualTo(m.get("dataMatricula"),
                    f.dataAte));
        }
        if (f.valorMinimo != null) {
            filtros.add(cb.greaterThanOrEqualTo(m.
                    <BigDecimal>get("valorLiquido"), f.valorMinimo));
        }
        if (f.nomeAluno != null) {
            filtros.add(cb.like(cb.lower(aluno.get("nome")), "%" +
                    f.nomeAluno.toLowerCase() + "%"));
        }

        cq.select(m)
                .where(filtros.toArray(new Predicate[0]))
                .orderBy(cb.desc(m.get("dataMatricula")));

        return
                em.createQuery(cq).setMaxResults(maxResultados).getResultList();
    }

    public long c1Contar(FiltroMatricula f) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Matricula> m = cq.from(Matricula.class);
        Join<Matricula, Plano> plano = m.join("plano");
        List<Predicate> filtros = new ArrayList<>();
        if (f.unidade != null)
            filtros.add(cb.equal(m.join("unidade").get("nome"), f.unidade));
        if (f.categoria != null)
            filtros.add(cb.equal(plano.join("categoria").get("nome"), f.categoria));
        if (f.periodicidade != null)
            filtros.add(cb.equal(plano.get("periodicidade"), f.periodicidade));

        if (f.status != null) filtros.add(cb.equal(m.get("status"),
                f.status));
        if (f.dataDe != null)
            filtros.add(cb.greaterThanOrEqualTo(m.get("dataMatricula"), f.dataDe));
        if (f.dataAte != null)
            filtros.add(cb.lessThanOrEqualTo(m.get("dataMatricula"), f.dataAte));
        if (f.valorMinimo != null) filtros.add(cb.greaterThanOrEqualTo(m.
                <BigDecimal>get("valorLiquido"), f.valorMinimo));
        if (f.nomeAluno != null)
            filtros.add(cb.like(cb.lower(m.join("aluno").get("nome")), "%" +
                    f.nomeAluno.toLowerCase() + "%"));
        cq.select(cb.count(m)).where(filtros.toArray(new Predicate[0]));
        return em.createQuery(cq).getSingleResult();
    }

    public List<Object[]> c2ResumoPorCategoria(BigDecimal receitaMinima) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Matricula> m = cq.from(Matricula.class);
        Join<Matricula, Plano> p = m.join("plano");
        Join<Plano, CategoriaPlano> c = p.join("categoria");

        Expression<BigDecimal> valor = m.get("valorLiquido");
        Expression<BigDecimal> soma = cb.sum(valor);

        cq.multiselect(c.get("nome"), cb.count(m), cb.avg(valor), soma)
                .where(cb.notEqual(m.get("status"), StatusMatricula.CANCELADA))
                .groupBy(c.get("nome"))
                .having(cb.gt(soma, receitaMinima))
                .orderBy(cb.desc(soma));

        return em.createQuery(cq).getResultList();
    }

    public List<Aluno> c3AtivosComParcelaAtrasada() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Aluno> cq = cb.createQuery(Aluno.class);
        Root<Aluno> a = cq.from(Aluno.class);

        // subconsulta 1: o aluno tem matrícula ativa?
        Subquery<Long> ativa = cq.subquery(Long.class);
        Root<Matricula> m = ativa.from(Matricula.class);
        ativa.select(m.<Long>get("id"))
                .where(cb.equal(m.get("aluno"), a),
                        cb.equal(m.get("status"), StatusMatricula.ATIVA));

        Subquery<Long> atrasada = cq.subquery(Long.class);
        Root<Pagamento> pg = atrasada.from(Pagamento.class);
        Join<Pagamento, Matricula> pm = pg.join("matricula");
        atrasada.select(pg.<Long>get("id"))
                .where(cb.equal(pm.get("aluno"), a),
                        cb.equal(pg.get("status"), StatusPagamento.ATRASADO));

        cq.select(a)
                .where(cb.exists(ativa), cb.exists(atrasada))
                .orderBy(cb.asc(a.get("nome")));

        return em.createQuery(cq).getResultList();
    }
}