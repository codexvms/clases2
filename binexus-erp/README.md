# Binexus ERP

Aplicación ERP monolítica construida con Spring Boot 3, PostgreSQL y Thymeleaf.

## Requisitos

- Java 21
- Maven 3.9+
- PostgreSQL 14+ (o compatible)

## Configuración de base de datos

1. Crear usuario y base de datos en PostgreSQL:

```sql
CREATE USER binexus WITH PASSWORD 'cambialo';
CREATE DATABASE binexus_erp OWNER binexus;
GRANT ALL PRIVILEGES ON DATABASE binexus_erp TO binexus;
```

2. Configurar variables de entorno antes de ejecutar la aplicación:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/binexus_erp"
export DB_USER="binexus"
export DB_PASS="cambialo"
export JWT_SECRET="cambia-esta-clave-segura"
```

## Ejecución

### Desarrollo

```bash
mvn spring-boot:run
```

### Compilación

```bash
mvn clean package
```

### Ejecución del JAR

```bash
java -jar target/binexus-erp-0.0.1-SNAPSHOT.jar
```

La aplicación quedará disponible en `http://localhost:8080`.

## Perfiles

- `default`: configuración productiva con `ddl-auto=validate`.
- `dev`: habilita logs detallados y CORS configurable. Activar con `-Dspring.profiles.active=dev` o variable `SPRING_PROFILES_ACTIVE=dev`.

## Migraciones y datos iniciales

Flyway gestiona el esquema mediante `src/main/resources/db/migration`. La migración inicial crea:

- Roles `ROLE_ADMIN` y `ROLE_USER`.
- Usuario administrador demo: `admin@binexus.com` con contraseña `Admin123!` (hash bcrypt). Cambiar en producción.

## Credenciales demo

- Usuario: `admin@binexus.com`
- Contraseña: `Admin123!`

## Seguridad

- Autenticación con formulario en `/login`.
- Protección de rutas: `/dashboard` requiere autenticación; `/admin/**` requiere `ROLE_ADMIN`.
- Endpoint API opcional `POST /api/auth/login` que entrega JWT firmado con el secreto configurado.
- Ajustar `JWT_SECRET`, habilitar HTTPS y rotar claves en entornos reales.

## Variables de entorno

| Variable       | Descripción                                      |
|----------------|--------------------------------------------------|
| `DB_URL`       | URL JDBC hacia PostgreSQL.                       |
| `DB_USER`      | Usuario de base de datos.                        |
| `DB_PASS`      | Contraseña del usuario de base de datos.         |
| `JWT_SECRET`   | Clave secreta para firmar tokens JWT.            |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos en perfil `dev`.    |

## Docker (opcional)

Ejemplo mínimo para levantar PostgreSQL y la app empaquetada:

`Dockerfile`

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/binexus-erp-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

`docker-compose.yml`

```yaml
version: '3.8'
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: binexus_erp
      POSTGRES_USER: binexus
      POSTGRES_PASSWORD: cambialo
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
  app:
    build: .
    depends_on:
      - db
    environment:
      DB_URL: jdbc:postgresql://db:5432/binexus_erp
      DB_USER: binexus
      DB_PASS: cambialo
      JWT_SECRET: cambia-esta-clave-segura
    ports:
      - "8080:8080"
volumes:
  postgres-data:
```

> **Nota:** No almacenar contraseñas reales en archivos de configuración. Usa gestores de secretos y HTTPS en producción.

## Pruebas

```bash
mvn test
```

Las pruebas unitarias verifican reglas de negocio del servicio de usuarios y la configuración de seguridad del controlador de autenticación.
