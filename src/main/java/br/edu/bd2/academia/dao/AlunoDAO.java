package br.edu.bd2.academia.dao;

import java.util.List;

import javax.persistence.EntityManager;

import javax.persistence.NoResultException;

import br.edu.bd2.academia.model.Aluno;

public class AlunoDAO extends GenericDAO<Aluno, Long> {

    public AlunoDAO(EntityManager em) {
        super(em, Aluno.class);
    }

    public Aluno buscarPorCpf(String cpf) {
        try {
            return em.createQuery("SELECT a FROM Aluno a WHERE a.cpf = :cpf", Aluno.class)
                     .setParameter("cpf", cpf)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Aluno> buscarPorNome(String trecho) {
        return em.createQuery("SELECT a FROM Aluno a WHERE LOWER(a.nome) LIKE :t ORDER BY a.nome", Aluno.class)
                 .setParameter("t", "%" + trecho.toLowerCase() + "%")
                 .setMaxResults(20)
                 .getResultList();
    }

    /** Usa a @NamedQuery declarada em Aluno. */
    public List<Aluno> porCidade(String cidade) {
        return em.createNamedQuery("Aluno.porCidade", Aluno.class)
                 .setParameter("cidade", cidade)
                 .getResultList();
    }
}
