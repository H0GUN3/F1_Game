param(
  [int]$Tcp = 54555,
  [int]$Udp = 54777
)

Write-Host "Starting server on TCP=$Tcp UDP=$Udp..."
./gradlew :server:run --args "$Tcp $Udp"

