package com.arm.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("DOFBOT API Document")
                .version("v0.0.1")
                .description("DOFBOT  API 명세서");
                
        return new OpenAPI()
                .info(info);
    }

}