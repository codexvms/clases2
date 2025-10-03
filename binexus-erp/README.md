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

## Funcionalidades incluidas

- Autenticación vía formulario (`/login`) con Spring Security y BCrypt.
- Control de acceso: `/dashboard` requiere autenticación, `/admin/**` requiere `ROLE_ADMIN`.
- Gestión de usuarios con asignación de roles, validación de correo único y estados.
- Gestión de organizaciones y locales con formularios Thymeleaf responsivos.
- Layout reutilizable con fragmentos, modo claro/oscuro, toasts y Chart.js en el dashboard.
- Migraciones Flyway y configuración productiva (`ddl-auto=validate`).

## Pruebas

Ejecuta las pruebas unitarias y de integración con:

```bash
mvn test
```

## Buenas prácticas de seguridad

- Mantén las variables `DB_URL`, `DB_USER` y `DB_PASS` fuera del control de versiones.
- Reemplaza las contraseñas por valores seguros en cada ambiente.
- Configura HTTPS y rotación periódica de claves para entornos productivos.
