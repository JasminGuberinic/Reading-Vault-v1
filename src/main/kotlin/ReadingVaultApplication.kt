package org.virtualcode

import com.google.inject.Guice
import com.google.inject.Injector
import io.dropwizard.Application
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.AuthValueFactoryProvider
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.ResourceConfigurationSourceProvider
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.flywaydb.core.Flyway
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature
import org.h2.tools.Server
import org.virtualcode.api.resource.*
import org.virtualcode.auth.JwtAuthenticator
import org.virtualcode.auth.RoleAuthorizer
import org.virtualcode.auth.UserPrincipal
import org.virtualcode.config.DateTimeParamConverterProvider
import org.virtualcode.config.ReadingVaultConfiguration
import org.virtualcode.config.SwaggerBundle
import org.virtualcode.config.module.ReadingVaultModule
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter

class ReadingVaultApplication : Application<ReadingVaultConfiguration>() {

    private lateinit var injector: Injector
    private var h2Server: Server? = null

    override fun getName(): String {
        return "reading-vault"
    }

    override fun initialize(bootstrap: Bootstrap<ReadingVaultConfiguration>) {
        // Podrška za environment variables u konfiguraciji
        bootstrap.configurationSourceProvider = SubstitutingSourceProvider(
            ResourceConfigurationSourceProvider(),
            EnvironmentVariableSubstitutor(false)
        )

        // Dodavanje Swagger bundle-a
        bootstrap.addBundle(SwaggerBundle())
    }

    override fun run(configuration: ReadingVaultConfiguration, environment: Environment) {
        // Kreiranje Guice Injector-a sa konfiguracijom
        injector = Guice.createInjector(ReadingVaultModule(configuration))

        environment.jersey().register(
            AuthDynamicFeature(
                OAuthCredentialAuthFilter.Builder<UserPrincipal>()
                .setAuthenticator(injector.getInstance(JwtAuthenticator::class.java))
                .setAuthorizer(RoleAuthorizer())
                .setPrefix("Bearer")
                .buildAuthFilter()
        )
        )

        environment.jersey().register(RolesAllowedDynamicFeature::class.java)
        environment.jersey().register(AuthValueFactoryProvider.Binder(UserPrincipal::class.java))

        // Pokretanje Flyway migracija
        runFlywayMigrations(configuration)

        environment.jersey().register(DateTimeParamConverterProvider::class.java)
        // Registracija resursa pomoću Guice-a
        environment.jersey().register(injector.getInstance(BookResource::class.java))
        environment.jersey().register(injector.getInstance(BookNoteResource::class.java))
        environment.jersey().register(injector.getInstance(BookLendingResource::class.java))
        environment.jersey().register(injector.getInstance(ReadingProgressResource::class.java))
        environment.jersey().register(injector.getInstance(GlobalBookNoteResource::class.java))
        environment.jersey().register(injector.getInstance(GlobalBookLendingResource::class.java))
        environment.jersey().register(injector.getInstance(BookOperationResource::class.java))
        environment.jersey().register(injector.getInstance(UserResource::class.java))

        // Registracija resursa pomoću Guice-a
        environment.jersey().register(injector.getInstance(BookResource::class.java))

        // Omogući H2 konzolu za lakši pristup bazi tokom razvoja
        if (configuration.enableH2Console) {
            h2Server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start()
            println("H2 Console dostupna na: http://localhost:8082")
        }

        // Registracija health check-ova
        // environment.healthChecks().register("database", DatabaseHealthCheck(...))

        // Registracija shutdown hook-a za čišćenje resursa
        environment.lifecycle().manage(object : io.dropwizard.lifecycle.Managed {
            override fun start() {}

            override fun stop() {
                h2Server?.stop()
            }
        })
    }

    private fun runFlywayMigrations(configuration: ReadingVaultConfiguration) {
        val flyway = Flyway.configure()
            .dataSource(configuration.databaseUrl, configuration.databaseUser, configuration.databasePassword)
            .cleanDisabled(false) // Omogućava clean operaciju
            .cleanOnValidationError(true) // Automatski čisti bazu ako ima problema sa validacijom
            .baselineOnMigrate(true) // Kreira baseline ako je baza prazna
            .load()
        flyway.clean()
        flyway.migrate()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Ako nema argumenata ili samo "server", koristi default konfiguraciju iz root direktorija
            if (args.isEmpty() || (args.size == 1 && args[0] == "server")) {
                ReadingVaultApplication().run("server", "config.yml")
            } else {
                ReadingVaultApplication().run(*args)
            }
        }
    }
}