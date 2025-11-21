Param()
Write-Host "[run-game] Launching desktop app..." -ForegroundColor Cyan
./gradlew.bat lwjgl3:run
exit $LASTEXITCODE

