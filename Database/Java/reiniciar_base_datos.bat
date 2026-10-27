@echo off
setlocal
cd /d "%~dp0"
title CardTradeDB - Reiniciar base de datos
echo =====================================================
echo ADVERTENCIA: se borraran todos los datos de CardTradeDB
echo y se volveran a cargar CardTradeDB.sql e inserts.sql.
echo =====================================================
set /p RESPUESTA=Escribe REINICIAR para continuar: 
if /I not "%RESPUESTA%"=="REINICIAR" (
  echo Operacion cancelada.
  pause
  exit /b 0
)

docker compose down -v
if errorlevel 1 goto :fallo
docker compose up -d
if errorlevel 1 goto :fallo
echo Base de datos reiniciada. Espera unos segundos antes de abrir la aplicacion.
pause
exit /b 0

:fallo
echo [ERROR] No se pudo reiniciar la base de datos.
pause
exit /b 1
