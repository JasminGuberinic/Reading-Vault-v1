package org.virtualcode.config

import io.dropwizard.ConfiguredBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.assets.AssetsBundle
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource
import io.swagger.v3.oas.integration.SwaggerConfiguration
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.Components
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.net.URI
import io.dropwizard.Configuration

class SwaggerBundle : ConfiguredBundle<Configuration> {

    override fun initialize(bootstrap: Bootstrap<*>) {
        // Dodaj AssetsBundle za serviranje Swagger UI resursa
        bootstrap.addBundle(AssetsBundle("/swagger-ui", "/swagger-ui", "index.html"))
    }

    override fun run(configuration: Configuration, environment: Environment) {
        // Konfiguracija Swagger-a
        val openAPI = OpenAPI()
        openAPI.info = Info()
            .title("Reading Vault API")
            .description("API za praćenje pročitanih knjiga")
            .version("1.0.0")
            .contact(
                Contact()
                    .name("Virtual Code")
                    .email("info@virtualcode.org")
            )

        openAPI.addServersItem(
            Server()
                .url("/")
                .description("Reading Vault API Server")
        )

        // Dodajemo Bearer token security scheme
        val securityScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Enter JWT token")

        val components = Components()
            .addSecuritySchemes("bearerAuth", securityScheme)

        openAPI.components(components)

        // Dodajemo globalni security requirement
        openAPI.addSecurityItem(
            SecurityRequirement().addList("bearerAuth")
        )

        // Konfiguracija Swagger-a
        val swaggerConfig = SwaggerConfiguration()
            .openAPI(openAPI)
            .prettyPrint(true)
            .resourcePackages(setOf("org.virtualcode.api.resource"))

        // Registracija OpenAPI resursa
        val openApiResource = OpenApiResource()
        openApiResource.openApiConfiguration = swaggerConfig
        environment.jersey().register(openApiResource)

        // Dodaj redirekciju sa /swagger na /swagger-ui/index.html
        environment.jersey().register(SwaggerRedirectResource())
    }

    @Path("/swagger")
    @Produces(MediaType.TEXT_HTML)
    class SwaggerRedirectResource {
        @GET
        fun redirectToSwaggerUI(): Response {
            return Response.temporaryRedirect(
                URI.create("/swagger-ui/index.html")
            ).build()
        }
    }
}