Portable Node for this repo

Goal:
- Provide local Node installation that doesn't require global Node on system.
- Scripts download Node zip from official distribution, extract into `portable_node/current`, and run `npm install` if `package.json` exists.

Files:
- portable_node_setup.bat  -> Windows batch setup (uses PowerShell/tar)
- portable_node_setup.ps1 -> PowerShell setup script (use for more control)
- portable_node_run.bat    -> Runs node.exe from portable install
- portable_node_packages/  -> (created by npm install) local node_modules prefix

Usage:
1. Open cmd as user in repo root.
2. Run: `portable_node_setup.bat` (or `powershell -File .\\portable_node_setup.ps1 -NodeVersion v20.18.0`)
3. If package.json present, npm packages will be installed into `portable_node_packages`.
4. Use `portable_node_run.bat script.js` to run scripts with portable node.

Notes & next steps:
- Default Node version in scripts: v20.18.0. Change by setting NODE_VERSION env var before running batch, or pass parameter to PS script.
- Consider vendorizing a specific Node version and checksum verification for offline installs.
