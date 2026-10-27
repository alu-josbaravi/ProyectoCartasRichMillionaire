@echo off
cd /d "%~dp0"
title CardTradeDB - Detener
echo Deteniendo MySQL de CardTradeDB...
docker compose down
if errorlevel 1 (
  echo No se pudo detener Docker. Comprueba que Docker Desktop este iniciado.
) else (
  echo MySQL detenido. Los datos se conservan en el volumen de Docker.
)
pause
