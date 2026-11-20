package br.edu.bd2.academia.dao;

import javax.persistence.EntityManager;

import br.edu.bd2.academia.model.Matricula;

public class MatriculaDAO extends GenericDAO<Matricula, Long> {

    public MatriculaDAO(EntityManager em) {
        super(em, Matricula.class);
    }
}
