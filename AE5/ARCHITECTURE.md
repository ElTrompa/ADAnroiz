# AE5 Warranty System - Architecture Documentation

## System Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                      JAVA DESKTOP APPLICATION                        │
│                        (JavaFX - Port 8080)                          │
├──────────────────────────────────────────────────────────────────────┤
│  UI Layer (JavaFX)                                                   │
│  ├─ LoginController         → Authentication                         │
│  ├─ MainViewController       → Tabbed interface                      │
│  ├─ InvoiceTab             → Read Odoo invoices (XML-RPC)           │
│  ├─ GuaranteeTab           → CRUD warranties (MongoDB)              │
│  └─ GuaranteeFormController → Modal forms for editing               │
├──────────────────────────────────────────────────────────────────────┤
│  Service Layer (Spring Boot)                                         │
│  ├─ OdooService            → XML-RPC integration                    │
│  └─ GuaranteeService       → Business logic for warranties          │
├──────────────────────────────────────────────────────────────────────┤
│  Data Layer                                                          │
│  ├─ GuaranteeRepository    → MongoDB DAO (Spring Data)              │
│  └─ REST Controller        → Optional HTTP API                      │
└──────────────────────────────────────────────────────────────────────┘
         │                              │
         │ XML-RPC                      │ MongoDB Driver
         │ (Port 8069)                  │ (Port 27017)
         ▼                              ▼
    ┌─────────────┐              ┌──────────────┐
    │   ODOO      │              │   MONGODB    │
    │  (Invoices) │              │(Guarantees)  │
    │PostgreSQL:  │              │              │
    │   :5432     │              │   Port       │
    └─────────────┘              │   27017      │
                                 └──────────────┘
```

---

## Technology Stack

```
┌────────────────────────────────────────────────────────┐
│ Backend Framework                                       │
├────────────────────────────────────────────────────────┤
│ Spring Boot 3.2.0                                       │
│  ├─ spring-boot-starter-web        (REST endpoints)    │
│  ├─ spring-boot-starter-data-mongodb (ORM)            │
│  └─ spring-context                  (Dependency inject)│
├────────────────────────────────────────────────────────┤
│ Frontend Framework                                      │
├────────────────────────────────────────────────────────┤
│ JavaFX 21.0.2                                           │
│  ├─ javafx-controls   (Buttons, TextFields, Tables)    │
│  ├─ javafx-fxml       (FXML support)                   │
│  └─ javafx-graphics   (Graphics & styling)             │
├────────────────────────────────────────────────────────┤
│ ERP Integration                                         │
├────────────────────────────────────────────────────────┤
│ Odoo 17 (via Docker)                                    │
│  └─ Apache XML-RPC Client 3.1.3    (API client)        │
├────────────────────────────────────────────────────────┤
│ Database                                                │
├────────────────────────────────────────────────────────┤
│ MongoDB 7.0 (Warranty data)                             │
│ PostgreSQL 15 (Odoo data)                               │
├────────────────────────────────────────────────────────┤
│ Build & Deploy                                          │
├────────────────────────────────────────────────────────┤
│ Maven 3.8+                                              │
│ Docker & docker-compose 3.8                             │
│ Java 21 JDK                                             │
└────────────────────────────────────────────────────────┘
```

---

## Data Flow Diagrams

### 1. User Authentication Flow

```
User → Login UI → LoginController → Hardcoded Check (admin/admin)
                                            │
                                    ✓ Credentials OK
                                            │
                                   MainViewController
                                            │
                    ┌───────────────────────┴────────────────────┐
                    │                                            │
              InvoiceTab                                  GuaranteeTab
         (Odoo Data Flow)                           (MongoDB Data Flow)
```

### 2. Invoice Fetch (Odoo XML-RPC)

```
JavaFX App
    │
    └─→ OdooService.getInvoices()
            │
            ├─→ authenticate("odoo", "admin", "admin")
            │       └─→ XML-RPC: /xmlrpc/2/common
            │           Returns: uid (user ID)
            │
            └─→ execute("account.move", "search", [])
                    └─→ XML-RPC: /xmlrpc/2/object
                        Returns: [invoiceIds]
                                  │
                         ┌────────┴────────┐
                         │                 │
                    ┌─────▼────┐     ┌─────▼────┐
                    │ Invoice 1 │     │ Invoice 2 │
                    └───────────┘     └───────────┘
                         │                 │
                         └────────┬────────┘
                                  │
                         TableView (JavaFX)
