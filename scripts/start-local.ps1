# script to start the app locally (Windows PowerShell)
# - ensures the DB exists (requires mysql client or Docker)
# - builds the project
# - runs the jar with local profile

Param(
    [switch]$UseDocker,
    [string]$DbName = 'matchpoint',
    [string]$DbUser = 'root',
    [string]$DbPass = '12345',
    [int]$ServerPort = 8080
)

function Ensure-Database {
    if ($UseDocker) {
        Write-Host "Starting MySQL container for local dev..."
        docker run --name matchpoint-mysql -e MYSQL_ROOT_PASSWORD=$DbPass -e MYSQL_DATABASE=$DbName -p 3306:3306 -d mysql:8.1
        Start-Sleep -Seconds 8
        return
    }

    # Try mysql client
    try {
        mysql -u $DbUser -p$DbPass -e "CREATE DATABASE IF NOT EXISTS $DbName CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
        Write-Host "Database ensured: $DbName"
    } catch {
        Write-Warning "Could not ensure DB via mysql client. Install MySQL client or run script with -UseDocker to start a local MySQL container."
    }
}

# Build
Write-Host "Building project..."
./mvnw.cmd -DskipTests package

# Ensure DB
Ensure-Database

# Run
Write-Host "Starting application with profile 'local' (port $ServerPort)..."
$env:LOCAL_DB_USERNAME = $DbUser
$env:LOCAL_DB_PASSWORD = $DbPass
$env:LOCAL_DB_URL = "jdbc:mysql://localhost:3306/${DbName}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
& java "-Dspring.profiles.active=local" "-Dserver.port=$ServerPort" -jar "target/matchpoint-0.0.1-SNAPSHOT.jar"
