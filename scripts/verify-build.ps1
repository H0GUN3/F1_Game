Param()
Write-Host "[verify-build] Generating asset list..." -ForegroundColor Cyan
./gradlew.bat generateAssetList
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "[verify-build] Cleaning and building..." -ForegroundColor Cyan
./gradlew.bat clean build
exit $LASTEXITCODE

