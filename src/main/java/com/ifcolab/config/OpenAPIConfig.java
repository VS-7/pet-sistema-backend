package com.ifcolab.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PET Sistema API")
                        .description("Sistema de gerenciamento do Plano de Estudo Tutorial (PET)")
                        .version("2.3.0"));
    }
} 