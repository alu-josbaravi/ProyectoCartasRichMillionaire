@echo off
setlocal
cd /d "%~dp0"
title CardTradeDB - Iniciar

echo ============================================
echo CARDTRADEDB - INICIO SIN MAVEN NI DOCKER
echo ============================================
echo.

where java >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no esta instalado o no esta incluido en PATH.
    pause
    exit /b 1
)

if not exist "target\cardtrade-backend-1.0.0.jar" (
    echo [ERROR] No se encuentra el archivo:
    echo target\cardtrade-backend-1.0.0.jar
    echo.
    echo Ejecuta recompilar.bat y genera el JAR desde IntelliJ IDEA.
    pause
    exit /b 1
)

set "DB_URL=jdbc:mysql://localhost:3306/tienda_cartas?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Madrid"
set "DB_USER=root"
set "DB_PASSWORD=root"

echo [OK] Java encontrado.
echo [INFO] La aplicacion se abrira en http://localhost:8080
start "" "http://localhost:8080"

echo [INFO] Iniciando CardTradeDB...
echo [INFO] Para detenerlo, pulsa Ctrl+C.
echo.
java -jar "target\cardtrade-backend-1.0.0.jar"

echo.
echo CardTradeDB se ha detenido.
pause
