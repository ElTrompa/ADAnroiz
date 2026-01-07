package com.example.garantias.scripts;

import com.example.garantias.service.ServicioOdoo;

public class CreateOdooDatabase {
    public static void main(String[] args) throws Exception {
        String url = args.length > 0 ? args[0] : "http://localhost:8069";
        String master = args.length > 1 ? args[1] : "admin";
        String db = args.length > 2 ? args[2] : "garantias";
        String adminEmail = args.length > 3 ? args[3] : "admin@example.com";
        String adminPass = args.length > 4 ? args[4] : "admin";
        boolean demo = args.length > 5 ? Boolean.parseBoolean(args[5]) : true;
        String lang = args.length > 6 ? args[6] : "es_ES";

        ServicioOdoo serv = new ServicioOdoo(url, "postgres_dummy");
        System.out.println("Comprobando existencia de la BD: " + db);
        boolean existe = serv.existeBaseDatos(db);
        if (existe) {
            System.out.println("La base de datos ya existe: " + db);
            return;
        }

        System.out.println("Creando la base de datos '" + db + "'...");
        boolean ok = serv.crearBaseDatos(master, db, demo, lang, adminPass, adminEmail);
        if (ok) System.out.println("Base de datos creada correctamente.");
        else System.out.println("Error al crear la base de datos. Revisa los logs de Odoo.");
    }
}