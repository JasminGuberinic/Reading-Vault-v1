# Reading Vault

Reading Vault is a server-side application built with Kotlin, using the Dropwizard framework, Exposed ORM, and an H2 database. The project leverages Guice for dependency injection, Flyway for database migrations, and Swagger for automatic REST API documentation.

## Technologies Used

- **Kotlin** (JVM 21)
- **Dropwizard** (REST API framework)
- **Exposed ORM** (database access)
- **H2** (in-memory database for development and testing)
- **Flyway** (database migrations)
- **Google Guice** (dependency injection)
- **Swagger/OpenAPI** (API documentation)
- **JUnit 5, Mockito** (testing)
- **Jackson** (JSON serialization)
- **JWT authentication** (JSON Web Token)
- **Logback/SLF4J** (logging)

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone <repo-url>
   cd Reading-Vault-v1

**Build and run the application:**
-   Using Gradle wrapper:

./gradlew build
./gradlew run

-    Or run the JAR directly:

java -jar build/libs/Reading-Vault-v1-all.jar