// Initialize MongoDB with sample data
db = db.getSiblingDB('ae5_warranty_db');

// Create guarantees collection with sample data
db.guarantees.insertMany([
  {
    _id: java.util.UUID.randomUUID().toString(),
    invoiceId: 1,
    client: "John Smith",
    product: "Bicicleta",
    purchaseDate: new Date("2024-01-15"),
    address: "123 Main St, New York, NY 10001",
    country: "United States",
    warrantyStart: new Date("2024-01-15"),
    warrantyEnd: new Date("2026-01-15"),
    status: "ACTIVE",
    description: "Bicicleta Carbono - garantía para la factura #1001",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: java.util.UUID.randomUUID().toString(),
    invoiceId: 2,
    client: "Jane Doe",
    product: "Maillot",
    purchaseDate: new Date("2023-06-20"),
    address: "456 Oak Ave, Los Angeles, CA 90001",
    country: "United States",
    warrantyStart: new Date("2023-06-20"),
    warrantyEnd: new Date("2025-06-20"),
    status: "EXPIRED",
    description: "Maillot Pro - garantía para la factura #1002",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: java.util.UUID.randomUUID().toString(),
    invoiceId: 3,
    client: "Carlos García",
    product: "Rodillo",
    purchaseDate: new Date("2024-09-10"),
    address: "789 Calle Mayor, Madrid, 28001",
    country: "Spain",
    warrantyStart: new Date("2024-09-10"),
    warrantyEnd: new Date("2026-09-10"),
    status: "ACTIVE",
    description: "Rodillo Elite - garantía para la factura #1003",
    createdAt: new Date(),
    updatedAt: new Date()
  }
  {
    _id: java.util.UUID.randomUUID().toString(),
    invoiceId: 2,
    client: "Jane Doe",
    purchaseDate: new Date("2023-06-20"),
    address: "456 Oak Ave, Los Angeles, CA 90001",
    country: "United States",
    warrantyStart: new Date("2023-06-20"),
    warrantyEnd: new Date("2025-06-20"),
    status: "EXPIRED",
    description: "Product warranty for Invoice #1002",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: java.util.UUID.randomUUID().toString(),
    invoiceId: 3,
    client: "Carlos García",
    purchaseDate: new Date("2024-09-10"),
    address: "789 Calle Mayor, Madrid, 28001",
    country: "Spain",
    warrantyStart: new Date("2024-09-10"),
    warrantyEnd: new Date("2026-09-10"),
    status: "ACTIVE",
    description: "Product warranty for Invoice #1003",
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

db.guarantees.createIndex({ client: 1 });
db.guarantees.createIndex({ status: 1 });
db.guarantees.createIndex({ country: 1 });
db.guarantees.createIndex({ invoiceId: 1 });

print("MongoDB initialized with sample guarantee data");
