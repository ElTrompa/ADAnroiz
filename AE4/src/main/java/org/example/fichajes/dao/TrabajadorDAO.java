package org.example.fichajes.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.example.fichajes.model.Trabajador;
import org.example.fichajes.util.DatabaseConnection;

import java.util.List;

public class TrabajadorDAO {

    public List<Trabajador> getAllTrabajadores() {
        EntityManager em = DatabaseConnection.getEntityManager();
        try {
            TypedQuery<Trabajador> query = em.createQuery(
                "SELECT t FROM Trabajador t WHERE t.activo = true ORDER BY t.apellidos, t.nombre",
                Trabajador.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Trabajador getTrabajadorById(int id) {
        EntityManager em = DatabaseConnection.getEntityManager();
        try {
            return em.find(Trabajador.class, id);
        } finally {
            em.close();
        }
    }

    public Trabajador getTrabajadorByDni(String dni) {
        EntityManager em = DatabaseConnection.getEntityManager();
        try {
            TypedQuery<Trabajador> query = em.createQuery(
                "SELECT t FROM Trabajador t WHERE t.dni = :dni",
                Trabajador.class
            );
            query.setParameter("dni", dni);
            List<Trabajador> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public int insertTrabajador(Trabajador trabajador) {
        EntityManager em = DatabaseConnection.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(trabajador);
            transaction.commit();
            return trabajador.getId();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean updateTrabajador(Trabajador trabajador) {
        EntityManager em = DatabaseConnection.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(trabajador);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    public boolean deleteTrabajador(int id) {
        EntityManager em = DatabaseConnection.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Trabajador trabajador = em.find(Trabajador.class, id);
            if (trabajador != null) {
                trabajador.setActivo(false);
                em.merge(trabajador);
                transaction.commit();
                return true;
            }
            transaction.rollback();
            return false;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }
}
