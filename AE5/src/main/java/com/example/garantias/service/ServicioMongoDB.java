package com.example.garantias.service;

import com.example.garantias.model.Garantia;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.ZoneId;

public class ServicioMongoDB {
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;
    private final Gson gson = new Gson();

    public ServicioMongoDB(String uri, String dbName) {
        this.client = MongoClients.create(uri);
        this.database = client.getDatabase(dbName);
        this.collection = database.getCollection("warranties");
    }

    public void insertarGarantia(Garantia g) {
        Document doc = Document.parse(gson.toJson(g));
        collection.insertOne(doc);
    }

    public boolean existeGarantiaPorLinea(int lineaFacturaId) {
        Document found = collection.find(new Document("lineaFacturaId", lineaFacturaId)).first();
        return found != null;
    }

    public boolean eliminarGarantiaPorLinea(int lineaFacturaId) {
        var res = collection.deleteOne(new Document("lineaFacturaId", lineaFacturaId));
        return res.getDeletedCount() > 0;
    }

    public void crearGarantiaDesdeLinea(int facturaId, String nombreFactura, com.example.garantias.model.LineaFactura linea, String nombreCliente, int mesesGarantia) {
        Garantia g = new Garantia();
        g.setFacturaId(facturaId);
        g.setNombreFactura(nombreFactura);
        g.setLineaFacturaId(linea.getId());
        g.setProductoId(linea.getProductoId());
        g.setNombreProducto(linea.getNombreProducto());
        g.setNombreCliente(nombreCliente);
        java.time.LocalDate hoy = java.time.LocalDate.now();
        g.setFechaCompra(hoy);
        g.setFechaInicioGarantia(hoy);
        g.setFechaFinGarantia(hoy.plusMonths(mesesGarantia));
        g.setMesesGarantia(mesesGarantia);
        g.setEstado(Garantia.Estado.ACTIVA);
        g.setNumeroSerie(linea.getNumeroSerie());
        insertarGarantia(g);
    }

    public java.util.List<Garantia> obtenerGarantias() {
        java.util.List<Garantia> res = new java.util.ArrayList<>();
        for (Document d : collection.find()) {
            Garantia g = new Garantia();
            if (d.containsKey("facturaId")) g.setFacturaId(d.getInteger("facturaId"));
            g.setNombreFactura(d.getString("nombreFactura"));
            if (d.containsKey("lineaFacturaId")) g.setLineaFacturaId(d.getInteger("lineaFacturaId"));
            if (d.containsKey("productoId")) g.setProductoId(d.getInteger("productoId"));
            g.setNombreProducto(d.getString("nombreProducto"));
            g.setNombreCliente(d.getString("nombreCliente"));
            // fechas: pueden estar como Date o String
            try {
                Object fInicio = d.get("fechaInicioGarantia");
                if (fInicio instanceof java.util.Date) {
                    g.setFechaInicioGarantia(((java.util.Date) fInicio).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                } else if (fInicio instanceof String) {
                    g.setFechaInicioGarantia(java.time.LocalDate.parse((String) fInicio));
                }
            } catch (Exception ignored) {}
            try {
                Object fFin = d.get("fechaFinGarantia");
                if (fFin instanceof java.util.Date) {
                    g.setFechaFinGarantia(((java.util.Date) fFin).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                } else if (fFin instanceof String) {
                    g.setFechaFinGarantia(java.time.LocalDate.parse((String) fFin));
                }
            } catch (Exception ignored) {}
            try { if (d.containsKey("mesesGarantia")) g.setMesesGarantia(d.getInteger("mesesGarantia")); } catch (Exception ignored) {}
            String estado = d.getString("estado");
            if (estado != null) {
                try { g.setEstado(Garantia.Estado.valueOf(estado)); } catch (Exception ignored) {}
            } else {
                // calcular estado por fechas
                if (g.getFechaFinGarantia() != null) {
                    java.time.LocalDate hoy = java.time.LocalDate.now();
                    long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, g.getFechaFinGarantia());
                    if (dias < 0) g.setEstado(Garantia.Estado.EXPIRADA);
                    else if (dias < 30) g.setEstado(Garantia.Estado.POR_EXPIRAR);
                    else g.setEstado(Garantia.Estado.ACTIVA);
                }
            }
            g.setNotas(d.getString("notas"));
            g.setNumeroSerie(d.getString("numeroSerie"));
            res.add(g);
        }
        return res;
    }

    public long contarGarantiasPorEstado(Garantia.Estado estado) {
        Document filter = new Document("estado", estado.name());
        return collection.countDocuments(filter);
    }
}

