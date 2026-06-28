@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "ROOT_DIR=%SCRIPT_DIR%.."
for %%I in ("%ROOT_DIR%") do set "ROOT_DIR=%%~fI"

set "FRONTEND_DIR=%ROOT_DIR%\frontend"
set "BACKEND_DIR=%ROOT_DIR%\backend"

echo Starting Fahmak 3alena Backend and Frontend...
echo Project root: "%ROOT_DIR%"

if not exist "%FRONTEND_DIR%\" (
    echo ERROR: Frontend folder not found: "%FRONTEND_DIR%"
    exit /b 1
)

if not exist "%BACKEND_DIR%\" (
    echo ERROR: Backend folder not found: "%BACKEND_DIR%"
    exit /b 1
)

echo Installing frontend dependencies...
pushd "%FRONTEND_DIR%" || (
    echo ERROR: Could not enter frontend folder: "%FRONTEND_DIR%"
    exit /b 1
)
call npm install
if errorlevel 1 (
    echo ERROR: npm install failed.
    popd
    exit /b 1
)

echo Starting Angular Frontend in a new terminal...
start "Fahmak 3alena Frontend" cmd /k "npm start"
popd

echo Starting Spring Boot Backend...
pushd "%BACKEND_DIR%" || (
    echo ERROR: Could not enter backend folder: "%BACKEND_DIR%"
    exit /b 1
)
call mvnw.cmd spring-boot:run
set "BACKEND_EXIT=%ERRORLEVEL%"
popd

exit /b %BACKEND_EXIT%
