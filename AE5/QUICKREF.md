# AE5 - Quick Reference Card

## 🚀 Quick Start

```bash
# 1. Start Docker (2-3 minutes)
docker-compose up -d

# 2. Build (1 minute)
mvn clean package

# 3. Run (instant)
mvn javafx:run

# 4. Login
# Username: admin
# Password: admin
```

---

## 📍 URLs & Ports

| Service | URL | Port | Credentials |
|---------|-----|------|-------------|
| **JavaFX App** | localhost | 8080 | admin/admin |
| **Odoo** | http://localhost:8069 | 8069 | admin/admin |
| **MongoDB** | mongodb://localhost:27017 | 27017 | admin/admin_password |
| **REST API** | http://localhost:8080/api/guarantees | 8080 | N/A |

---

## 🗄️ MongoDB Commands

```bash
# Connect
docker exec -it ae5-mongodb mongosh -u admin -p admin_password

# Use database
use ae5_warranty_db;

# View guarantees
db.guarantees.find().pretty();

# Count records
db.guarantees.countDocuments();

# Search by client
db.guarantees.find({ client: "John Smith" });

# Update status
db.guarantees.updateOne(
  { _id: "id-here" },
  { $set: { status: "EXPIRED" } }
);
```

---

## 🐳 Docker Commands

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker logs ae5-odoo -f
docker logs ae5-mongodb -f

# Check status
docker ps

# Clean reset (DESTRUCTIVE)
docker-compose down -v
docker-compose up -d
```

---

## 🔨 Maven Commands

```bash
# Build
mvn clean package

# Run
mvn javafx:run

# Test
mvn test

# Clean
mvn clean

# Install dependencies
mvn install
```

---

## 📱 REST API Examples

```bash
# List all warranties
curl http://localhost:8080/api/guarantees

# Get single warranty
curl http://localhost:8080/api/guarantees/{id}

# Create warranty
curl -X POST http://localhost:8080/api/guarantees \
  -H "Content-Type: application/json" \
  -d '{
    "invoiceId": 1001,
    "client": "John Smith",
    "status": "ACTIVE"
  }'

# Search
curl "http://localhost:8080/api/guarantees/search?client=John&status=ACTIVE"

# Delete
curl -X DELETE http://localhost:8080/api/guarantees/{id}
```

---

## 📂 Important Files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven dependencies |
| `docker-compose.yml` | Services definition |
| `application.yml` | Spring configuration |
| `README.md` | Full documentation |
| `DEPLOYMENT.md` | Setup guide |
| `ARCHITECTURE.md` | Technical design |

---

## 🔑 Default Credentials

```
JavaFX:   admin / admin
Odoo:     admin / admin
MongoDB:  admin / admin_password
```

---

## ✅ Checklist Before Deploy

- [ ] Docker installed & running
- [ ] Java 21 installed
- [ ] Maven installed
- [ ] Port 8069, 27017, 8080 available
- [ ] docker-compose up -d works
- [ ] mvn javafx:run launches
- [ ] Can login with admin/admin
- [ ] Invoices load from Odoo
- [ ] Can create warranty in MongoDB

---

## 📊 Table of Contents

| Document | Read When |
|----------|-----------|
| [README.md](README.md) | First time - full overview |
| [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) | Want project status |
| [DEPLOYMENT.md](DEPLOYMENT.md) | Setting up locally |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Understanding design |
| [CONTRIBUTING.md](CONTRIBUTING.md) | Want to develop |
| [Makefile](Makefile) | Using Linux/Mac |

---

## 🆘 Troubleshooting

### Odoo won't start
```bash
docker logs ae5-odoo | grep error
# Wait 3-5 minutes for initialization
```

### MongoDB connection fails
```bash
docker logs ae5-mongodb
docker exec ae5-mongodb mongosh --eval "db.adminCommand('ping')"
```

### JavaFX won't run
```bash
java -version  # Check Java 21+
mvn clean compile
mvn javafx:run
```

### Port already in use
```bash
# Find what's using port
lsof -i :8069
# Kill process or use different port
```

---

## 🎯 Quick Task Reference

| Task | Command |
|------|---------|
| Start everything | `docker-compose up -d && mvn javafx:run` |
| Run tests | `mvn test` |
| Reset data | `docker-compose down -v && docker-compose up -d` |
| View logs | `docker logs ae5-odoo -f` |
| Build only | `mvn clean package` |
| Format code | `mvn spotless:apply` |

---

## 📞 Support

1. Check README.md for overview
2. Read DEPLOYMENT.md for setup issues
3. Review ARCHITECTURE.md for design questions
4. See CONTRIBUTING.md for development help
5. Check Docker logs for runtime errors

---

**Last Updated:** January 2026  
**Version:** 1.0.0  
**Bookmark this for quick reference!** 🔖
