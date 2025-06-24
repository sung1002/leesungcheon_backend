package com.example.projectwb.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
            .title("Banking API 명세서")
            .version("v1.1.0")
            .description("계좌 거래내역 관리, 입출금, 이체 서비스를 제공하는 API 목록");

        return new OpenAPI()
            .components(new Components())
            .info(info);
    }
}
