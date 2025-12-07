package org.example.fichajes.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.example.fichajes.model.Fichaje;
import org.example.fichajes.util.DatabaseConnection;

import java.time.LocalDate;
import java.util.List;

public class FichajeDAO {

    public List<Fichaje> getAllFichajes() {
        EntityManager em = DatabaseConnection.getEntityManager();
        try {
            TypedQuery<Fichaje> query = em.createQuery(
                "SELECT f FROM Fichaje f ORDER BY f.fecha DESC, f.hora DESC",
                Fichaje.class
            );
            List<Fichaje> fichajes = query.getResultList();
            fichajes.forEach(f -> f.setTrabajadorNombre(f.getTrabajador().getNombreCompleto()));
            return fichajes;
        } finally {
            em.close();
        }
    }

    public List<Fichaje> getFichajesByTrabajador(int trabajadorId) {
        EntityManager em = DatabaseConnection.getEntityManager();
        try {
            TypedQuery<Fichaje> query = em.createQuery(
                "SELECT f FROM Fichaje f WHERE f.trabajador.id = :trabajadorId ORDER BY f.fecha DESC, f.hora DESC",
                Fichaje.class
            );
            query.setParameter("trabajadorId", trabajadorId);
            List<Fichaje> fichajes = query.getResultList();
            fichajes.forEach(f -> f.setTrabajadorNombre(f.getTrabajador().getNombreCompleto()));
            return fichajes;
        } finally {
            em.close();
        }
    }

    public List<Fichaje> getFichajesByFecha(LocalDate fecha) {
        EntityManager em = DatabaseConnection.getEntityManager();
        try {
            TypedQuery<Fichaje> query = em.createQuery(
                "SELECT f FROM Fichaje f WHERE f.fecha = :fecha ORDER BY f.hora",
                Fichaje.class
            );
            query.setParameter("fecha", fecha);
            List<Fichaje> fichajes = query.getResultList();
            fichajes.forEach(f -> f.setTrabajadorNombre(f.getTrabajador().getNombreCompleto()));
            return fichajes;
        } finally {
            em.close();
        }
    }

    public List<Fichaje> getFichajesByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        EntityManager em = DatabaseConnection.getEntityManager();
        try {
            TypedQuery<Fichaje> query = em.createQuery(
                "SELECT f FROM Fichaje f WHERE f.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY f.fecha DESC, f.hora DESC",
                Fichaje.class
            );
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);
            List<Fichaje> fichajes = query.getResultList();
            fichajes.forEach(f -> f.setTrabajadorNombre(f.getTrabajador().getNombreCompleto()));
            return fichajes;
        } finally {
            em.close();
        }
    }

    public int insertFichaje(Fichaje fichaje) {
        EntityManager em = DatabaseConnection.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(fichaje);
            transaction.commit();
            return fichaje.getId();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean updateFichaje(Fichaje fichaje) {
        EntityManager em = DatabaseConnection.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(fichaje);
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

    public boolean deleteFichaje(int id) {
        EntityManager em = DatabaseConnection.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Fichaje fichaje = em.find(Fichaje.class, id);
            if (fichaje != null) {
                em.remove(fichaje);
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

