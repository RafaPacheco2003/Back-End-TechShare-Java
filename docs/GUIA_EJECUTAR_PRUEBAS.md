# 🎯 Cómo Ejecutar y Verificar las Pruebas Unitarias

## Problema Actual

Tu sistema tiene **Java 23**, pero el proyecto necesita **Java 17**. Además, Docker Desktop está experimentando problemas.

## ✅ Solución 1: Usar SDKMAN (La Más Fácil - RECOMENDADA)

SDKMAN te permite tener múltiples versiones de Java y cambiar entre ellas fácilmente.

### En Windows (PowerShell):

1. **Instala SDKMAN para Windows**:
   ```powershell
   # Descarga e instala desde: https://sdkman.io/install
   # O usa Chocolatey:
   choco install sdkman
   ```

2. **Instala Java 17**:
   ```bash
   sdk install java 17.0.9-tem
   ```

3. **Úsalo temporalmente** (sin cambiar tu Java 23):
   ```bash
   sdk use java 17.0.9-tem
   ```

4. **Ejecuta las pruebas**:
   ```bash
   cd G:\TechShare\Back-End-TechShare-Java
   .\mvnw.cmd clean test
   ```

## ✅ Solución 2: Maven con Java 17 Portable (Sin Instalación)

Puedes descargar Java 17 portable y usarlo solo para este proyecto sin afectar tu Java 23.

### Pasos:

1. **Descarga Java 17 Portable**:
   - Ir a: https://adoptium.net/temurin/releases/?version=17
   - Descargar: `.zip` para Windows x64
   - Extraer a: `C:\Java\jdk-17` (o donde prefieras)

2. **Crear script para ejecutar pruebas con Java 17**:
   ```powershell
   # Crear archivo: run-tests.ps1
   $env:JAVA_HOME = "C:\Java\jdk-17"
   $env:PATH = "C:\Java\jdk-17\bin;$env:PATH"
   
   cd G:\TechShare\Back-End-TechShare-Java
   .\mvnw.cmd clean test
   ```

3. **Ejecutar**:
   ```powershell
   .\run-tests.ps1
   ```

## ✅ Solución 3: Configurar Maven para Usar Java 17 Específicamente

Si tienes Java 17 instalado en algún lugar, puedes decirle a Maven que lo use:

1. **Crear archivo `.mvn/jvm.config`** con la ruta a Java 17:
   ```
   --java-home=C:\Path\To\Java17
   ```

2. **O usar variable de entorno temporal**:
   ```powershell
   $env:JAVA_HOME = "C:\Path\To\Java17"
   .\mvnw.cmd clean test
   ```

## 🐳 Solución 4: Arreglar Docker y Usar Contenedor de Testing

### Reinicia Docker Desktop:

1. **Cierra Docker Desktop completamente**
2. **Abre el Administrador de Tareas** (Ctrl+Shift+Esc)
3. **Termina todos los procesos de Docker**
4. **Inicia Docker Desktop nuevamente**
5. **Espera a que esté completamente iniciado** (ícono verde)

### Luego ejecuta:

```powershell
cd G:\TechShare

# Ejecutar pruebas en contenedor temporal
docker run --rm `
  -v "${PWD}\Back-End-TechShare-Java:/app" `
  -w /app `
  eclipse-temurin:17-jdk `
  ./mvnw test -Dtest=MaterialsServiceImplTest
```

### O usa el docker-compose.test.yml que creé:

```powershell
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

## 📊 Cómo Verificar los Resultados

Una vez que ejecutes las pruebas con cualquiera de estos métodos, verás:

### ✅ Resultado Exitoso:
```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### ❌ Resultado con Fallos:
```
[ERROR] Tests run: 15, Failures: 3, Errors: 2, Skipped: 0
[INFO] BUILD FAILURE
```

### 📄 Reporte Detallado:

Los resultados estarán en:
```
Back-End-TechShare-Java/target/surefire-reports/
```

Archivos importantes:
- `MaterialsServiceImplTest.txt` - Resumen de texto
- `TEST-*.xml` - Reporte XML detallado

## 🎓 Mi Recomendación Personal

**Usa la Solución 2** (Java 17 Portable):

1. Es rápido (5 minutos)
2. No afecta tu Java 23
3. No requiere instalación
4. Funciona al instante

### Script Completo (Copia y Pega):

```powershell
# run-tests-java17.ps1

# 1. Descarga Java 17 si no lo tienes
$javaPath = "C:\Java\jdk-17"

if (-Not (Test-Path $javaPath)) {
    Write-Host "⚠️ Java 17 no encontrado en $javaPath"
    Write-Host "📥 Descárgalo de: https://adoptium.net/temurin/releases/?version=17"
    Write-Host "📦 Extrae el ZIP a: $javaPath"
    exit 1
}

# 2. Configura Java 17 temporalmente
$env:JAVA_HOME = $javaPath
$env:PATH = "$javaPath\bin;$env:PATH"

# 3. Verifica la versión
Write-Host "🔍 Verificando Java..."
java -version

# 4. Ejecuta las pruebas
Write-Host "`n🧪 Ejecutando pruebas unitarias..."
cd G:\TechShare\Back-End-TechShare-Java
.\mvnw.cmd clean test

# 5. Muestra resultados
Write-Host "`n📊 Resultados en: target\surefire-reports\"
```

## 📈 Próximos Pasos Después de Ejecutar las Pruebas

Una vez que veas que las **15 pruebas pasan exitosamente**, continuaremos con:

1. ✅ Crear pruebas para `BorrowService` (gestión de préstamos)
2. ✅ Crear pruebas para `UserService` (gestión de usuarios)
3. ✅ Crear pruebas para `CategoriesService`
4. ✅ Crear pruebas para `SubCategoriesService`
5. ✅ Crear pruebas de integración para Controllers
6. ✅ Implementar mejoras de seguridad
7. ✅ Agregar paginación
8. ✅ Implementar Spring Actuator para monitoreo

**Objetivo**: Llevar el backend a **10/10** como solicitaste.

## 💡 Tips

- **No necesitas cambiar tu Java 23** para tus otros proyectos
- **Java 17 portable** solo se usa cuando ejecutas las pruebas de este proyecto
- **Las pruebas ya están creadas** y listas para ejecutarse
- **Solo necesitas un entorno con Java 17** para verificar que funcionen

---

¿Cuál solución prefieres intentar primero? Te puedo guiar paso a paso en cualquiera de ellas.
