# Binexus ERP

Binexus ERP es una aplicación administrativa construida con Spring Boot 3.5, Java 21 y PostgreSQL. Esta primera entrega incluye los módulos base del sistema: autenticación, gestión de usuarios, roles y privilegios, organizaciones y locales.

## Requisitos

- Java 21
- Maven 3.9+
- PostgreSQL 14 o superior

## Configuración

1. Crea la base de datos y el usuario en PostgreSQL:

```sql
CREATE USER binexus WITH PASSWORD 'cambialo';
CREATE DATABASE binexus_erp OWNER binexus;
GRANT ALL PRIVILEGES ON DATABASE binexus_erp TO binexus;
```

2. Exporta las variables de entorno esperadas por la aplicación:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/binexus_erp"
export DB_USER="binexus"
export DB_PASS="cambialo"
```

Flyway ejecutará automáticamente la migración inicial (`V1__init.sql`) que crea el esquema completo (usuarios, roles, privilegios, organizaciones y locales) y carga los datos de ejemplo requeridos.

### Credenciales iniciales

| Usuario               | Contraseña |
|-----------------------|------------|
| `admin@binexus.com`   | `Admin123!`|

> **Importante:** cambia la contraseña del administrador y rota las credenciales de base de datos en cuanto despliegues el proyecto.

## Ejecución

Compila y ejecuta el proyecto con Maven:

```bash
mvn clean package
java -jar target/binexus-erp-0.0.1-SNAPSHOT.jar
```

La aplicación quedará disponible en `http://localhost:8080`.

### Ejecutar con base de datos H2 en memoria

Si prefieres evitar instalar PostgreSQL para realizar pruebas locales rápidas, puedes iniciar la aplicación con el perfil `h2`,
el cual levanta automáticamente una base de datos en memoria y ejecuta las migraciones Flyway sobre ella:

```bash
SPRING_PROFILES_ACTIVE=h2 mvn spring-boot:run
```

Al utilizar este perfil también se habilita la consola web de H2 en `http://localhost:8080/h2-console`, utilizando la URL `jdbc:h2:mem:binexus` y usuario `sa` (sin contraseña).

## Funcionalidades incluidas

- Autenticación vía formulario (`/login`) con Spring Security y BCrypt.
- Control de acceso: `/dashboard` requiere autenticación, `/admin/**` requiere `ROLE_ADMIN`.
- Gestión de usuarios con asignación de roles, validación de correo único y estados.
- Gestión de organizaciones y locales con formularios Thymeleaf responsivos.
- Layout reutilizable con fragmentos, modo claro/oscuro, toasts y Chart.js en el dashboard.
- Migraciones Flyway y configuración productiva (`ddl-auto=validate`).
- Módulo de cierre de caja con historial, aperturas controladas y resumen de ventas.
- Solicitudes de autorizaciones remotas con aprobación por usuarios administradores y seguimiento de estados.
- Auditoría automática (`created_at` / `updated_at`) para las principales entidades del dominio.

### Guía rápida: cierre de caja y autorizaciones

1. **Apertura de caja**: navega a `Caja → Abrir` e ingresa el monto inicial. El sistema valida que no exista otra caja abierta para el usuario en curso.
2. **Registro de ventas**: mientras exista una caja abierta, las ventas quedan asociadas al cierre activo; si no hay caja abierta, el servicio de ventas bloquea la operación.
3. **Cierre de caja**: desde `Caja → Cerrar` revisa el resumen de ventas y confirma el cierre para almacenar la fecha y calcular las diferencias.
4. **Historial**: consulta `Caja → Historial` para revisar aperturas y cierres previos ordenados cronológicamente.
5. **Autorizaciones remotas**: ante acciones restringidas (anular venta, modificar precios, etc.) solicita una autorización y comparte el código generado con un supervisor. Los administradores aprueban o rechazan desde `Autorizaciones`, manteniendo un registro auditable.

## Pruebas

Ejecuta las pruebas unitarias y de integración con:

```bash
mvn test
```

Las pruebas actuales validan las reglas de negocio del cierre de caja y el flujo principal del controlador de autorizaciones remotas.

## Buenas prácticas de seguridad

- Mantén las variables `DB_URL`, `DB_USER` y `DB_PASS` fuera del control de versiones.
- Reemplaza las contraseñas por valores seguros en cada ambiente.
- Configura HTTPS y rotación periódica de claves para entornos productivos.