```

### 3. Guarantee CRUD (MongoDB)

```
JavaFX App → GuaranteeService → GuaranteeRepository → MongoDB
    │                                                      │
    ├─ CREATE                                             │
    │   └─→ createGuarantee(g)                           │
    │       └─→ repository.save(g)                       │
    │           └─→ db.guarantees.insertOne()  ─────────→│
    │                                                     │
    ├─ READ                                              │
    │   └─→ getGuaranteeById(id)                         │
    │       └─→ repository.findById(id) ────────────────→│
    │                                                     │
    ├─ UPDATE                                            │
    │   └─→ updateGuarantee(id, g)                       │
    │       └─→ repository.save(g)                       │
    │           └─→ db.guarantees.updateOne() ──────────→│
    │                                                     │
    └─ DELETE                                            │
        └─→ deleteGuarantee(id)                          │
            └─→ repository.deleteById(id) ──────────────→│
                                                          │
            Result: JSON Document                        │
            {                                            │
              "_id": "uuid",                             │
              "invoiceId": 1001,                         │
              "client": "John Smith",                    │
              "status": "ACTIVE",                        │
              ...                                        │
            }                                            │
```

---

## Package Structure

```
com.example.ae5/
├── Launcher.java                    (Spring Boot Bootstrap)
├── HelloApplication.java            (JavaFX Entry Point)
│
├── model/
│   ├── Guarantee.java              (MongoDB Document)
│   └── Invoice.java                 (Odoo DTO)
│
├── repository/
│   └── GuaranteeRepository.java     (Spring Data MongoDB)
│
├── service/
│   ├── OdooService.java            (Odoo Integration via XML-RPC)
│   └── GuaranteeService.java       (Business Logic)
│
├── controller/
│   └── GuaranteeController.java    (Optional REST API)
│
├── ui/
│   ├── LoginController.java         (Login Screen)
│   ├── MainViewController.java      (Main Window)
│   └── GuaranteeFormController.java (Form Modal)
│
└── config/
    └── AppConfig.java              (Spring Configuration)
```

---

## Database Schemas

### MongoDB - Guarantees Collection

```javascript
{
  _id: ObjectId("...") || UUID String,
  invoiceId: NumberLong(1001),           // Foreign key to Odoo
  client: String,                        // Client name
  purchaseDate: Date,                    // Date of purchase
  address: String,                       // Address
  country: String,                       // Country
  warrantyStart: Date,                   // Warranty period start
  warrantyEnd: Date,                     // Warranty period end
  status: String,                        // ACTIVE | EXPIRED | CANCELLED
  description: String,                   // Optional notes
  createdAt: Date,                       // Creation timestamp
  updatedAt: Date                        // Last update timestamp
}
```

**Indexes:**
```
db.guarantees.createIndex({ client: 1 });
db.guarantees.createIndex({ status: 1 });
db.guarantees.createIndex({ country: 1 });
db.guarantees.createIndex({ invoiceId: 1 });
```

### PostgreSQL - Odoo account.move (Invoice) Model

```sql
SELECT
    id,
    name (invoice number),
    partner_id (customer reference),
    invoice_date,
    amount_total,
    state (draft/posted/paid/cancelled)
FROM account_move
WHERE move_type = 'out_invoice';
```

---

## XML-RPC API Endpoints

### Odoo Connection

**Common Endpoint:** `http://localhost:8069/xmlrpc/2/common`

```python
# Authenticate
authenticate(db: str, login: str, password: str) -> int (uid)

# Example
uid = authenticate("odoo", "admin", "admin")  # Returns: 2
```

**Object Endpoint:** `http://localhost:8069/xmlrpc/2/object`

```python
# Execute method
execute(db: str, uid: int, password: str, model: str, method: str, args: list) -> Any

# Search
execute("odoo", 2, "admin", "account.move", "search", [])
# Returns: [1, 2, 3, ...]

# Read
execute("odoo", 2, "admin", "account.move", "read", 
        [1, 2, 3],
        ["id", "name", "partner_id", "invoice_date", "amount_total"])
# Returns: [
#   {"id": 1, "name": "INV/2024/001", ...},
#   ...
# ]

# Create
execute("odoo", 2, "admin", "account.move", "create",
        [{...invoice data...}])
# Returns: new_id
```

