# 📦 AE5 Warranty System - Deployment Guide

## Quick Deployment (5 minutes)

### Prerequisites Checklist
- [ ] Docker Desktop installed and running
- [ ] Java 21 JDK installed (`java -version` should show 21.x.x)
- [ ] Maven installed (`mvn -version` should work)
- [ ] Git installed

---

## Step 1: Clone & Navigate
```bash
git clone <repository-url>
cd AE5
```

---

## Step 2: Start Docker Services

### Linux/macOS
```bash
# Make setup script executable
chmod +x scripts/setup.sh

# Start Docker containers
docker-compose up -d

# Run setup (optional - initializes sample data)
./scripts/setup.sh
```

### Windows PowerShell
```powershell
# Start Docker containers
docker-compose up -d

# Run setup batch file
.\scripts\setup.bat
```

**Expected Output:**
```
Creating network "ae5-network" with driver "bridge"
Creating ae5-postgres ... done
Creating ae5-odoo ... done
Creating ae5-mongodb ... done
```

---

## Step 3: Wait for Odoo Initialization
The Odoo container needs 2-3 minutes to initialize. Check status:
```bash
docker logs ae5-odoo | tail -20
```

Look for message: `Odoo saas started`

---

## Step 4: Verify Services

### Check Odoo
```bash
curl http://localhost:8069
# Should respond with HTML
```

### Check MongoDB
```bash
docker exec ae5-mongodb mongosh --eval "db.adminCommand('ping')"
# Should output: { ok: 1 }
```

### View PostgreSQL logs
```bash
docker logs ae5-postgres | grep "database system is ready"
```

---

## Step 5: Build Java Application

```bash
# Build the project
mvn clean package

# Expected output:
# [INFO] Building jar: target/ae5-warranty-system-1.0.0.jar
# [INFO] BUILD SUCCESS
```

---

## Step 6: Run JavaFX Application

### Option A: Using Maven Plugin (Recommended)
```bash
mvn javafx:run
```

### Option B: Run JAR directly
```bash
java -jar target/ae5-warranty-system-1.0.0.jar
```

### Login Credentials
- **Username:** `admin`
- **Password:** `admin`

---

## Step 7: Create Demo Invoices in Odoo (Optional)

### Via Web UI
1. Navigate to `http://localhost:8069`
2. Login with: `admin` / `admin`
3. Go to: **Accounting** → **Invoices**
4. Click **Create** and fill in:
   - Customer: (create or select)
   - Invoice Date: Today
   - Product: Any product
   - Quantity & Price
5. Click **Confirm**

The invoice should then appear in the JavaFX app.

### Via Python Script (Automatic)
If `init-odoo.py` is present, execute:
```bash
docker exec ae5-odoo python3 /app/init-odoo.py
```

---

## Verification Checklist

- [ ] Docker containers running: `docker ps` shows 3 containers
- [ ] Odoo accessible: `http://localhost:8069` works
- [ ] MongoDB accessible: Can connect with MongoDB client
- [ ] JavaFX app starts without errors
- [ ] Login with admin/admin succeeds
- [ ] **Invoices tab** shows data from Odoo
- [ ] **Guarantees tab** shows sample data from MongoDB
- [ ] Can create new guarantee
- [ ] Can search/filter guarantees
- [ ] REST API works: `curl http://localhost:8080/api/guarantees`

---

## Common Issues & Solutions

### Issue: Odoo not starting
```bash
# Check logs
docker logs ae5-odoo

# Wait longer (up to 5 minutes)
docker logs ae5-odoo | grep -i "ready"

# Restart if needed
docker-compose restart odoo
```

### Issue: MongoDB connection refused
```bash
# Verify container is running
docker ps | grep mongodb

# Check MongoDB logs
docker logs ae5-mongodb

# Restart MongoDB
docker-compose restart mongodb
```

### Issue: Java app won't start
```bash
# Check if port 8080 is available
# On Windows:
netstat -ano | findstr :8080

# On Linux/Mac:
lsof -i :8080

# Rebuild
mvn clean compile
mvn javafx:run
```

### Issue: Invoices not appearing
1. Ensure Odoo has at least one invoice
2. Check Odoo login in `application.yml`:
   ```yaml
   odoo:
     username: admin
     password: admin
   ```
3. Verify Odoo connection via XML-RPC:
   ```bash
   docker exec ae5-odoo python3 << 'EOF'
   import xmlrpc.client
   common = xmlrpc.client.ServerProxy("http://localhost:8069/xmlrpc/2/common")
   uid = common.authenticate("odoo", "admin", "admin", {})
   print(f"Auth OK: {uid}")
   EOF
   ```

---

## Maintenance

### View Logs
```bash
# Odoo logs
docker logs ae5-odoo -f

# MongoDB logs
docker logs ae5-mongodb -f

# Java app logs
# Check: logs/ae5-warranty-system.log
```

### Stop Services
```bash
docker-compose down
```

### Reset All Data
```bash
# ⚠️ WARNING: Deletes all databases
docker-compose down -v
docker-compose up -d
```

### Backup Data
```bash
# Export MongoDB
docker exec ae5-mongodb mongodump --out /backup/ae5_backup

# Export PostgreSQL
docker exec ae5-postgres pg_dump -U odoo odoo > ae5_odoo_backup.sql
```

---

## Performance Tuning

### MongoDB Indexes
```bash
docker exec ae5-mongodb mongosh ae5_warranty_db << 'EOF'
db.guarantees.createIndex({ client: 1 })
db.guarantees.createIndex({ status: 1 })
db.guarantees.createIndex({ invoiceId: 1 })
db.guarantees.createIndex({ country: 1 })
EOF
```

### Increase Docker Memory
Edit `docker-compose.yml`:
```yaml
services:
  odoo:
    deploy:
      resources:
        limits:
          memory: 2G
```

---

## Production Deployment

For production use:

1. **Change default credentials:**
   - Update `odoo.password` in `application.yml`
   - Update MongoDB credentials in `docker-compose.yml`
   - Update Spring security settings

2. **Use SSL/TLS:**
   - Set up reverse proxy (nginx)
   - Configure HTTPS for all endpoints

3. **Database Backups:**
   - Set up automated MongoDB backups
   - Set up automated PostgreSQL backups
   - Store backups off-site

4. **Monitoring:**
   - Set up logging aggregation (ELK stack)
   - Monitor Docker resource usage
   - Set up alerting

5. **Scaling:**
   - Use Docker Swarm or Kubernetes
   - Scale Odoo instances separately

---

## Support & Troubleshooting

See **README.md** for:
- Architecture overview
- API documentation
- Configuration details

For issues:
1. Check Docker logs: `docker logs <container-name>`
2. Check application logs: `tail -f logs/ae5-warranty-system.log`
3. Verify network connectivity: `docker network inspect ae5-network`
4. Check MongoDB connectivity: `docker exec ae5-mongodb mongosh --eval "db.version()"`

---

**Last Updated:** January 2026  
**Version:** 1.0.0
