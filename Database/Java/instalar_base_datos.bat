@echo off
setlocal
cd /d "%~dp0"
title CardTradeDB - Instalar base de datos

echo ============================================
echo CARDTRADEDB - INSTALAR BASE DE DATOS
echo ============================================
echo.

set "MYSQL_EXE="
where mysql >nul 2>&1
if not errorlevel 1 set "MYSQL_EXE=mysql"

if not defined MYSQL_EXE if exist "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" set "MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
if not defined MYSQL_EXE if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set "MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

if not defined MYSQL_EXE (
    echo [ERROR] No se encuentra mysql.exe.
    echo Instala MySQL Server o anade su carpeta bin al PATH.
    pause
    exit /b 1
)

if not exist "database\CardTradeDB.sql" (
    echo [ERROR] No existe database\CardTradeDB.sql
    pause
    exit /b 1
)

if not exist "database\inserts.sql" (
    echo [ERROR] No existe database\inserts.sql
    pause
    exit /b 1
)

echo [1/2] Creando la base de datos y las tablas.
echo Introduce la contrasena del usuario root de MySQL.
"%MYSQL_EXE%" -u root -p < "database\CardTradeDB.sql"
if errorlevel 1 (
    echo [ERROR] No se pudo crear la base de datos.
    pause
    exit /b 1
)

echo.
echo [2/2] Cargando los datos iniciales.
echo Introduce de nuevo la contrasena del usuario root de MySQL.
"%MYSQL_EXE%" -u root -p tienda_cartas < "database\inserts.sql"
if errorlevel 1 (
    echo [ERROR] No se pudieron cargar los datos iniciales.
    pause
    exit /b 1
)

echo.
echo [OK] Base de datos instalada correctamente.
pause
