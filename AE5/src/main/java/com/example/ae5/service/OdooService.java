package com.example.ae5.service;

import com.example.ae5.model.Invoice;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OdooService {

    private String odooUrl;
    private String odooDb;
    private String odooUsername;
    private String odooPassword;
    private int odooUid;

    public OdooService() {
        // Load defaults from application.yml (if present)
        Map<String, Object> appConfig = loadApplicationYaml();
        Map<String, Object> odooConfig = appConfig != null && appConfig.get("odoo") instanceof Map ? (Map<String, Object>) appConfig.get("odoo") : null;

        // Read from application.yml first
        String ymlUrl = safeGetString(odooConfig, "url");
        String ymlDb = safeGetString(odooConfig, "db");
        String ymlUser = safeGetString(odooConfig, "username");
        String ymlPass = safeGetString(odooConfig, "password");

        // Environment variables override application.yml
        String envUrl = System.getenv("ODOO_URL");
        String envDb = System.getenv("ODOO_DB");
        String envUser = System.getenv("ODOO_USERNAME");
        String envPass = System.getenv("ODOO_PASSWORD");

        this.odooUrl = envUrl != null && !envUrl.isBlank() ? envUrl : (ymlUrl != null && !ymlUrl.isBlank() ? ymlUrl : "http://localhost:8069");
        this.odooDb = envDb != null && !envDb.isBlank() ? envDb : (ymlDb != null && !ymlDb.isBlank() ? ymlDb : "odoo");
        this.odooUsername = envUser != null && !envUser.isBlank() ? envUser : (ymlUser != null && !ymlUser.isBlank() ? ymlUser : "admin");
        this.odooPassword = envPass != null ? envPass : (ymlPass != null ? ymlPass : "");
        this.odooUid = -1;

        System.out.println("Odoo configuration: url=" + this.odooUrl + " db=" + this.odooDb + " user=" + this.odooUsername);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Map<String, Object> loadApplicationYaml() {
        try (java.io.InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.yml")) {
            if (in == null) {
                return null;
            }
            org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
            Object data = yaml.load(in);
            if (data instanceof Map) {
                return (Map<String, Object>) data;
            }
        } catch (Exception e) {
            System.err.println("Failed to read application.yml: " + e.getMessage());
        }
        return null;
    }

    private String safeGetString(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    /**
     * Authenticate with Odoo and get user ID
     */
    public void authenticate() {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(odooUrl + "/xmlrpc/2/common"));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            // Odoo 17+ requires user_agent_env parameter
            Map<String, Object> userAgentEnv = new java.util.HashMap<>();
            userAgentEnv.put("type", "jsonrpc");
            userAgentEnv.put("server_version", "17.0");
            
            Object[] params = {odooDb, odooUsername, odooPassword, userAgentEnv};
            Object result = client.execute("authenticate", params);

            // Expect a positive integer UID on success
            if (result instanceof Integer && ((Integer) result) > 0) {
                odooUid = (Integer) result;
                System.out.println("Successfully authenticated with Odoo. UID: " + odooUid);
            } else {
                throw new RuntimeException("Invalid credentials or unexpected authentication response: " + result);
            }
        } catch (MalformedURLException | XmlRpcException | RuntimeException e) {
            System.err.println("Failed to authenticate with Odoo: " + e.getMessage());
            System.err.println("Odoo URL: " + odooUrl);
            System.err.println("Odoo DB: " + odooDb);
            System.err.println("Odoo Username: " + odooUsername);
            // Propagate error so caller can react (UI will switch to offline mode)
            throw new RuntimeException("Failed to authenticate with Odoo: " + e.getMessage(), e);
        }
    }

    /**
     * Fetch invoices from Odoo
     */
    public List<Invoice> getInvoices() {
        if (odooUid == -1) {
            authenticate();
        }

        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(odooUrl + "/xmlrpc/2/object"));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            // Search for invoices - using empty search criteria to get all
            Map<String, Object> searchParams = new java.util.HashMap<>();
            // Can add filters here if needed: searchParams.put("domain", new Object[]{});
            
            Object[] params = {odooDb, odooUid, odooPassword, "account.move", "search", new Object[]{}};
            Object result = client.execute("execute", params);
            
            Object[] invoiceIds = new Object[]{};
            if (result instanceof Object[]) {
                invoiceIds = (Object[]) result;
            }

            List<Invoice> invoices = new ArrayList<>();

            if (invoiceIds.length > 0) {
                System.out.println("Found " + invoiceIds.length + " invoices in Odoo");
                
                // Read invoice details
                Object[] invoiceIdsList = invoiceIds;
                Object[] readParams = {odooDb, odooUid, odooPassword, "account.move", "read",
                        invoiceIdsList,
                        new String[]{"id", "name", "partner_id", "invoice_date", "amount_total", "state"}};
                        
                Object readResult = client.execute("execute", readParams);
                Object[] invoiceRecords = new Object[]{};
                
                if (readResult instanceof Object[]) {
                    invoiceRecords = (Object[]) readResult;
                }

                for (Object record : invoiceRecords) {
                    if (record instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) record;
                        Invoice invoice = new Invoice();
                        invoice.setId(((Number) map.get("id")).longValue());
                        invoice.setNumber((String) map.get("name"));

                        // Extract partner name
                        Object partnerObj = map.get("partner_id");
                        if (partnerObj instanceof Object[]) {
                            Object[] partner = (Object[]) partnerObj;
                            if (partner.length > 1) {
                                invoice.setClient((String) partner[1]);
                            }
                        }

                        // Parse date
                        if (map.get("invoice_date") != null) {
                            if (map.get("invoice_date") instanceof Date) {
                                LocalDate date = ((Date) map.get("invoice_date"))
                                        .toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                invoice.setDate(date);
                            } else if (map.get("invoice_date") instanceof String) {
                                invoice.setDate(LocalDate.parse((String) map.get("invoice_date")));
                            }
                        }

                        invoice.setAmount(((Number) map.get("amount_total")).doubleValue());
                        invoice.setStatus((String) map.get("state"));

                        invoices.add(invoice);
                    }
                }
            } else {
                System.out.println("No invoices found in Odoo");
            }

            System.out.println("Retrieved " + invoices.size() + " invoices from Odoo");
            return invoices;

        } catch (Exception e) {
            System.err.println("Failed to fetch invoices from Odoo: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch invoices", e);
        }
    }

    /**
     * Get a specific invoice by ID
     */
    public Invoice getInvoiceById(Long invoiceId) {
        List<Invoice> invoices = getInvoices();
        return invoices.stream()
                .filter(inv -> inv.getId().equals(invoiceId))
                .findFirst()
                .orElse(null);
    }
}
