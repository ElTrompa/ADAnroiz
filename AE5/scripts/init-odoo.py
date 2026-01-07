#!/usr/bin/env python3
"""
Initialize Odoo with demo invoices for the AE5 Warranty System.
Run this after Odoo is fully initialized.
"""

import xmlrpc.client
import sys
from datetime import date, timedelta

# Configuration
ODOO_URL = "http://odoo:8069"  # From inside Docker network
DB = "odoo"
LOGIN = "admin"
PASSWORD = "admin"

def get_uid():
    """Authenticate and get UID"""
    common = xmlrpc.client.ServerProxy(f"{ODOO_URL}/xmlrpc/2/common")
    uid = common.authenticate(DB, LOGIN, PASSWORD, {})
    return uid

def create_customers_and_invoices():
    """Create sample customers and invoices"""
    try:
        uid = get_uid()
        print(f"✓ Authenticated with UID: {uid}")
        
        models = xmlrpc.client.ServerProxy(f"{ODOO_URL}/xmlrpc/2/object")
        
        # Create customers
        print("\n📝 Creating demo customers...")
        customers = [
            {"name": "John Smith", "country_id": False},
            {"name": "Jane Doe", "country_id": False},
            {"name": "Carlos García", "country_id": False},
        ]
        
        customer_ids = []
        for customer in customers:
            customer_id = models.execute_kw(
                DB, uid, PASSWORD, "res.partner", "create",
                [customer]
            )
            customer_ids.append(customer_id)
            print(f"  ✓ Created customer '{customer['name']}' (ID: {customer_id})")
        
        # Create invoices
        print("\n📄 Creating demo invoices...")
        invoices = [
            {
                "move_type": "out_invoice",
                "partner_id": customer_ids[0],
                "invoice_date": str(date.today() - timedelta(days=30)),
                "line_ids": [(0, 0, {
                    "name": "Bicicleta Carbono",
                    "quantity": 1,
                    "price_unit": 1000.00
                })]
            },
            {
                "move_type": "out_invoice",
                "partner_id": customer_ids[1],
                "invoice_date": str(date.today() - timedelta(days=60)),
                "line_ids": [(0, 0, {
                    "name": "Maillot Pro",
                    "quantity": 2,
                    "price_unit": 500.00
                })]
            },
            {
                "move_type": "out_invoice",
                "partner_id": customer_ids[2],
                "invoice_date": str(date.today() - timedelta(days=10)),
                "line_ids": [(0, 0, {
                    "name": "Rodillo Elite",
                    "quantity": 1,
                    "price_unit": 1500.00
                })]
            },
        ]
        
        invoice_ids = []
        for i, invoice in enumerate(invoices):
            try:
                invoice_id = models.execute_kw(
                    DB, uid, PASSWORD, "account.move", "create",
                    [invoice]
                )
                invoice_ids.append(invoice_id)
                print(f"  ✓ Created invoice #{i+1001} (ID: {invoice_id})")
            except Exception as e:
                print(f"  ✗ Failed to create invoice: {e}")
        
        print("\n✅ Odoo initialization complete!")
        print(f"   Created {len(customer_ids)} customers and {len(invoice_ids)} invoices")
        return True
        
    except Exception as e:
        print(f"❌ Error: {e}")
        return False

if __name__ == "__main__":
    # Wait for Odoo to be ready
    import time
    for i in range(30):
        try:
            get_uid()
            print("✓ Odoo is ready")
            break
        except Exception:
            if i < 29:
                print(f"⏳ Waiting for Odoo... ({i+1}/30)")
                time.sleep(2)
            else:
                print("❌ Odoo did not become ready in time")
                sys.exit(1)
    
    # Create demo data
    success = create_customers_and_invoices()
    sys.exit(0 if success else 1)
