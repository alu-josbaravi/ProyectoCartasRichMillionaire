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
 
echo [OK] Java encontrado.
 
rem ---- Comprobar que MariaDB responde en el puerto 3306 ----
powershell -NoProfile -Command "if ((Test-NetConnection localhost -Port 3306 -WarningAction SilentlyContinue).TcpTestSucceeded) { exit 0 } else { exit 1 }" >nul 2>&1
if errorlevel 1 (
    echo [AVISO] No responde nada en localhost:3306. Comprueba que MariaDB esta iniciado.
    echo.
) else (
    echo [OK] MariaDB responde en localhost:3306.
)
 
rem ---- Conexion a la base de datos ----
rem Estas variables sobrescriben application.properties (nombres que Spring Boot si lee).
rem Si ya definiste SPRING_DATASOURCE_PASSWORD en Windows, no se vuelve a pedir.
set "SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/tienda_cartas"
set "SPRING_DATASOURCE_USERNAME=root"
 
if not defined SPRING_DATASOURCE_PASSWORD (
    set /p "SPRING_DATASOURCE_PASSWORD=Contrasena de MariaDB para root: "
)
 
rem ---- Abrir el navegador cuando la aplicacion ya haya arrancado ----
start "" powershell -WindowStyle Hidden -NoProfile -Command "Start-Sleep -Seconds 8; Start-Process 'http://localhost:8080'"
 
echo.
echo [INFO] Iniciando CardTradeDB en http://localhost:8080
echo [INFO] El navegador se abrira en unos segundos.
echo [INFO] Para detenerlo, pulsa Ctrl+C.
echo.
java -jar "target\cardtrade-backend-1.0.0.jar"
 
echo.
echo CardTradeDB se ha detenido.
pause