@echo off
REM Initialize AE5 Warranty System on Windows
REM Prerequisites: Docker Desktop, Maven, Java 21

cls
echo =========================================
echo AE5 Warranty System - Docker Setup (Windows)
echo =========================================

echo.
echo [1] Checking Docker services...

docker ps --format "{{.Names}}" | findstr "ae5-odoo" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Odoo container not found. Run: docker-compose up -d
    exit /b 1
)

docker ps --format "{{.Names}}" | findstr "ae5-mongodb" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] MongoDB container not found. Run: docker-compose up -d
    exit /b 1
)

echo [OK] Docker services are running

echo.
echo [2] Waiting for Odoo to initialize (2-3 minutes)...

setlocal enabledelayedexpansion
set "attempt=0"
set "max_attempts=60"

:wait_loop
docker logs ae5-odoo 2>&1 | findstr "Odoo saas" >nul 2>&1
if not errorlevel 1 (
    echo [OK] Odoo is ready
    goto odoo_ready
)

set /a attempt=attempt+1
if !attempt! equ %max_attempts% (
    echo [WARNING] Odoo initialization timed out. Check: docker logs ae5-odoo
    goto odoo_ready
)

set /a mod=!attempt! %% 10
if !mod! equ 0 (
    echo [INFO] Still waiting... (!attempt!/%max_attempts%)
)

timeout /t 5 /nobreak > nul
goto wait_loop

:odoo_ready
echo.
echo [3] Verifying MongoDB...

docker exec ae5-mongodb mongosh --eval "db.adminCommand('ping')" >nul 2>&1
if errorlevel 1 (
    echo [WARNING] MongoDB may not be fully ready yet
) else (
    echo [OK] MongoDB is responding
)

echo.
echo =========================================
echo [OK] Setup Complete!
echo =========================================
echo.
echo URLs:
echo   - Odoo:    http://localhost:8069
echo   - MongoDB: mongodb://localhost:27017
echo.
echo Credentials:
echo   - Odoo:    admin / admin
echo   - MongoDB: admin / admin_password
echo.
echo Next Steps:
echo   1. Build: mvn clean package
echo   2. Run:   mvn javafx:run
echo   3. Login: admin / admin
echo.
echo Troubleshooting:
echo   - Check logs:    docker logs ae5-odoo
echo   - Reset all:     docker-compose down -v
echo   - Restart:       docker-compose up -d
echo.
