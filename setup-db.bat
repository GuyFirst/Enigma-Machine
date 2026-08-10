@echo off
REM One-time setup: creates the "enigma" database that application.yml expects.
REM PostgreSQL 17 was installed with superuser "postgres" / password "enigma" (matches application.yml).

set PGPASSWORD=enigma
set PSQL="C:\Program Files\PostgreSQL\17\bin\psql.exe"

echo Creating database "enigma" (safe to ignore "already exists" error on reruns)...
%PSQL% -U postgres -h localhost -c "CREATE DATABASE enigma;"

echo.
echo Current databases:
%PSQL% -U postgres -h localhost -l
