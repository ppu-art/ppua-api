package kh.edu.ppua.api.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PPUA RESTFull API")
                        .description("RESTFull API for Phnom Penh University of the Arts")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("chivonchhai@ppua.edu.kh")))
                .externalDocs(new ExternalDocumentation()
                        .description("API Documentation")
                        .url("https://ppua.edu.kh/"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.ppua.edu.kh")
                                .description("Production Server")
                ));
    }

    @Bean
    public GroupedOpenApi devApi() {
        return GroupedOpenApi.builder()
                .group("dev-apis")
                .pathsToMatch("/api/**") // Scan all paths starting with /api
                .packagesToScan("kh.edu.ppua.api.controller") // Scan specific package
                .build();
    }

}