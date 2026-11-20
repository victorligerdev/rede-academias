package br.edu.bd2.academia.util;

import javax.persistence.EntityManager;

import javax.persistence.EntityManagerFactory;

import javax.persistence.Persistence;

/**
 * O EntityManagerFactory é caro (lê o mapeamento, abre o pool de conexões),
 * então existe um só por aplicação. Já o EntityManager é leve e criado por
 * unidade de trabalho.
 */
public final class JPAUtil {

    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("academiaPU");
    private JPAUtil() { }
    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public static void fechar() {
        if (EMF.isOpen()) {
            EMF.close();
        }
    }
}
