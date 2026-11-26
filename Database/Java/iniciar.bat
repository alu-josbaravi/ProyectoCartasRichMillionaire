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

rem ---- Puerto 8080 libre? (un proceso antiguo seguiria sirviendo la version anterior) ----
netstat -ano | findstr /R /C:":8080 .*LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo.
    echo [ERROR] Ya hay un programa escuchando en el puerto 8080.
    echo Puede ser una version antigua de CardTradeDB que sigue abierta.
    echo Ejecuta detener.bat y vuelve a lanzar este script.
    echo.
    pause
    exit /b 1
)

rem ---- MariaDB / MySQL responde en el puerto 3306? (solo avisa) ----
powershell -NoProfile -Command "if ((Test-NetConnection localhost -Port 3306 -WarningAction SilentlyContinue).TcpTestSucceeded) { exit 0 } else { exit 1 }" >nul 2>&1
if errorlevel 1 (
    echo [AVISO] No responde nada en localhost:3306. Comprueba que la base de datos esta iniciada.
) else (
    echo [OK] La base de datos responde en localhost:3306.
)

rem ---- Conexion a la base de datos ----
rem Se usa lo que ponga src\main\resources\application.properties (ya incluido en el JAR).
rem Para no recompilar al cambiar la contrasena, define antes la variable de entorno
rem SPRING_DATASOURCE_PASSWORD (tiene prioridad sobre el JAR).

rem ---- Abrir el navegador cuando la aplicacion ya haya arrancado ----
start "" powershell -WindowStyle Hidden -NoProfile -Command "Start-Sleep -Seconds 8; Start-Process 'http://localhost:8080'"

echo.
echo [INFO] Iniciando CardTradeDB en http://localhost:8080
echo [INFO] Panel de administracion: http://localhost:8080/admin.html  (usuario foc / contrasena foc)
echo [INFO] El navegador se abrira en unos segundos.
echo [INFO] Para detenerlo, pulsa Ctrl+C.
echo.
java -jar "target\cardtrade-backend-1.0.0.jar"

echo.
echo CardTradeDB se ha detenido.
pause
