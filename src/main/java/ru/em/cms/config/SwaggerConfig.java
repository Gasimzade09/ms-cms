package ru.em.cms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Card management system (CMS)")
                        .version("1.0")
                        .description("API documentation for the application"))
                .components(new Components()
                        .addSecuritySchemes("JwtToken",
                                new SecurityScheme()
                                        .name("JwtToken")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT") // Опционально, для документации
                        )
                )
                // 2. Применяем эту схему глобально
                .addSecurityItem(new SecurityRequirement().addList("JwtToken"));
    }
}
