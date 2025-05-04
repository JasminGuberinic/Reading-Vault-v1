plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "org.virtualcode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


application {
    mainClass.set("org.virtualcode.ReadingVaultApplication")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("io.dropwizard:dropwizard-core:2.1.1") // Koristite najnoviju stabilnu verziju
    implementation("io.dropwizard:dropwizard-assets:2.1.1") // Ako želite da servirate statičke fajlove
    implementation("io.dropwizard:dropwizard-jersey:2.1.1") // Za Jersey integraciju (REST)
    implementation("io.dropwizard:dropwizard-jackson:2.1.1") // Za Jackson podršku (JSON)

    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.49.0") // Najnovija verzija
    implementation("org.jetbrains.exposed:exposed-dao:0.49.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.49.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.49.0")

    // H2 baza podataka (za primer i razvoj) - zamenite sa vašom bazom
    implementation("com.h2database:h2:2.2.224")

    // Google Guice za Dependency Injection
    implementation("com.google.inject:guice:7.0.0")
    implementation("com.google.inject.extensions:guice-servlet:7.0.0") // Ako koristite servlete (Jersey jeste servlet-based)
    implementation("com.google.inject.extensions:guice-multibindings:4.2.3") // Korisno za lakše registrovanje više instanci

    // Flyway za Migracije
    implementation("org.flywaydb:flyway-core:10.15.2") // Najnovija verzija

//    // OpenAPI (Swagger) - Dropwizard integracija
//    implementation("io.dropwizard.modules:dropwizard-openapi:2.1.1") // Ili najnovija verzija

    implementation("com.zaxxer:HikariCP:5.1.0") // Proverite najnoviju stabilnu verziju na Maven Central

    // Jackson Kotlin Module
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")

    // Swagger/OpenAPI
    implementation("io.swagger.core.v3:swagger-core:2.2.15")
    implementation("io.swagger.core.v3:swagger-jaxrs2:2.2.15")
    implementation("io.swagger.core.v3:swagger-integration:2.2.15")
    implementation("org.webjars:swagger-ui:5.1.0")

    // Javax Validation
    implementation("javax.validation:validation-api:2.0.1.Final")

    // Eksplicitne SLF4J zavisnosti
    implementation("org.slf4j:slf4j-api:1.7.36") // Verzija koju koristi Dropwizard 2.1.1
    implementation("ch.qos.logback:logback-classic:1.2.11") // Kompatibilno sa SLF4J 1.7.x
    implementation("ch.qos.logback:logback-core:1.2.11")

    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.22")
    testImplementation("io.dropwizard:dropwizard-testing:4.0.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")

    implementation("io.dropwizard:dropwizard-auth:2.1.1")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.mindrot:jbcrypt:0.4")

}

configurations.all {
    resolutionStrategy {
        // Forsiraj konzistentnu verziju SLF4J-a
        force("org.slf4j:slf4j-api:1.7.36")
        force("ch.qos.logback:logback-classic:1.2.11")
        force("ch.qos.logback:logback-core:1.2.11")
    }

    // Isključi konfliktne implementacije
    exclude(group = "org.slf4j", module = "slf4j-simple")
    exclude(group = "org.slf4j", module = "slf4j-log4j12")
    exclude(group = "org.slf4j", module = "slf4j-jdk14")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.shadowJar {
    mergeServiceFiles()
    manifest {
        attributes(mapOf("Main-Class" to application.mainClass.get()))
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}