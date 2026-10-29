@echo off
setlocal
cd /d "%~dp0"
title CardTradeDB - Abrir IntelliJ IDEA

set "IDEA_EXE=C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.4\bin\idea64.exe"

if not exist "%IDEA_EXE%" (
    echo [ERROR] No se encuentra IntelliJ IDEA en:
    echo %IDEA_EXE%
    echo.
    echo Modifica IDEA_EXE dentro de este archivo si cambia la version.
    pause
    exit /b 1
)

start "" "%IDEA_EXE%" "%CD%"
