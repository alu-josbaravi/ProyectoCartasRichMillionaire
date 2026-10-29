@echo off
setlocal
cd /d "%~dp0"
title CardTradeDB - Recompilar sin Maven instalado

echo ============================================
echo CARDTRADEDB - RECOMPILAR CON INTELLIJ IDEA
echo ============================================
echo.
echo No tienes Maven instalado en Windows.
echo IntelliJ IDEA puede usar su Maven integrado.
echo.
echo Cuando se abra IntelliJ IDEA:
echo.
echo   1. Espera a que termine de cargar el proyecto.
echo   2. Abre el panel Maven de la derecha.
echo   3. Abre CardTrade Backend.
echo   4. Abre Lifecycle.
echo   5. Haz doble clic en package.
echo   6. Espera a BUILD SUCCESS.
echo.
echo El JAR actualizado se guardara en:
echo target\cardtrade-backend-1.0.0.jar
echo.
pause
call "%~dp0abrir_intellij.bat"
