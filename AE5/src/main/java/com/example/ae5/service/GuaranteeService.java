package com.example.ae5.service;

import com.example.ae5.model.Guarantee;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.*;

public class GuaranteeService {

    private MongoCollection<Document> collection;

    public GuaranteeService() {
        try {
            MongoClient client = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = client.getDatabase("warranty_db");
            this.collection = database.getCollection("guarantees");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
        }
    }

    public Guarantee createGuarantee(Guarantee guarantee) {
        try {
            guarantee.setId(new ObjectId().toString());
            guarantee.setCreatedAt(LocalDate.now());
            guarantee.setUpdatedAt(LocalDate.now());
            
            Document doc = guaranteeToDocument(guarantee);
            collection.insertOne(doc);
            System.out.println("Created guarantee: " + guarantee.getId());
            return guarantee;
        } catch (Exception e) {
            System.err.println("Error creating guarantee: " + e.getMessage());
            return null;
        }
    }

    public Guarantee updateGuarantee(String id, Guarantee guarantee) {
        try {
            guarantee.setId(id);
            guarantee.setUpdatedAt(LocalDate.now());
            
            Document doc = guaranteeToDocument(guarantee);
            collection.replaceOne(new Document("_id", new ObjectId(id)), doc);
            System.out.println("Updated guarantee: " + id);
            return guarantee;
        } catch (Exception e) {
            System.err.println("Error updating guarantee: " + e.getMessage());
            return null;
        }
    }

    public void deleteGuarantee(String id) {
        try {
            collection.deleteOne(new Document("_id", new ObjectId(id)));
            System.out.println("Deleted guarantee: " + id);
        } catch (Exception e) {
            System.err.println("Error deleting guarantee: " + e.getMessage());
        }
    }

    public Guarantee getGuaranteeById(String id) {
        try {
            Document doc = collection.find(new Document("_id", new ObjectId(id))).first();
            return doc != null ? documentToGuarantee(doc) : null;
        } catch (Exception e) {
            System.err.println("Error getting guarantee: " + e.getMessage());
            return null;
        }
    }

    public List<Guarantee> getAllGuarantees() {
        List<Guarantee> guarantees = new ArrayList<>();
        try {
            for (Document doc : collection.find()) {
                guarantees.add(documentToGuarantee(doc));
            }
        } catch (Exception e) {
            System.err.println("Error getting all guarantees: " + e.getMessage());
        }
        return guarantees;
    }

    public List<Guarantee> getGuaranteesByClient(String client) {
        List<Guarantee> guarantees = new ArrayList<>();
        try {
            for (Document doc : collection.find(new Document("client", client))) {
                guarantees.add(documentToGuarantee(doc));
            }
        } catch (Exception e) {
            System.err.println("Error getting guarantees by client: " + e.getMessage());
        }
        return guarantees;
    }

    public List<Guarantee> getGuaranteesByStatus(String status) {
        List<Guarantee> guarantees = new ArrayList<>();
        try {
            for (Document doc : collection.find(new Document("status", status))) {
                guarantees.add(documentToGuarantee(doc));
            }
        } catch (Exception e) {
            System.err.println("Error getting guarantees by status: " + e.getMessage());
        }
        return guarantees;
    }

    public List<Guarantee> getGuaranteesByCountry(String country) {
        List<Guarantee> guarantees = new ArrayList<>();
        try {
            for (Document doc : collection.find(new Document("country", country))) {
                guarantees.add(documentToGuarantee(doc));
            }
        } catch (Exception e) {
            System.err.println("Error getting guarantees by country: " + e.getMessage());
        }
        return guarantees;
    }

    public List<Guarantee> getGuaranteesByInvoice(Long invoiceId) {
        List<Guarantee> guarantees = new ArrayList<>();
        try {
            for (Document doc : collection.find(new Document("invoiceId", invoiceId))) {
                guarantees.add(documentToGuarantee(doc));
            }
        } catch (Exception e) {
            System.err.println("Error getting guarantees by invoice: " + e.getMessage());
        }
        return guarantees;
    }

    public List<Guarantee> searchGuarantees(String client, String status, String country) {
        List<Guarantee> guarantees = getAllGuarantees();
        
        if (client != null && !client.isEmpty()) {
            guarantees = guarantees.stream()
                    .filter(g -> g.getClient().toLowerCase().contains(client.toLowerCase()))
                    .toList();
        }
        
        if (status != null && !status.isEmpty()) {
            guarantees = guarantees.stream()
                    .filter(g -> g.getStatus().equalsIgnoreCase(status))
                    .toList();
        }
        
        if (country != null && !country.isEmpty()) {
            guarantees = guarantees.stream()
                    .filter(g -> g.getCountry().toLowerCase().contains(country.toLowerCase()))
                    .toList();
        }
        
        return guarantees;
    }

    public void updateGuaranteeStatus(String id, String status) {
        try {
            Document update = new Document("$set", new Document("status", status).append("updatedAt", LocalDate.now()));
            collection.updateOne(new Document("_id", new ObjectId(id)), update);
            System.out.println("Updated guarantee status: " + id + " -> " + status);
        } catch (Exception e) {
            System.err.println("Error updating guarantee status: " + e.getMessage());
        }
    }

    private Document guaranteeToDocument(Guarantee g) {
        return new Document()
                .append("_id", new ObjectId(g.getId()))
                .append("invoiceId", g.getInvoiceId())
                .append("client", g.getClient())
                .append("product", g.getProduct())
                .append("purchaseDate", g.getPurchaseDate())
                .append("address", g.getAddress())
                .append("country", g.getCountry())
                .append("warrantyStart", g.getWarrantyStart())
                .append("warrantyEnd", g.getWarrantyEnd())
                .append("status", g.getStatus())
                .append("description", g.getDescription())
                .append("createdAt", g.getCreatedAt())
                .append("updatedAt", g.getUpdatedAt());
    }

    private Guarantee documentToGuarantee(Document doc) {
        Guarantee g = new Guarantee();
        g.setId(doc.getObjectId("_id").toString());
        g.setInvoiceId(doc.getLong("invoiceId"));
        g.setClient(doc.getString("client"));
        g.setPurchaseDate((LocalDate) doc.get("purchaseDate"));
        g.setAddress(doc.getString("address"));
        g.setCountry(doc.getString("country"));
        g.setWarrantyStart((LocalDate) doc.get("warrantyStart"));
        g.setWarrantyEnd((LocalDate) doc.get("warrantyEnd"));
        g.setStatus(doc.getString("status"));
        g.setDescription(doc.getString("description"));
        g.setProduct(doc.getString("product"));
        g.setCreatedAt((LocalDate) doc.get("createdAt"));
        g.setUpdatedAt((LocalDate) doc.get("updatedAt"));
        return g;
    }
}
