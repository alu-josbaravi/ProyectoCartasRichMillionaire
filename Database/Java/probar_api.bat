@echo off
setlocal
cd /d "%~dp0"
title CardTradeDB - Probar la API

where java >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no esta instalado o no esta incluido en PATH.
    pause
    exit /b 1
)

echo Con la aplicacion arrancada (iniciar.bat), comprueba login, permisos y panel de administracion.
echo Requiere un JDK 17 o superior ^(el que ya usas con IntelliJ^).
echo.
java pruebas\ProbarApi.java %1
echo.
pause
