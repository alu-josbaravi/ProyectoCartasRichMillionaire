@echo off
cd /d "%~dp0"
title CardTradeDB - Compilar
where mvn >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Maven no esta instalado o no esta en PATH.
  pause
  exit /b 1
)
mvn clean package
if errorlevel 1 (
  echo [ERROR] La compilacion ha fallado.
) else (
  echo.
  echo Compilacion terminada: target\cardtrade-backend-1.0.0.jar
)
pause
