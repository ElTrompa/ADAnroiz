.PHONY: help setup build run test logs clean stop

help:
	@echo "AE5 Warranty Management System - Makefile"
	@echo ""
	@echo "Available targets:"
	@echo "  setup          - Start Docker services and initialize data"
	@echo "  build          - Build the Maven project"
	@echo "  run            - Run the JavaFX application"
	@echo "  dev            - Build and run (combined)"
	@echo "  test           - Run unit tests"
	@echo "  logs-odoo      - Show Odoo logs"
	@echo "  logs-mongo     - Show MongoDB logs"
	@echo "  logs-app       - Show application logs"
	@echo "  docker-status  - Check Docker services status"
	@echo "  clean          - Clean Maven build artifacts"
	@echo "  stop           - Stop Docker services"
	@echo "  reset          - Reset all data (DESTRUCTIVE)"
	@echo ""

setup:
	@echo "Starting Docker services..."
	docker-compose up -d
	@echo "Waiting for services to be ready..."
	sleep 3
	@echo "Running setup script..."
	chmod +x scripts/setup.sh
	./scripts/setup.sh

build:
	@echo "Building Maven project..."
	mvn clean package

run:
	@echo "Running JavaFX application..."
	mvn javafx:run

dev: build run

test:
	@echo "Running tests..."
	mvn test

logs-odoo:
	docker logs -f ae5-odoo

logs-mongo:
	docker logs -f ae5-mongodb

logs-app:
	tail -f logs/ae5-warranty-system.log

docker-status:
	@echo "Docker Services Status:"
	@docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep ae5

clean:
	@echo "Cleaning Maven artifacts..."
	mvn clean
	rm -rf logs/

stop:
	@echo "Stopping Docker services..."
	docker-compose down

reset:
	@echo "WARNING: This will delete all data!"
	@read -p "Are you sure? (y/N) " -n 1 -r; \
	echo; \
	if [[ $$REPLY =~ ^[Yy]$$ ]]; then \
		docker-compose down -v; \
		docker-compose up -d; \
		echo "Reset complete."; \
	else \
		echo "Cancelled."; \
	fi
