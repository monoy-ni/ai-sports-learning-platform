$ErrorActionPreference = "Stop"

Push-Location frontend
if (Test-Path node_modules) {
  npm run typecheck
  npm run build
} else {
  Write-Host "Skip frontend checks: frontend/node_modules does not exist. Run npm install first."
}
Pop-Location

Push-Location backend
mvn test
Pop-Location

Push-Location ai-service
if (Test-Path .venv) {
  .\.venv\Scripts\python -m pytest
} else {
  python -m pytest
}
Pop-Location
