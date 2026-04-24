param(
    [string]$NodeVersion = 'v20.18.0'
)
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$Target = Join-Path $ScriptDir 'portable_node'
if (-not (Test-Path $Target)) { New-Item -ItemType Directory -Path $Target | Out-Null }
$zip = Join-Path $ScriptDir "node-$NodeVersion-win-x64.zip"
$uri = "https://nodejs.org/dist/$NodeVersion/node-$NodeVersion-win-x64.zip"
Write-Output "Downloading Node $NodeVersion from $uri"
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
# Rename extracted folder to 'current'
$extracted = Get-ChildItem -Path $Target -Directory | Where-Object { $_.Name -like 'node-*' } | Select-Object -First 1
if ($extracted) {
    $dest = Join-Path $Target 'current'
    if (Test-Path $dest) { Remove-Item -Recurse -Force $dest }
    Move-Item -Path $extracted.FullName -Destination $dest
}
if (Test-Path (Join-Path $ScriptDir 'package.json')) {
    Write-Output 'Found package.json. Running npm install using portable node.'
    & (Join-Path $Target 'current\node.exe') (Join-Path $Target 'current\node_modules\npm\bin\npm-cli.js') install --prefix (Join-Path $ScriptDir 'portable_node_packages')
}
Write-Output 'Portable Node setup complete.'
