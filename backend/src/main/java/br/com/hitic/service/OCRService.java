package br.com.hitic.service;

import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.hitic.exception.CustomException;
import br.com.hitic.utils.ParameterUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class OCRService {

	@Value("${ocr.api.url}")
	private String OCR_API_URL;

	private final ParameterUtils parameterUtils;

	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;

	private String ocrApiKey;

	@PostConstruct
	public void init() throws CustomException {
		this.ocrApiKey = parameterUtils.findByParamKey("OCR_API").getValue();
	}

	public String sendToOcr(MultipartFile file) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.set("apikey", ocrApiKey);

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", file.getResource());
			body.add("language", "por");
			body.add("isOverlayRequired", "false");
			body.add("OCREngine", "2");

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			ResponseEntity<String> response = restTemplate.postForEntity(OCR_API_URL, requestEntity, String.class);

			return extractParsedText(response.getBody());

		} catch (Exception e) {
			throw new RuntimeException("Erro ao enviar para OCR", e);
		}
	}

	private String extractParsedText(String jsonResponse) {
		try {
			JsonNode root = objectMapper.readTree(jsonResponse);
			JsonNode parsedResults = root.path("ParsedResults");

			return StreamSupport.stream(parsedResults.spliterator(), false)
					.map(node -> node.path("ParsedText").asText().replaceAll("\n", " ").replaceAll("\\s+", " "))
					.reduce("", (acc, text) -> acc + text + " ").trim();

		} catch (Exception e) {
			throw new RuntimeException("Erro ao processar a resposta JSON do OCR", e);
		}
	}

}
