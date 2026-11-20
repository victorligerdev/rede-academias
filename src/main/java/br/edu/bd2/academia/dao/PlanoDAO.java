package br.edu.bd2.academia.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.edu.bd2.academia.model.CategoriaPlano;
import br.edu.bd2.academia.model.Plano;

public class PlanoDAO extends GenericDAO<Plano, Long> {

    public PlanoDAO(EntityManager em) {
        super(em, Plano.class);
    }

    /** Planos com categoria já carregada (JOIN FETCH evita o N+1 na listagem). */
    public List<Plano> listarComCategoria(int pagina, int tamanho) {
        return em.createQuery("SELECT p FROM Plano p JOIN FETCH p.categoria ORDER BY p.codigo", Plano.class)
                 .setFirstResult(pagina * tamanho)
                 .setMaxResults(tamanho)
                 .getResultList();
    }

    public List<CategoriaPlano> listarCategorias() {
        return em.createQuery("SELECT c FROM CategoriaPlano c ORDER BY c.id", CategoriaPlano.class)
                 .getResultList();
    }

    /** Exclusão lógica: plano com matrículas não pode ser apagado (FK), só desativado. */
    public boolean possuiMatriculas(Long planoId) {
        return em.createQuery("SELECT COUNT(m) FROM Matricula m WHERE m.plano.id = :id", Long.class)
                 .setParameter("id", planoId)
                 .getSingleResult() > 0;
    }
}
