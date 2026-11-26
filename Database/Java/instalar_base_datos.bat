@echo off
setlocal
cd /d "%~dp0"
title CardTradeDB - Instalar base de datos

echo ============================================
echo CARDTRADEDB - INSTALAR BASE DE DATOS
echo ============================================
echo.

rem Los SQL estan en la carpeta Database (una sola copia), dos niveles por encima de este script.
set "SQL_DIR=%~dp0.."

if not exist "%SQL_DIR%\CardTradeDB.sql" (
    echo [ERROR] No existe %SQL_DIR%\CardTradeDB.sql
    pause
    exit /b 1
)

if not exist "%SQL_DIR%\inserts.sql" (
    echo [ERROR] No existe %SQL_DIR%\inserts.sql
    pause
    exit /b 1
)

rem ---- Buscar el cliente de linea de comandos (MariaDB o MySQL) ----
set "MYSQL_EXE="
for %%X in (mariadb.exe mysql.exe) do if not defined MYSQL_EXE for /f "delims=" %%P in ('where %%X 2^>nul') do if not defined MYSQL_EXE set "MYSQL_EXE=%%P"

if not defined MYSQL_EXE for /d %%D in ("C:\Program Files\MariaDB*") do if exist "%%D\bin\mariadb.exe" set "MYSQL_EXE=%%D\bin\mariadb.exe"
if not defined MYSQL_EXE for /d %%D in ("C:\Program Files\MariaDB*") do if exist "%%D\bin\mysql.exe" set "MYSQL_EXE=%%D\bin\mysql.exe"
if not defined MYSQL_EXE for /d %%D in ("C:\Program Files\MySQL\MySQL Server *") do if exist "%%D\bin\mysql.exe" set "MYSQL_EXE=%%D\bin\mysql.exe"

if not defined MYSQL_EXE (
    echo [ERROR] No se encuentra mariadb.exe ni mysql.exe.
    echo Instala MariaDB o MySQL, o anade su carpeta bin al PATH.
    pause
    exit /b 1
)

echo [OK] Cliente encontrado: %MYSQL_EXE%
echo.
echo ATENCION: esto BORRA la base de datos tienda_cartas y la crea de nuevo con los datos de prueba.
choice /C SN /M "Continuar"
if errorlevel 2 (
    echo Cancelado.
    pause
    exit /b 0
)

echo.
echo [1/2] Creando la base de datos y las tablas.
echo Introduce la contrasena del usuario root.
"%MYSQL_EXE%" -u root -p --default-character-set=utf8mb4 < "%SQL_DIR%\CardTradeDB.sql"
if errorlevel 1 (
    echo [ERROR] No se pudo crear la base de datos.
    pause
    exit /b 1
)

echo.
echo [2/2] Cargando los datos iniciales.
echo Introduce de nuevo la contrasena del usuario root.
"%MYSQL_EXE%" -u root -p --default-character-set=utf8mb4 tienda_cartas < "%SQL_DIR%\inserts.sql"
if errorlevel 1 (
    echo [ERROR] No se pudieron cargar los datos iniciales.
    pause
    exit /b 1
)

echo.
echo [OK] Base de datos instalada correctamente.
echo La cuenta de desarrollo foc / foc se crea sola la primera vez que arranca la aplicacion.
pause
