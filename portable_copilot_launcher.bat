@echo off
setlocal
set BASE_DIR=%~dp0
set TARGET=%BASE_DIR%portable_copilot
if not exist "%TARGET%" mkdir "%TARGET%"

REM If binary exists, run
if exist "%TARGET%\copilot.exe" goto run_binary

echo Copilot binary not found.

echo Downloading copilot Windows release...
curl -L -o "%BASE_DIR%copilot.zip" "https://github.com/github/copilot-cli/releases/latest/download/copilot-windows-amd64.zip"
REM Try to extract using tar (Windows 10+) else use PowerShell Expand-Archive
if exist "%SYSTEMROOT%\System32\tar.exe" (
  tar -xf "%BASE_DIR%copilot.zip" -C "%TARGET%"
) else (
  powershell -NoProfile -Command "Expand-Archive -LiteralPath '%BASE_DIR%copilot.zip' -DestinationPath '%TARGET%'"
)
del "%BASE_DIR%copilot.zip" 2>nul || rem ignore

if exist "%TARGET%\copilot.exe" goto run_binary

echo Copilot binary not found after download. Trying node-based fallback...

set NODE_DIR=%BASE_DIR%portable_node\current
set PKG_PREFIX=%BASE_DIR%portable_node_packages

if exist "%NODE_DIR%\node.exe" (
  REM Ensure copilot npm package installed
  if not exist "%PKG_PREFIX%\node_modules\.bin\copilot.cmd" (
    echo Installing @githubnext/copilot-cli into %PKG_PREFIX%...
    "%NODE_DIR%\node.exe" "%NODE_DIR%\node_modules\npm\bin\npm-cli.js" install --prefix "%PKG_PREFIX%" @githubnext/copilot-cli@latest
  )
  REM Run installed binary if present
  if exist "%PKG_PREFIX%\node_modules\.bin\copilot.cmd" (
    call "%PKG_PREFIX%\node_modules\.bin\copilot.cmd" %*
    exit /b %ERRORLEVEL%
  ) else if exist "%PKG_PREFIX%\node_modules\.bin\copilot" (
    "%NODE_DIR%\node.exe" "%PKG_PREFIX%\node_modules\.bin\copilot" %*
    exit /b %ERRORLEVEL%
  ) else (
    echo Node-based copilot not found after install.
  )
) else (
  echo Portable Node not installed. Run portable_node_setup.bat first.
)

echo No copilot available. Exiting with error.
exit /b 1

:run_binary
"%TARGET%\copilot.exe" %*
