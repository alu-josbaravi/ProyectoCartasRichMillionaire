@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"
title CardTradeDB - Inicio automatico

echo ============================================
echo       CARDTRADEDB - INICIO AUTOMATICO
echo ============================================
echo.

where docker >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Docker no esta instalado o no esta en PATH.
  echo Instala Docker Desktop y vuelve a ejecutar este archivo.
  pause
  exit /b 1
)

docker info >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Docker Desktop no esta iniciado.
  echo Abre Docker Desktop, espera a que termine de arrancar y prueba de nuevo.
  pause
  exit /b 1
)

where java >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Java 17 o posterior no esta instalado o no esta en PATH.
  pause
  exit /b 1
)

where mvn >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Maven no esta instalado o no esta en PATH.
  echo Instala Maven 3.9 o posterior y vuelve a ejecutar este archivo.
  pause
  exit /b 1
)

echo [1/4] Iniciando MySQL con Docker...
docker compose up -d
if errorlevel 1 goto :fallo

echo [2/4] Esperando a que MySQL acepte conexiones...
set /a intentos=0
:esperar_mysql
set /a intentos+=1
docker compose exec -T mysql mysqladmin ping -h localhost -uroot -proot --silent >nul 2>&1
if not errorlevel 1 goto :mysql_listo
if !intentos! GEQ 60 (
  echo [ERROR] MySQL no respondio despues de 60 segundos.
  docker compose logs mysql
  pause
  exit /b 1
)
timeout /t 1 /nobreak >nul
goto :esperar_mysql

:mysql_listo
echo [3/4] MySQL esta listo.
set "DB_URL=jdbc:mysql://localhost:3306/tienda_cartas?useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=Europe/Madrid"
set "DB_USER=root"
set "DB_PASSWORD=root"
set "SERVER_PORT=8080"

echo [4/4] Iniciando Java...
echo.
echo Cuando aparezca Started CardTradeApplication, abre:
echo http://localhost:8080
echo.
echo Esta ventana debe permanecer abierta. Pulsa Ctrl+C para detener Java.
start "" http://localhost:8080
mvn spring-boot:run
exit /b %errorlevel%

:fallo
echo.
echo [ERROR] No se pudo iniciar el proyecto.
echo Revisa los mensajes anteriores.
pause
exit /b 1
