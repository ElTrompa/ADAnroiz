package com.example.garantias.service;

import com.example.garantias.model.Factura;
import com.example.garantias.model.LineaFactura;
import com.example.garantias.model.SesionOdoo;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicioOdoo {
    private final String url;
    private final String db;
    private final HttpClient client;
    private final Gson gson = new Gson();
    private int uid = -1;

    public ServicioOdoo(String url, String db) {
        this.url = url;
        this.db = db;
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.client = HttpClient.newBuilder().cookieHandler(cookieManager).build();
    }

    public boolean autenticar(String usuario, String password) throws IOException, InterruptedException {
        Map<String, Object> params = new HashMap<>();
        params.put("db", db);
        params.put("login", usuario);
        params.put("password", password);

        Map<String, Object> body = new HashMap<>();
        body.put("jsonrpc", "2.0");
        body.put("method", "call");
        body.put("params", params);
        body.put("id", System.currentTimeMillis());

        String json = gson.toJson(body);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/web/session/authenticate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<?, ?> resp = gson.fromJson(response.body(), Map.class);
        if (resp != null && resp.get("result") instanceof Map) {
            Map<?, ?> result = (Map<?, ?>) resp.get("result");
            Double uidD = (Double) result.get("uid");
            if (uidD != null) {
                this.uid = uidD.intValue();
                SesionOdoo s = SesionOdoo.getInstancia();
                s.setUrl(url);
                s.setBaseDatos(db);
                s.setUsuarioId(this.uid);
                s.setNombreUsuario(usuario);
                // Capturar cookie de sesión para reutilizarla en futuras llamadas
                response.headers().firstValue("Set-Cookie").ifPresent(c -> {
                    String cookieVal = c.split(";", 2)[0];
                    s.setCookieSession(cookieVal);
                });
                return true;
            }
        }
        return false;
    }

    public List<Factura> obtenerFacturasVenta() throws IOException, InterruptedException {
        // Uso de search_read para traer facturas y campos relevantes
        Map<String, Object> params = new HashMap<>();
        params.put("model", "account.move");
        params.put("method", "search_read");
        List<Object> args = new ArrayList<>();
        // domain: [['move_type', '=', 'out_invoice']]
        List<Object> domain = List.of(List.of("move_type", "=", "out_invoice"));
        args.add(domain);
        params.put("args", args);
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("fields", List.of("id", "name", "partner_id", "invoice_date", "amount_total", "invoice_line_ids"));
        params.put("kwargs", kwargs);

        Map<String, Object> body = new HashMap<>();
        body.put("jsonrpc", "2.0");
        body.put("method", "call");
        body.put("params", params);
        body.put("id", System.currentTimeMillis());

        String json = gson.toJson(body);
        // Construir request y adjuntar cookie de sesión si existe
        String cookie = SesionOdoo.getInstancia().getCookieSession();
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url + "/web/dataset/call_kw/account.move/search_read"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (cookie != null) builder.header("Cookie", cookie);
        HttpRequest request = builder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Debug: mostrar status y body para diagnosticar respuestas vacías
        System.out.println("DEBUG: Odoo search_read status=" + response.statusCode() + " cookie=" + cookie + " body=" + response.body());
        Map<?, ?> resp = gson.fromJson(response.body(), Map.class);
        List<Factura> result = new ArrayList<>();
        if (resp != null) {
            Object resObj = resp.get("result");
            // Caso 1: result es un Map con 'records'
            if (resObj instanceof Map) {
                Map<?, ?> r = (Map<?, ?>) resObj;
                Object records = r.get("records");
                if (records instanceof List) {
                    List<?> recs = (List<?>) records;
                    for (Object o : recs) {
                        if (o instanceof Map) {
                            Map<?, ?> m = (Map<?, ?>) o;
                            Factura f = new Factura();
                            Double idD = (Double) m.get("id");
                            if (idD != null) f.setId(idD.intValue());
                            f.setNombre((String) m.get("name"));
                            Object partner = m.get("partner_id");
                            if (partner instanceof List) {
                                List<?> p = (List<?>) partner;
                                if (p.size() >= 2) f.setNombreCliente((String) p.get(1));
                            }
                            String date = (String) m.get("invoice_date");
                            if (date != null) f.setFechaFactura(LocalDate.parse(date));
                            Double total = (Double) m.get("amount_total");
                            if (total != null) f.setMontoTotal(total);

                            // obtener líneas
                            List<LineaFactura> lineas = new ArrayList<>();
                            Object lineIdsObj = m.get("invoice_line_ids");
                            if (lineIdsObj instanceof List) {
                                List<?> lineIds = (List<?>) lineIdsObj;
                                if (!lineIds.isEmpty()) {
                                    List<LineaFactura> loaded = leerLineasFactura(lineIds);
                                    lineas.addAll(loaded);
                                }
                            }
                            f.setLineas(lineas);
                            result.add(f);
                        }
                    }
                }
            }
            // Caso 2: result es directamente una List de registros (forma que vimos en los logs)
            else if (resObj instanceof List) {
                List<?> recs = (List<?>) resObj;
                for (Object o : recs) {
                    if (o instanceof Map) {
                        Map<?, ?> m = (Map<?, ?>) o;
                        Factura f = new Factura();
                        Double idD = (Double) m.get("id");
                        if (idD != null) f.setId(idD.intValue());
                        f.setNombre((String) m.get("name"));
                        Object partner = m.get("partner_id");
                        if (partner instanceof List) {
                            List<?> p = (List<?>) partner;
                            if (p.size() >= 2) f.setNombreCliente((String) p.get(1));
                        }
                        String date = (String) m.get("invoice_date");
                        if (date != null) f.setFechaFactura(LocalDate.parse(date));
                        Double total = (Double) m.get("amount_total");
                        if (total != null) f.setMontoTotal(total);

                        // obtener líneas
                        List<LineaFactura> lineas = new ArrayList<>();
                        Object lineIdsObj = m.get("invoice_line_ids");
                        if (lineIdsObj instanceof List) {
                            List<?> lineIds = (List<?>) lineIdsObj;
                            if (!lineIds.isEmpty()) {
                                List<LineaFactura> loaded = leerLineasFactura(lineIds);
                                lineas.addAll(loaded);
                            }
                        }
                        f.setLineas(lineas);
                        result.add(f);
                    }
                }
            }
        }
        return result;
    }

    private List<LineaFactura> leerLineasFactura(List<?> ids) throws IOException, InterruptedException {
        // Llamada a read para account.move.line
        Map<String, Object> params = new HashMap<>();
        params.put("model", "account.move.line");
        params.put("method", "read");
        List<Object> args = new ArrayList<>();
        args.add(ids);
        params.put("args", args);
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("fields", List.of("id", "product_id", "quantity", "name"));
        params.put("kwargs", kwargs);

        Map<String, Object> body = new HashMap<>();
        body.put("jsonrpc", "2.0");
        body.put("method", "call");
        body.put("params", params);
        body.put("id", System.currentTimeMillis());

        String json = gson.toJson(body);
        // Adjuntar cookie si hay sesión
        String cookie = SesionOdoo.getInstancia().getCookieSession();
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url + "/web/dataset/call_kw/account.move.line/read"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (cookie != null) builder.header("Cookie", cookie);
        HttpRequest request = builder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Debug: mostrar status y body para diagnosticar respuestas de líneas
        System.out.println("DEBUG: Odoo line read status=" + response.statusCode() + " cookie=" + cookie + " body=" + response.body());
        Map<?, ?> resp = gson.fromJson(response.body(), Map.class);
        List<LineaFactura> result = new ArrayList<>();
        if (resp != null) {
            Object resObj = resp.get("result");
            // Caso Map con 'records'
            if (resObj instanceof Map) {
                Map<?, ?> r = (Map<?, ?>) resObj;
                Object records = r.get("records");
                if (records instanceof List) {
                    for (Object o : (List<?>) records) {
                        if (o instanceof Map) {
                            Map<?, ?> m = (Map<?, ?>) o;
                            LineaFactura lf = new LineaFactura();
                            Double idD = (Double) m.get("id");
                            if (idD != null) lf.setId(idD.intValue());
                            Object prod = m.get("product_id");
                            if (prod instanceof List) {
                                List<?> p = (List<?>) prod;
                                if (!p.isEmpty()) {
                                    Double pid = (Double) p.get(0);
                                    lf.setProductoId(pid.intValue());
                                    if (p.size() > 1) lf.setNombreProducto((String) p.get(1));
                                }
                            }
                            Double qty = (Double) m.get("quantity");
                            if (qty != null) lf.setCantidad(qty);
                            lf.setNumeroSerie(null);
                            result.add(lf);
                        }
                    }
                }
            }
            // Caso resultado directo en forma de List
            else if (resObj instanceof List) {
                for (Object o : (List<?>) resObj) {
                    if (o instanceof Map) {
                        Map<?, ?> m = (Map<?, ?>) o;
                        LineaFactura lf = new LineaFactura();
                        Double idD = (Double) m.get("id");
                        if (idD != null) lf.setId(idD.intValue());
                        Object prod = m.get("product_id");
                        if (prod instanceof List) {
                            List<?> p = (List<?>) prod;
                            if (!p.isEmpty()) {
                                Double pid = (Double) p.get(0);
                                lf.setProductoId(pid.intValue());
                                if (p.size() > 1) lf.setNombreProducto((String) p.get(1));
                            }
                        }
                        Double qty = (Double) m.get("quantity");
                        if (qty != null) lf.setCantidad(qty);
                        lf.setNumeroSerie(null);
                        result.add(lf);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Comprueba si existe una base de datos con el nombre indicado.
     */
    public boolean existeBaseDatos(String dbName) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("jsonrpc", "2.0");
            body.put("method", "call");
            body.put("params", new HashMap<>());
            body.put("id", System.currentTimeMillis());

            String json = gson.toJson(body);
            String cookie = SesionOdoo.getInstancia().getCookieSession();
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url + "/web/database/list"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json));
            if (cookie != null) builder.header("Cookie", cookie);
            HttpRequest request = builder.build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Map<?, ?> resp = gson.fromJson(response.body(), Map.class);
            if (resp != null && resp.get("result") instanceof List) {
                List<?> list = (List<?>) resp.get("result");
                return list.contains(dbName);
            }
        } catch (Exception e) {
            // Ignorar y devolver false si no es posible listar
        }
        return false;
    }

    /**
     * Crea una base de datos en Odoo usando la contraseña maestra.
     */
    public boolean crearBaseDatos(String masterPwd, String nombreBd, boolean demo, String lang, String adminPassword, String adminEmail) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("master_pwd", masterPwd);
            params.put("name", nombreBd);
            params.put("demo", demo);
            params.put("lang", lang == null ? "es_ES" : lang);
            params.put("admin_password", adminPassword);
            params.put("admin_email", adminEmail);

            Map<String, Object> body = new HashMap<>();
            body.put("jsonrpc", "2.0");
            body.put("method", "call");
            body.put("params", params);
            body.put("id", System.currentTimeMillis());

            String json = gson.toJson(body);
            String cookie = SesionOdoo.getInstancia().getCookieSession();
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url + "/web/database/create"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json));
            if (cookie != null) builder.header("Cookie", cookie);
            HttpRequest request = builder.build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Map<?, ?> resp = gson.fromJson(response.body(), Map.class);
            if (resp != null && resp.get("result") != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
