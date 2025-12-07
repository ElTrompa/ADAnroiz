package com.example.ae4;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class FichajeDAO {

    /**
     * Obtiene todos los fichajes de la base de datos
     * @return lista de fichajes
     */
    public static List<Fichaje> getAllFichajes() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Fichaje> fichajes = session.createQuery("FROM Fichaje", Fichaje.class).list();
        session.close();
        return fichajes;
    }

    /**
     * Guarda un fichaje en la base de datos
     * @param fichaje objeto Fichaje a guardar
     */
    public static void saveFichaje(Fichaje fichaje) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(fichaje);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Borra un fichaje de la base de datos
     * @param fichaje objeto Fichaje a eliminar
     */
    public static void deleteFichaje(Fichaje fichaje) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(fichaje);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Actualiza un fichaje existente
     * @param fichaje objeto Fichaje con los cambios
     */
    public static void updateFichaje(Fichaje fichaje) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(fichaje);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Ejemplo de búsqueda de fichajes por trabajador
     */
    public static List<Fichaje> getFichajesByTrabajador(int trabajadorId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Fichaje> query = session.createQuery(
                "FROM Fichaje f WHERE f.trabajador.id = :id", Fichaje.class);
        query.setParameter("id", trabajadorId);
        List<Fichaje> fichajes = query.list();
        session.close();
        return fichajes;
    }
}
