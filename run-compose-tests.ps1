# 🚀 TechShare - Script de Pruebas con Docker Compose
# Ejecuta pruebas usando docker-compose para un entorno más completo

Write-Host "🚀 TechShare - Ejecutando Pruebas con Docker Compose" -ForegroundColor Green
Write-Host "=" * 60 -ForegroundColor Cyan

# Validar que Docker Compose esté disponible
Write-Host "🔍 Verificando Docker Compose..." -ForegroundColor Yellow
try {
    docker-compose --version | Out-Null
    Write-Host "✅ Docker Compose está disponible" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: Docker Compose no está instalado" -ForegroundColor Red
    exit 1
}

# Navegar al directorio del proyecto
$projectPath = "g:\TechShare\Back-End-TechShare-Java"
Set-Location $projectPath

# Limpiar contenedores anteriores si existen
Write-Host "🧹 Limpiando contenedores anteriores..." -ForegroundColor Yellow
docker-compose -f docker-compose.test.yml down --remove-orphans 2>$null

# Verificar si se debe reconstruir
$forceBuild = $args -contains "--build" -or $args -contains "-b"
$composeCommand = "docker-compose -f docker-compose.test.yml"

if ($forceBuild) {
    Write-Host "🔨 Reconstruyendo imágenes..." -ForegroundColor Yellow
    & $composeCommand build --no-cache
} else {
    Write-Host "🏗️ Construyendo imágenes si es necesario..." -ForegroundColor Yellow  
    & $composeCommand build
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error al construir las imágenes" -ForegroundColor Red
    exit 1
}

# Crear directorio para reportes
$reportsDir = "test-reports"
if (!(Test-Path $reportsDir)) {
    New-Item -ItemType Directory -Path $reportsDir | Out-Null
}

# Configurar comando de pruebas
$testCommand = "clean test"
if ($args -contains "--integration" -or $args -contains "-i") {
    $testCommand = "clean verify"
    Write-Host "🔗 Ejecutando pruebas de integración" -ForegroundColor Cyan
}

if ($args -contains "--verbose" -or $args -contains "-v") {
    $testCommand += " -X"
    Write-Host "📝 Modo verbose activado" -ForegroundColor Cyan
}

# Ejecutar las pruebas
Write-Host "🧪 Ejecutando pruebas..." -ForegroundColor Yellow

try {
    # Ejecutar el servicio de pruebas
    & $composeCommand run --rm test-runner mvn $testCommand
    $testExitCode = $LASTEXITCODE
    
    Write-Host ""
    Write-Host "=" * 60 -ForegroundColor Cyan
    
    if ($testExitCode -eq 0) {
        Write-Host "🎉 ¡PRUEBAS COMPLETADAS EXITOSAMENTE!" -ForegroundColor Green
        
        # Mostrar estadísticas si están disponibles
        if (Test-Path "target\surefire-reports") {
            $xmlReports = Get-ChildItem -Path "target\surefire-reports" -Filter "TEST-*.xml"
            if ($xmlReports.Count -gt 0) {
                Write-Host "📊 Reportes generados: $($xmlReports.Count) archivos" -ForegroundColor Cyan
            }
        }
        
    } else {
        Write-Host "❌ ALGUNAS PRUEBAS FALLARON (Código: $testExitCode)" -ForegroundColor Red
    }
    
} catch {
    Write-Host "❌ Error ejecutando las pruebas: $($_.Exception.Message)" -ForegroundColor Red
    $testExitCode = 1
    
} finally {
    # Limpiar contenedores
    Write-Host "🧹 Limpiando contenedores..." -ForegroundColor Yellow
    & $composeCommand down --remove-orphans 2>$null
}

# Mostrar información de reportes
Write-Host ""
Write-Host "📊 INFORMACIÓN DE REPORTES:" -ForegroundColor Cyan
if (Test-Path "target\surefire-reports") {
    Write-Host "   📁 Reportes Surefire: target\surefire-reports" -ForegroundColor White
    
    # Buscar archivo de resumen
    $summaryFile = Get-ChildItem -Path "target\surefire-reports" -Filter "*Summary*.txt" | Select-Object -First 1
    if ($summaryFile) {
        Write-Host "   📄 Resumen: $($summaryFile.Name)" -ForegroundColor White
    }
} else {
    Write-Host "   ⚠️ No se encontraron reportes" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🔧 COMANDOS DISPONIBLES:" -ForegroundColor Cyan
Write-Host "   .\run-compose-tests.ps1                 # Pruebas básicas" -ForegroundColor White
Write-Host "   .\run-compose-tests.ps1 --build         # Re-construir y probar" -ForegroundColor White
Write-Host "   .\run-compose-tests.ps1 --integration   # Pruebas de integración" -ForegroundColor White
Write-Host "   .\run-compose-tests.ps1 --verbose       # Modo detallado" -ForegroundColor White

Write-Host ""
Write-Host "📚 ARCHIVOS DE CONFIGURACIÓN:" -ForegroundColor Cyan
Write-Host "   🐳 docker-compose.test.yml   # Configuración de servicios" -ForegroundColor White
Write-Host "   🐳 Dockerfile.test           # Imagen de pruebas" -ForegroundColor White
Write-Host "   ⚙️ application-test.properties # Configuración de pruebas" -ForegroundColor White

exit $testExitCode