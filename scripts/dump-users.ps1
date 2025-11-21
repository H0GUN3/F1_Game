Param()
Write-Host "[dump-users] Listing users from SQLite..." -ForegroundColor Cyan
./gradlew.bat -q :core:dumpUsers
exit $LASTEXITCODE

