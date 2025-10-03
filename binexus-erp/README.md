# Binexus ERP

Base monolítica construida con Spring Boot 3, Thymeleaf y PostgreSQL.

## Requisitos

- Java 21
- Maven 3.9+
- PostgreSQL 14 o superior

## Variables de entorno

Configura las credenciales de base de datos antes de ejecutar la aplicación:

- `DB_URL`
- `DB_USER`
- `DB_PASS`

## Comandos

```bash
mvn clean package
java -jar target/binexus-erp-*.jar
```
