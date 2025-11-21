# TechShare - Script de Pruebas Unitarias con Docker

param(
    [switch]$Build,
    [switch]$Verbose,
    [switch]$Parallel
)

Write-Host "🚀 TechShare - Ejecutando Pruebas Unitarias con Docker" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan

# Verificar Docker
Write-Host "🔍 Verificando Docker..." -ForegroundColor Yellow
docker --version 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error: Docker no está disponible" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Docker está disponible" -ForegroundColor Green

# Ir al directorio del proyecto
$projectPath = "g:\TechShare\Back-End-TechShare-Java"
Set-Location $projectPath

# Construir imagen si es necesario
$imageName = "techshare-test"
$imageExists = docker images -q $imageName

if ($Build -or !$imageExists) {
    Write-Host "🔨 Construyendo imagen de pruebas..." -ForegroundColor Yellow
    docker build -f Dockerfile.test -t $imageName .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error al construir imagen" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ Imagen construida" -ForegroundColor Green
} else {
    Write-Host "✅ Usando imagen existente" -ForegroundColor Green
}

# Crear directorio de reportes
$reportsDir = "test-reports"
if (!(Test-Path $reportsDir)) {
    New-Item -ItemType Directory -Path $reportsDir | Out-Null
}

# Preparar comando Maven
$mavenArgs = "clean test"
if ($Verbose) {
    $mavenArgs += " -X"
}
if ($Parallel) {
    $mavenArgs += " -T 1C"
}

# Ejecutar pruebas
Write-Host "🧪 Ejecutando pruebas..." -ForegroundColor Yellow
docker run --rm --name techshare-tests -v "${PWD}:/app:ro" -v "${PWD}/${reportsDir}:/app/target/surefire-reports" -e SPRING_PROFILES_ACTIVE=test $imageName sh -c "mvn $mavenArgs"

$testResult = $LASTEXITCODE

# Mostrar resultados
Write-Host "============================================================" -ForegroundColor Cyan
if ($testResult -eq 0) {
    Write-Host "🎉 PRUEBAS COMPLETADAS EXITOSAMENTE" -ForegroundColor Green
} else {
    Write-Host "❌ ALGUNAS PRUEBAS FALLARON" -ForegroundColor Red
}

Write-Host "📊 Reportes disponibles en: $reportsDir" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

exit $testResult