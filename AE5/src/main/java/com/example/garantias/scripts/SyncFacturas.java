package com.example.garantias.scripts;

import com.example.garantias.model.LineaFactura;
import com.example.garantias.model.SesionOdoo;
import com.example.garantias.service.ServicioMongoDB;
import com.example.garantias.service.ServicioOdoo;

import java.util.List;

public class SyncFacturas {
    public static void main(String[] args) {
        try {
            java.util.Properties props = new java.util.Properties();
            try (var is = SyncFacturas.class.getResourceAsStream("/application.properties")) {
                props.load(is);
            }
            String url = props.getProperty("odoo.url", "http://localhost:8069");
            String db = props.getProperty("odoo.db", "garantias");
            String mongoUri = props.getProperty("mongodb.uri", "mongodb://admin:admin_password@localhost:27017");
            String mongoDb = props.getProperty("mongodb.db", "garantias_db");
            int mesesGarantia = Integer.parseInt(props.getProperty("meses.garantia", "12"));

            System.out.println("Conectando a Odoo: " + url + " db: " + db);
            ServicioOdoo serv = new ServicioOdoo(url, db);
            // Lectura en modo anónimo / pedir credenciales si no autenticado
            // Para sincronizar, intentamos autenticar con admin@example.com/admin
            boolean authOK = serv.autenticar("admin@example.com", "admin");
            if (!authOK) {
                System.err.println("No se pudo autenticar en Odoo");
                return;
            }
            System.out.println("Autenticado OK");

            System.out.println("Conectando a MongoDB: " + mongoUri + " db: " + mongoDb);
            ServicioMongoDB servM = new ServicioMongoDB(mongoUri, mongoDb);

            List<com.example.garantias.model.Factura> facturas = serv.obtenerFacturasVenta();
            System.out.println("Facturas encontradas: " + facturas.size());
            int creadas = 0; int saltadas = 0;
            for (var f : facturas) {
                if (f.getLineas() != null) {
                    for (LineaFactura l : f.getLineas()) {
                        if (!servM.existeGarantiaPorLinea(l.getId())) {
                            servM.crearGarantiaDesdeLinea(f.getId(), f.getNombre(), l, f.getNombreCliente(), mesesGarantia);
                            System.out.println("Garantia creada para linea: " + l.getId() + " factura: " + f.getId());
                            creadas++;
                        } else {
                            System.out.println("Garantia ya existe para linea: " + l.getId());
                            saltadas++;
                        }
                    }
                }
            }
            System.out.println(String.format("Sincronizacion finalizada. Creadas=%d, Saltadas=%d", creadas, saltadas));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
