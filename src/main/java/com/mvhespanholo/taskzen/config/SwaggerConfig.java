package com.mvhespanholo.taskzen.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI taskZenOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TaskZen API")
                        .version("1.0")
                        .description("API para gerenciamento de tarefas"));
    }
}
