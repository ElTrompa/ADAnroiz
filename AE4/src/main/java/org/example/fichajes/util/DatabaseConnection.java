package org.example.fichajes.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseConnection {
    private static EntityManagerFactory entityManagerFactory;

    private DatabaseConnection() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            try {
                entityManagerFactory = Persistence.createEntityManagerFactory("FichajesPU");
                System.out.println("EntityManagerFactory creado exitosamente");
            } catch (Exception e) {
                System.err.println("Error al crear EntityManagerFactory: " + e.getMessage());
                throw e;
            }
        }
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void closeConnection() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("EntityManagerFactory cerrado");
        }
    }

    public static boolean testConnection() {
        try {
            EntityManagerFactory emf = getEntityManagerFactory();
            if (emf != null && emf.isOpen()) {
                EntityManager em = emf.createEntityManager();
                em.close();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al probar la conexion: " + e.getMessage());
            return false;
        }
    }
}

