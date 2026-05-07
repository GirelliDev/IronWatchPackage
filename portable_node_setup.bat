@echo off
setlocal
set BASE_DIR=%~dp0
set TARGET=%BASE_DIR%portable_node
if not exist "%TARGET%" mkdir "%TARGET%"
REM Default Node version. Change NODE_VERSION env var to override (e.g., set NODE_VERSION=v20.18.0)
if "%NODE_VERSION%"=="" set NODE_VERSION=v20.18.0
set ZIP_NAME=node-%NODE_VERSION%-win-x64.zip
echo Downloading Node %NODE_VERSION% ...
set DOWNLOAD_URL=https://nodejs.org/dist/%NODE_VERSION%/node-%NODE_VERSION%-win-x64.zip
echo URL: %DOWNLOAD_URL%
powershell -NoProfile -Command "try { Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%BASE_DIR%\%ZIP_NAME%' -UseBasicParsing } catch { Write-Error 'Download failed. Edit script to correct NODE_VERSION or download manually.'; exit 1 }"
if exist "%SYSTEMROOT%\System32\tar.exe" (
  tar -xf "%BASE_DIR%\%ZIP_NAME%" -C "%TARGET%"
) else (
  powershell -NoProfile -Command "Expand-Archive -LiteralPath '%BASE_DIR%\%ZIP_NAME%' -DestinationPath '%TARGET%' -Force"
)
del "%BASE_DIR%\%ZIP_NAME%" 2>nul || rem ignore
echo Node extracted to %TARGET%\node-%NODE_VERSION%
echo Creating symlink folder 'current' for convenience
if exist "%TARGET%\current" rmdir /s /q "%TARGET%\current"
move "%TARGET%\node-%NODE_VERSION%" "%TARGET%\current" >nul 2>nul || ren "%TARGET%\node-%NODE_VERSION%" current 2>nul
echo Installing npm packages from package.json if exists...
if exist "%BASE_DIR%package.json" (
  echo Running npm install with portable node...
  "%TARGET%\current\node.exe" "%TARGET%\current\node_modules\npm\bin\npm-cli.js" install --prefix "%BASE_DIR%\portable_node_packages"
) else (
  echo No package.json found in repo root. Skip npm install.
)
echo Setup complete. Use portable_node_run.bat to execute node/npm from portable installation.
