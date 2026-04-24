@echo off
setlocal
set BASE_DIR=%~dp0
set NODE_DIR=%BASE_DIR%portable_node\current
if not exist "%NODE_DIR%\node.exe" (
  echo Portable Node not installed. Run portable_node_setup.bat first.
  exit /b 1
)
"%NODE_DIR%\node.exe" %*
