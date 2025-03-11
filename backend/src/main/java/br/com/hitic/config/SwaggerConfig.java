package br.com.hitic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title("Documentação das APIs HITIC").version("2.2.0")
				.description("Esta é a documentação da API do projeto HITIC / UnDF / 2025."));
	}
}