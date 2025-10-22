Archivos movidos desde `src/main/resources/db/migration` para archivado.

No poner aquí scripts que Flyway deba ejecutar en runtime. Este directorio es solo para registro y rollback manual.

Si necesitas restaurar una migración, muévela de vuelta a `src/main/resources/db/migration` y reconstruye el JAR.

Estado actual:

- `V9__add_birth_date_if_missing.sql` fue movido a este directorio para evitar que Flyway lo detecte y ejecute automáticamente al iniciar la aplicación.
- Si necesitas aplicar esa alteración en una base de datos controlada, ejecuta el script manualmente contra la base de datos objetivo o muévelo de nuevo a `src/main/resources/db/migration` y reconstruye el JAR.

Por favor, tenga en cuenta los riesgos de remover migraciones: si la columna `birth_date` es requerida por la versión actual del código en producción, asegúrate de aplicar la migración manualmente antes de retirar el script del classpath.
