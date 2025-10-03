# Script para ejecutar pruebas con Java 17
# Guarda este archivo como: run-tests-java17.ps1

Write-Host "🚀 TechShare - Ejecutor de Pruebas con Java 17" -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Cyan

# Ruta donde debería estar Java 17
$javaPath = "C:\Java\jdk-17"

# Verificar si existe Java 17
if (-Not (Test-Path $javaPath)) {
    Write-Host "`n❌ Java 17 no encontrado en: $javaPath" -ForegroundColor Red
    Write-Host "`n📥 Para continuar:" -ForegroundColor Yellow
    Write-Host "   1. Descarga Java 17 de: https://adoptium.net/temurin/releases/?version=17" -ForegroundColor White
    Write-Host "   2. Selecciona: JDK 17 (LTS) > Windows x64 > .zip" -ForegroundColor White
    Write-Host "   3. Extrae el contenido a: $javaPath" -ForegroundColor White
    Write-Host "   4. Vuelve a ejecutar este script" -ForegroundColor White
    
    # Preguntar si quiere usar otra ruta
    Write-Host "`n¿Tienes Java 17 en otra ubicación? (S/N): " -NoNewline -ForegroundColor Yellow
    $respuesta = Read-Host
    
    if ($respuesta -eq "S" -or $respuesta -eq "s") {
        Write-Host "Ingresa la ruta completa a Java 17: " -NoNewline -ForegroundColor Yellow
        $javaPath = Read-Host
        
        if (-Not (Test-Path $javaPath)) {
            Write-Host "❌ La ruta no existe. Abortando." -ForegroundColor Red
            exit 1
        }
    } else {
        exit 1
    }
}

# Configurar Java 17 temporalmente (solo para este script)
Write-Host "`n⚙️ Configurando Java 17..." -ForegroundColor Green
$env:JAVA_HOME = $javaPath
$env:PATH = "$javaPath\bin;$env:PATH"

# Verificar versión
Write-Host "`n🔍 Verificando versión de Java:" -ForegroundColor Cyan
java -version

# Cambiar al directorio del proyecto
Write-Host "`n📂 Cambiando al directorio del proyecto..." -ForegroundColor Cyan
Set-Location "G:\TechShare\Back-End-TechShare-Java"

# Limpiar builds anteriores
Write-Host "`n🧹 Limpiando builds anteriores..." -ForegroundColor Cyan
.\mvnw.cmd clean

# Ejecutar pruebas
Write-Host "`n🧪 Ejecutando pruebas unitarias de MaterialsService..." -ForegroundColor Green
Write-Host "=" * 60 -ForegroundColor Green
.\mvnw.cmd test -Dtest=MaterialsServiceImplTest

# Capturar el código de salida
$exitCode = $LASTEXITCODE

# Mostrar resumen
Write-Host "`n" + "=" * 60 -ForegroundColor Cyan
if ($exitCode -eq 0) {
    Write-Host "✅ ¡PRUEBAS EXITOSAS!" -ForegroundColor Green
    Write-Host "   Todas las pruebas pasaron correctamente." -ForegroundColor Green
} else {
    Write-Host "❌ PRUEBAS FALLIDAS" -ForegroundColor Red
    Write-Host "   Revisa los errores arriba." -ForegroundColor Red
}

# Mostrar ubicación de reportes
Write-Host "`n📊 Reportes generados en:" -ForegroundColor Cyan
Write-Host "   $PWD\target\surefire-reports\" -ForegroundColor White

# Preguntar si desea ver el reporte
Write-Host "`n¿Deseas abrir la carpeta de reportes? (S/N): " -NoNewline -ForegroundColor Yellow
$verReporte = Read-Host

if ($verReporte -eq "S" -or $verReporte -eq "s") {
    if (Test-Path ".\target\surefire-reports") {
        Invoke-Item ".\target\surefire-reports"
    } else {
        Write-Host "⚠️ La carpeta de reportes aún no existe." -ForegroundColor Yellow
    }
}

Write-Host "`n✨ Script finalizado." -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Cyan

# Salir con el mismo código que Maven
exit $exitCode
