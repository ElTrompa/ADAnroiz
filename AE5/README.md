# AE5 - Warranty Management System

A comprehensive warranty management system integrating **JavaFX** (desktop UI), **MongoDB** (warranty database), and **Odoo** (invoice management) via **Docker**.

## 📋 Overview

This is a university practice project for **Grado en Ingeniería Informática** (Bachelor's in Computer Engineering). The system allows:

- **Authentication** (username/password login)
- **Invoice Management** from Odoo (read-only, real-time queries)
- **Warranty CRUD** operations stored in MongoDB
- **Search & Filtering** by client, status, country, or invoice
- **Docker Deployment** with full persistence

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    JavaFX Desktop App                        │
│             (Login, Invoices, Warranty Management)           │
└──────────┬──────────────────────┬──────────────────────────┘
           │                      │
      XML-RPC (Odoo)        MongoDB Driver
           │                      │
┌──────────▼──────────┐   ┌──────▼──────────────┐
│   Odoo (Docker)    │   │  MongoDB (Docker)   │
│  - Invoices        │   │  - Warranties       │
│  - Customers       │   │  - Persistence      │
│  - Port 8069       │   │  - Port 27017       │
└────────────────────┘   └─────────────────────┘
```

## 🧪 Technology Stack

- **Java 21** + **Spring Boot 3.2** + **JavaFX 21**
- **MongoDB 7.0** (document database)
- **Odoo 17** (ERP system)
- **PostgreSQL 15** (Odoo's database)
- **Docker & docker-compose** (containerization)
- **Apache XML-RPC** (Odoo API client)
- **Spring Data MongoDB** (ORM)

## 📦 Prerequisites

- **Docker** & **docker-compose**
- **Java 21 JDK**
- **Maven 3.8+**
- **Git**

## 🚀 Quick Start

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/AE5.git
cd AE5
```

### 2. Start Docker Services
```bash
docker-compose up -d
```

This brings up:
- **Odoo** @ `http://localhost:8069` (user: admin / password: admin)
- **MongoDB** @ `mongodb://localhost:27017` (user: admin / password: admin_password)
- **PostgreSQL** for Odoo (internal)

### 3. Wait for Odoo Initialization
```bash
# Check Odoo is ready (may take 1-2 minutes)
docker logs ae5-odoo | grep "Odoo"
```

### 4. Create Demo Invoices in Odoo (Optional)
1. Access Odoo at `http://localhost:8069`
2. Login with **admin** / **admin**
3. Create a few invoices manually or via API for testing

### 5. Build & Run JavaFX App
```bash
# Build
mvn clean package

# Run (using JavaFX plugin)
mvn javafx:run

# OR run standalone JAR
java -jar target/ae5-warranty-system-1.0.0.jar
```

### 6. Login
- **Username:** `admin`
- **Password:** `admin`

## 📚 Features

### ✅ Authentication (Task 1)
- Simple login screen with username/password
- Session control (hardcoded for MVP: admin/admin)
- Access control to main views

### ✅ Invoice Management (Task 2)
- Real-time connection to Odoo via XML-RPC
- Display: Invoice #, Client, Date, Amount
- Refresh button to sync from Odoo
- **Note:** Invoices are queried live, not stored in MongoDB

### ✅ Warranty Management (Task 3)
- **Fields:**
  - ID (auto-generated UUID)
  - Invoice ID (links to Odoo)
  - Client name
  - Purchase date
  - Address & Country
  - Warranty start/end dates
  - Status (ACTIVE, EXPIRED, CANCELLED)
  - Description
  - Timestamps (createdAt, updatedAt)

- **CRUD Operations:**
  - ➕ **Create** new warranty
  - 📖 **Read** / list all warranties
  - ✏️ **Edit** existing warranty
  - ❌ **Delete** warranty

### ✅ Search & Filtering (Task 4)
- Filter by **Client** (substring match)
- Filter by **Status** (ACTIVE, EXPIRED, CANCELLED)
- Filter by **Country**
- Combine filters for advanced search
- Results displayed in real-time table

### ✅ GUI (JavaFX)
- **Login Screen:** Clean form with error handling
- **Main Window:** Tabbed interface
  - **Invoices Tab:** Read-only table from Odoo
  - **Guarantees Tab:** Full CRUD with form modal
- **Menu Bar:** File (Exit), Help (About)
- **Responsive:** Resizable windows, scrollable tables

## 🐳 Docker Deployment

### Services Overview

| Service | Port | Credentials | Volume |
|---------|------|-------------|--------|
| Odoo | 8069 | admin/admin | odoo_data |
| MongoDB | 27017 | admin/admin_password | mongodb_data |
| PostgreSQL | 5432 (internal) | odoo/odoo_password | postgres_data |

### Environment Variables
```bash
# Odoo (auto-configured)
POSTGRES_USER=odoo
POSTGRES_PASSWORD=odoo_password

# MongoDB (auto-configured)
MONGO_INITDB_ROOT_USERNAME=admin
MONGO_INITDB_ROOT_PASSWORD=admin_password
```

### Persistence
All data is stored in Docker volumes:
- `postgres_data` – Odoo database
- `odoo_data` – Odoo addons & filestore
- `mongodb_data` – Warranty documents
- `odoo_addons` – Custom Odoo modules

### Stop Services
```bash
docker-compose down
```

### Reset Everything
```bash
docker-compose down -v  # Remove all volumes
docker-compose up -d    # Restart fresh
```

## 🔌 API Details

### Odoo XML-RPC Endpoints
- **Common:** `http://localhost:8069/xmlrpc/2/common`
  - `authenticate(db, login, password)` → uid
- **Object:** `http://localhost:8069/xmlrpc/2/object`
  - `execute(db, uid, password, model, method, args)`

**Example:** Fetch invoices
```java
OdooService odooService = // ... get from Spring
List<Invoice> invoices = odooService.getInvoices();
```

### MongoDB Collections
- **guarantees:** Stores all warranty documents
- **Indexes:** client, status, country, invoiceId (for fast queries)

**Example:** Create warranty
```java
GuaranteeService guaranteeService = // ... get from Spring
Guarantee g = new Guarantee();
g.setClient("John Doe");
// ... set other fields
guaranteeService.createGuarantee(g);
```

## 📂 Project Structure

```
AE5/
├── pom.xml                              # Maven dependencies
├── docker-compose.yml                   # Docker services
├── scripts/
│   └── init-mongodb.js                  # MongoDB initialization
├── src/main/
│   ├── java/
│   │   ├── module-info.java             # Module definition
│   │   └── com/example/ae5/
│   │       ├── Launcher.java            # Spring Boot bootstrap
│   │       ├── HelloApplication.java    # JavaFX entry point
│   │       ├── model/
│   │       │   ├── Guarantee.java       # Warranty document
│   │       │   └── Invoice.java         # Invoice DTO
│   │       ├── repository/
│   │       │   └── GuaranteeRepository.java  # MongoDB DAO
│   │       ├── service/
│   │       │   ├── OdooService.java     # Odoo integration
│   │       │   └── GuaranteeService.java    # Business logic
│   │       ├── ui/
│   │       │   ├── LoginController.java
│   │       │   ├── MainViewController.java
│   │       │   └── GuaranteeFormController.java
│   │       └── config/
│   │           └── AppConfig.java       # Spring configuration
│   └── resources/
│       ├── application.yml              # Spring config
│       └── logback.xml                  # Logging config
├── .gitignore
└── README.md (this file)
```

## 🧪 Testing

### Manual Testing Checklist
- [ ] Docker services start without errors
- [ ] Odoo accessible at localhost:8069
- [ ] MongoDB accessible at localhost:27017
- [ ] Login with admin/admin works
- [ ] Invoice list loads from Odoo
- [ ] Can create, edit, delete warranties
- [ ] Filtering by client, status, country works
- [ ] Search combines multiple filters correctly
- [ ] Warranty dates are persistent in MongoDB

### Automated Tests
```bash
# Run JUnit tests
mvn test

# Run integration tests with Docker
mvn verify
```

## 🛠️ Troubleshooting

### Odoo Not Starting
```bash
docker logs ae5-odoo
# Wait 2-3 minutes for PostgreSQL to initialize
# Check postgresql health:
docker logs ae5-postgres
```

### MongoDB Connection Refused
```bash
docker logs ae5-mongodb
# Verify port 27017 is not used:
lsof -i :27017
```

### Spring Boot won't find Odoo
```yaml
# Check application.yml
odoo:
  url: http://localhost:8069  # From Java POV
  # NOT http://ae5-odoo:8069 (that's Docker-only)
```

### GUI doesn't appear
```bash
# On WSL2/headless systems, may need:
export DISPLAY=:0
mvn javafx:run
```

## 📖 API Documentation

### OdooService
```java
public List<Invoice> getInvoices()        // Fetch all invoices from Odoo
public Invoice getInvoiceById(Long id)    // Get single invoice
public void authenticate()                // Connect to Odoo
```

### GuaranteeService
```java
public Guarantee createGuarantee(Guarantee g)
public Guarantee updateGuarantee(String id, Guarantee g)
public void deleteGuarantee(String id)
public Optional<Guarantee> getGuaranteeById(String id)
public List<Guarantee> getAllGuarantees()
public List<Guarantee> getGuaranteesByClient(String client)
public List<Guarantee> getGuaranteesByStatus(String status)
public List<Guarantee> getGuaranteesByCountry(String country)
public List<Guarantee> getGuaranteesByInvoice(Long invoiceId)
public List<Guarantee> searchGuarantees(String client, String status, String country)
public void updateGuaranteeStatus(String id, String status)
```

## 📋 Default Credentials

| System | Username | Password |
|--------|----------|----------|
| JavaFX App | admin | admin |
| Odoo | admin | admin |
| MongoDB | admin | admin_password |

## 📝 Configuration Files

### application.yml
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/ae5_warranty_db
odoo:
  url: http://localhost:8069
  db: odoo
  username: admin
  password: admin
```

### pom.xml
Key dependencies:
- `spring-boot-starter-web` – REST & Spring MVC
- `spring-boot-starter-data-mongodb` – MongoDB ORM
- `javafx-*` – GUI framework
- `org.apache.xmlrpc:xmlrpc-client` – Odoo API

## 🎯 Next Steps / Future Enhancements

- [ ] JWT authentication (replace hardcoded credentials)
- [ ] REST API endpoints (expose CRUD via HTTP)
- [ ] User roles & permissions
- [ ] Warranty renewal notifications
- [ ] Export to PDF/Excel
- [ ] Email notifications
- [ ] Advanced reporting & analytics
- [ ] Multi-language support

## 📄 License

This is a university project. Feel free to use for educational purposes.

## 👨‍🎓 Authors

- **Andreu** (Student ID)

---

**Last Updated:** January 2026  
**Version:** 1.0.0
