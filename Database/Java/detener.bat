@echo off
setlocal enabledelayedexpansion
title CardTradeDB - Detener

echo ============================================
echo CARDTRADEDB - DETENER EL SERVIDOR (puerto 8080)
echo ============================================
echo.

set "ENCONTRADO="
for /f "tokens=5" %%P in ('netstat -ano ^| findstr /R /C:":8080 .*LISTENING"') do (
    set "ENCONTRADO=1"
    echo Proceso que usa el puerto 8080 ^(PID %%P^):
    tasklist /FI "PID eq %%P" /NH
    echo.
    choice /C SN /M "Cerrar este proceso"
    if not errorlevel 2 (
        taskkill /PID %%P /F
    ) else (
        echo No se ha cerrado.
    )
)

if not defined ENCONTRADO echo No hay ningun programa escuchando en el puerto 8080.
echo.
pause