---

## REST API Endpoints (Optional)

The app includes a REST API on port 8080 for external access:

```
GET    /api/guarantees                  - List all
GET    /api/guarantees/{id}             - Get by ID
POST   /api/guarantees                  - Create
PUT    /api/guarantees/{id}             - Update
DELETE /api/guarantees/{id}             - Delete

GET    /api/guarantees/search            - Search with filters
GET    /api/guarantees/client/{client}   - Filter by client
GET    /api/guarantees/status/{status}   - Filter by status
GET    /api/guarantees/country/{country} - Filter by country
GET    /api/guarantees/invoice/{id}      - Filter by invoice
```

---

## Deployment Architecture

```
┌────────────────────────────────────────────────────┐
│                Docker Host (Windows/Linux/Mac)    │
├────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────┐ │
│  │         Docker Network (ae5-network)         │ │
│  │                                              │ │
│  │  ┌──────────────┐  ┌──────────────┐         │ │
│  │  │ Odoo         │  │ PostgreSQL    │         │ │
│  │  │ Container    │──│ Container     │         │ │
│  │  │ Port: 8069   │  │ Port: 5432    │         │ │
│  │  └──────────────┘  └──────────────┘         │ │
│  │                                              │ │
│  │  ┌──────────────────────────────────────┐   │ │
│  │  │ MongoDB Container                    │   │ │
│  │  │ Port: 27017                          │   │ │
│  │  └──────────────────────────────────────┘   │ │
│  │                                              │ │
│  └──────────────────────────────────────────────┘ │
│                                                    │
│  ┌──────────────────────────────────────────────┐ │
│  │ Java Application (Standalone)                │ │
│  │ Port: 8080                                   │ │
│  │ ├─ Spring Boot REST API                      │ │
│  │ └─ JavaFX GUI                                │ │
│  └──────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────┘
         │
         │ Network Access from Host
         │
┌────────▼─────────┐
│ Host Machine     │
│ - Keyboard/Mouse │
│ - Display        │
└──────────────────┘
```

### Volume Mapping

```
Docker Volume          Host Location           Container Path
─────────────────────────────────────────────────────────────
postgres_data      → /var/lib/docker/...   → /var/lib/postgresql/data
odoo_data          → /var/lib/docker/...   → /var/lib/odoo
odoo_addons        → /var/lib/docker/...   → /mnt/extra-addons
mongodb_data       → /var/lib/docker/...   → /data/db
```

---

## Security Considerations

### Current Implementation (Development)
- Hardcoded credentials (admin/admin)
- No SSL/TLS
- Local network only

### Production Recommendations

1. **Authentication:**
   - Replace hardcoded credentials with JWT/OAuth
   - Use LDAP/AD integration
   - Implement role-based access control (RBAC)

2. **Network:**
   - Enable SSL/TLS for all endpoints
   - Use reverse proxy (nginx/Apache)
   - Restrict API access

3. **Data Protection:**
   - Encrypt sensitive fields in MongoDB
   - Use connection pooling
   - Implement rate limiting

4. **Infrastructure:**
   - Use Kubernetes for orchestration
   - Set up automated backups
   - Implement health checks & monitoring

---

## Performance Considerations

### Current Metrics
- **Odoo Invoice Load:** ~1-2 seconds for <1000 invoices
- **MongoDB Query:** <50ms for indexed searches
- **UI Rendering:** <100ms for tables with 100+ rows

### Optimization Strategies
1. Implement pagination for large datasets
2. Cache invoice data (refresh interval: 5 minutes)
3. Use MongoDB aggregation pipeline for complex queries
4. Implement lazy loading in JavaFX tables
5. Consider read replicas for MongoDB

---

## Future Enhancements

```
Phase 2 (Planned):
├─ User Management System
├─ Email Notifications
├─ Warranty Renewal Logic
├─ PDF Export/Report Generation
├─ Advanced Analytics Dashboard
├─ Mobile App (React Native)
└─ API Gateway (Kong/AWS API Gateway)

Phase 3 (Proposed):
├─ Machine Learning (warranty prediction)
├─ Blockchain Integration (audit trail)
├─ Multi-tenant Support
└─ AI Chatbot Support
```

---

**Last Updated:** January 2026  
**Version:** 1.0.0
