#!/bin/bash
# Initialize Odoo and MongoDB with demo data
# Run this after: docker-compose up -d

set -e

echo "========================================="
echo "AE5 Warranty System - Docker Setup"
echo "========================================="

# Check if Docker services are running
echo -e "\n🔍 Checking Docker services..."
if ! docker ps | grep -q ae5-odoo; then
    echo "❌ Odoo container not found. Run: docker-compose up -d"
    exit 1
fi

if ! docker ps | grep -q ae5-mongodb; then
    echo "❌ MongoDB container not found. Run: docker-compose up -d"
    exit 1
fi

echo "✓ Docker services are running"

# Wait for Odoo to be ready
echo -e "\n⏳ Waiting for Odoo to initialize (this may take 2-3 minutes)..."
max_attempts=60
attempt=0
while [ $attempt -lt $max_attempts ]; do
    if docker logs ae5-odoo 2>&1 | grep -q "Odoo saas"; then
        echo "✓ Odoo is ready"
        break
    fi
    attempt=$((attempt + 1))
    if [ $((attempt % 10)) -eq 0 ]; then
        echo "  Still waiting... ($attempt/$max_attempts)"
    fi
    sleep 5
done

if [ $attempt -eq $max_attempts ]; then
    echo "⚠️  Odoo initialization timed out. It may still be starting."
    echo "   Check: docker logs ae5-odoo"
fi

# Optional: Create Odoo demo data
echo -e "\n📝 Creating Odoo demo data..."
echo "   (Skip if you prefer to create invoices manually via UI)"

# Check if python script exists
if [ -f "scripts/init-odoo.py" ]; then
    docker exec ae5-odoo python3 << 'EOF'
import xmlrpc.client
import sys
from datetime import date, timedelta

DB = "odoo"
LOGIN = "admin"
PASSWORD = "admin"

try:
    common = xmlrpc.client.ServerProxy("http://localhost:8069/xmlrpc/2/common")
    uid = common.authenticate(DB, LOGIN, PASSWORD, {})
    
    models = xmlrpc.client.ServerProxy("http://localhost:8069/xmlrpc/2/object")
    
    # Create a sample customer
    customer_id = models.execute_kw(
        DB, uid, PASSWORD, "res.partner", "create",
        [{"name": "Demo Customer 1"}]
    )
    print(f"✓ Created demo customer (ID: {customer_id})")
    
    # Create a sample invoice
    invoice_data = {
        "move_type": "out_invoice",
        "partner_id": customer_id,
        "invoice_date": str(date.today()),
        "line_ids": [(0, 0, {
            "name": "Demo Product",
            "quantity": 1,
            "price_unit": 100.0
        })]
    }
    invoice_id = models.execute_kw(
        DB, uid, PASSWORD, "account.move", "create",
        [invoice_data]
    )
    print(f"✓ Created demo invoice (ID: {invoice_id})")
    
except Exception as e:
    print(f"⚠️  Could not create demo data: {e}")
    print("   You can create invoices manually in the Odoo UI")
EOF
else
    echo "   (Odoo init script not found, skipping)"
fi

# Verify MongoDB
echo -e "\n🍃 Checking MongoDB..."
if docker exec ae5-mongodb mongosh --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
    echo "✓ MongoDB is responding"
else
    echo "⚠️  MongoDB may not be fully ready yet"
fi

# Print summary
echo -e "\n========================================="
echo "✅ Setup Complete!"
echo "========================================="
echo ""
echo "🌐 Service URLs:"
echo "   Odoo:    http://localhost:8069"
echo "   MongoDB: mongodb://localhost:27017"
echo ""
echo "🔑 Credentials:"
echo "   Odoo:    admin / admin"
echo "   MongoDB: admin / admin_password"
echo ""
echo "📚 Next Steps:"
echo "   1. Build the Java app:"
echo "      mvn clean package"
echo ""
echo "   2. Run the JavaFX application:"
echo "      mvn javafx:run"
echo ""
echo "   3. Login with: admin / admin"
echo ""
echo "📖 Documentation:"
echo "   See README.md for detailed instructions"
echo ""
echo "⚠️  Troubleshooting:"
echo "   - Check Docker logs:     docker logs ae5-odoo"
echo "   - Reset everything:      docker-compose down -v"
echo "   - Full restart:          docker-compose up -d"
echo ""
