package br.com.hitic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.hitic.repository.ParameterRepository;
import lombok.Data;

@Data
@Configuration
public class WebClientConfig {

	private final ParameterRepository parameterRepository;

	@Bean
	WebClient openAiWebClient() {

		String apiKey = parameterRepository.findByKey("OPEANAI_API");

		return WebClient.builder().baseUrl("https://api.openai.com/v1/chat/completions")
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ".concat(apiKey))
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
	}
}