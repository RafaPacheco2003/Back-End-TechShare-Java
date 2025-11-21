# 🧪 TechShare - Script de Pruebas Unitarias con Docker
# Ejecuta todas las pruebas unitarias usando Java 17 en contenedor
# Sin necesidad de tener Java localmente instalado

Write-Host "🚀 TechShare - Ejecutando Pruebas Unitarias con Docker" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan

# Validar que Docker esté disponible
Write-Host "🔍 Verificando Docker..." -ForegroundColor Yellow
$dockerCheck = docker --version 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error: Docker no está instalado o no está en PATH" -ForegroundColor Red
    Write-Host "📥 Instala Docker Desktop desde: https://www.docker.com/products/docker-desktop" -ForegroundColor Yellow
    exit 1
}
Write-Host "✅ Docker está disponible" -ForegroundColor Green

# Navegar al directorio del proyecto
$projectPath = "g:\TechShare\Back-End-TechShare-Java"
Write-Host "📁 Cambiando al directorio del proyecto: $projectPath" -ForegroundColor Yellow

if (!(Test-Path $projectPath)) {
    Write-Host "❌ Error: No se encontró el directorio del proyecto" -ForegroundColor Red
    exit 1
}

Set-Location $projectPath

# Construir la imagen de pruebas si no existe o forzar reconstrucción
$forceBuild = $args -contains "--build" -or $args -contains "-b"
$imageName = "techshare-test"

$imageExists = docker images -q $imageName
if ($forceBuild -or !$imageExists) {
    Write-Host "🔨 Construyendo imagen de pruebas (Java 17)..." -ForegroundColor Yellow
    docker build -f Dockerfile.test -t $imageName .
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error al construir la imagen de Docker" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ Imagen construida exitosamente" -ForegroundColor Green
} else {
    Write-Host "✅ Usando imagen existente: $imageName" -ForegroundColor Green
}

# Crear directorio para reportes si no existe
$reportsDir = "test-reports"
if (!(Test-Path $reportsDir)) {
    New-Item -ItemType Directory -Path $reportsDir | Out-Null
    Write-Host "📁 Creado directorio de reportes: $reportsDir" -ForegroundColor Green
}

# Ejecutar las pruebas
Write-Host "🧪 Ejecutando pruebas unitarias..." -ForegroundColor Yellow
Write-Host "⏱️  Esto puede tomar algunos minutos..." -ForegroundColor Cyan

# Parámetros adicionales para Maven
$mavenArgs = "clean test"
if ($args -contains "--verbose" -or $args -contains "-v") {
    $mavenArgs += " -X"
    Write-Host "📝 Modo verbose activado" -ForegroundColor Cyan
}

if ($args -contains "--parallel" -or $args -contains "-p") {
    $mavenArgs += " -T 1C"
    Write-Host "⚡ Ejecución en paralelo activada" -ForegroundColor Cyan
}

# Ejecutar contenedor de pruebas
docker run --rm --name techshare-tests --memory="1024m" --cpus="2.0" -v "${PWD}:/app:ro" -v "${PWD}/${reportsDir}:/app/target/surefire-reports" -e SPRING_PROFILES_ACTIVE=test -e MAVEN_OPTS="-Xmx768m -XX:MaxMetaspaceSize=256m" $imageName sh -c "mvn $mavenArgs"

$testExitCode = $LASTEXITCODE

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan

# Mostrar resultados
if ($testExitCode -eq 0) {
    Write-Host "🎉 ¡PRUEBAS COMPLETADAS EXITOSAMENTE!" -ForegroundColor Green
    Write-Host "✅ Todas las pruebas unitarias pasaron" -ForegroundColor Green
    
    # Contar archivos de pruebas ejecutados
    $testFiles = Get-ChildItem -Path "src\test\java" -Filter "*Test.java" -Recurse -ErrorAction SilentlyContinue
    $testCount = $testFiles.Count
    Write-Host "📊 Archivos de prueba encontrados: $testCount" -ForegroundColor Cyan
    
    if ($testCount -gt 0) {
        Write-Host "📋 Archivos de prueba:" -ForegroundColor Cyan
        foreach ($file in $testFiles) {
            $relativePath = $file.FullName.Replace($projectPath, "").TrimStart('\')
            Write-Host "   ✓ $relativePath" -ForegroundColor White
        }
    }
} else {
    Write-Host "❌ ALGUNAS PRUEBAS FALLARON" -ForegroundColor Red
    Write-Host "🔍 Revisa los reportes en: $reportsDir" -ForegroundColor Yellow
    Write-Host "📋 Ejecuta con --verbose para más detalles" -ForegroundColor Yellow
}

# Información sobre reportes
Write-Host ""
Write-Host "📊 REPORTES DISPONIBLES:" -ForegroundColor Cyan
Write-Host "   📁 Reportes Surefire: $reportsDir" -ForegroundColor White
Write-Host "   📁 Logs de Maven: target/maven.log" -ForegroundColor White

# Información sobre comandos útiles
Write-Host ""
Write-Host "🔧 COMANDOS ÚTILES:" -ForegroundColor Cyan
Write-Host "   .\run-tests.ps1              # Ejecutar pruebas" -ForegroundColor White
Write-Host "   .\run-tests.ps1 --build      # Reconstruir imagen y ejecutar" -ForegroundColor White
Write-Host "   .\run-tests.ps1 --verbose    # Modo detallado" -ForegroundColor White
Write-Host "   .\run-tests.ps1 --parallel   # Ejecución paralela" -ForegroundColor White

Write-Host ""
Write-Host "🔗 Más información:" -ForegroundColor Cyan
Write-Host "   📖 README: README.md" -ForegroundColor White
Write-Host "   🐳 Docker Compose: docker-compose.test.yml" -ForegroundColor White

exit $testExitCode