package br.edu.bd2.academia.dao;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;

import javax.persistence.EntityTransaction;

import javax.persistence.criteria.CriteriaBuilder;

import javax.persistence.criteria.CriteriaQuery;

/**
 * DAO genérico com o CRUD básico para qualquer entidade.
 *
 * T = tipo da entidade (Aluno, Plano...)
 * ID = tipo da chave primária (Long)
 *
 * Toda operação de escrita roda dentro de uma transação: se der erro,
 * faz rollback e relança a exceção.
 */
public class GenericDAO<T, ID> {

    protected final EntityManager em;
    private final Class<T> classe;

    public GenericDAO(EntityManager em, Class<T> classe) {
        this.em = em;
        this.classe = classe;
    }

    /** CREATE: entidade nova (new) passa para managed e é inserida no commit. */
    public T salvar(T entidade) {
        executarEmTransacao(e -> e.persist(entidade));
        return entidade;
    }

    /** UPDATE: copia o estado de uma entidade detached para uma managed. */
    public T atualizar(T entidade) {
        return executarComRetorno(e -> e.merge(entidade));
    }

    /** DELETE: managed -> removed; o DELETE é enviado no commit. */
    public void remover(ID id) {
        executarEmTransacao(e -> {
            T entidade = e.find(classe, id);
            if (entidade != null) {
                e.remove(entidade);
            }
        });
    }

    /** READ por chave primária (consulta primeiro o cache de 1º nível). */
    public T buscarPorId(ID id) {
        return em.find(classe, id);
    }

    /** READ de todos, via Criteria (funciona para qualquer T). */
    public List<T> listarTodos() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(classe);
        cq.select(cq.from(classe));
        return em.createQuery(cq).getResultList();
    }

    /** READ paginado: pagina começa em 0. */
    public List<T> listarPaginado(int pagina, int tamanho) {
        return em.createQuery("SELECT x FROM " + classe.getSimpleName() + " x ORDER BY x.id", classe)
                 .setFirstResult(pagina * tamanho)
                 .setMaxResults(tamanho)
                 .getResultList();
    }

    public long contar() {
        return em.createQuery("SELECT COUNT(x) FROM " + classe.getSimpleName() + " x", Long.class)
                 .getSingleResult();
    }

    // ---------- controle de transação ----------
    protected void executarEmTransacao(Consumer<EntityManager> acao) {
        executarComRetorno(e -> { acao.accept(e); return null; });
    }

    protected <R> R executarComRetorno(Function<EntityManager, R> acao) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R resultado = acao.apply(em);
            tx.commit();
            return resultado;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }
    }
}
