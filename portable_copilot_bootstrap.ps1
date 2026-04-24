$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$Target = Join-Path $ScriptDir 'portable_copilot'
if (-not (Test-Path $Target)) { New-Item -ItemType Directory -Path $Target | Out-Null }
$exe = Join-Path $Target 'copilot.exe'
if (Test-Path $exe) { & $exe @args; exit }
Write-Output 'Copilot not found. Downloading latest Windows release...'
$zip = Join-Path $ScriptDir 'copilot.zip'
$uri = 'https://github.com/github/copilot-cli/releases/latest/download/copilot-windows-amd64.zip'
Invoke-WebRequest -Uri $uri -OutFile $zip -UseBasicParsing
Try {
    if (Test-Path "$env:SystemRoot\System32\tar.exe") {
        & "$env:SystemRoot\System32\tar.exe" -xf $zip -C $Target
    } else {
        Expand-Archive -LiteralPath $zip -DestinationPath $Target -Force
    }
} Finally {
    Remove-Item -Path $zip -ErrorAction SilentlyContinue
}
if (Test-Path $exe) { & $exe @args } else {
    # If copilot.exe extracted to script dir, move
    $maybe = Join-Path $ScriptDir 'copilot.exe'
    if (Test-Path $maybe) { Move-Item $maybe $exe; & $exe @args } else { Write-Error 'Copilot executable not found after extraction.' }
}
