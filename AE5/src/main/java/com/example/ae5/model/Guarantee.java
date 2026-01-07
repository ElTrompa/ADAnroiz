package com.example.ae5.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guarantee {
    private String id = UUID.randomUUID().toString();

    private Long invoiceId;           // Reference to Odoo invoice
    private String client;             // Client name
    private String product;            // Product (e.g., Bicicleta, Maillot, Culote, Rodillo)
    private LocalDate purchaseDate;    // Date of purchase
    private String address;            // Guarantee location address
    private String country;            // Guarantee country
    private LocalDate warrantyStart;   // Warranty start date
    private LocalDate warrantyEnd;     // Warranty end date
    private String status;             // ACTIVA, EXPIRADA, CANCELADA
    private String description;        // Additional notes
    private LocalDate createdAt = LocalDate.now();
    private LocalDate updatedAt = LocalDate.now();
}
