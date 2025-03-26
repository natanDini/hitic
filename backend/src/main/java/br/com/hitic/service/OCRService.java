package br.com.hitic.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import br.com.hitic.enums.SeverityStatus;
import br.com.hitic.exception.CustomException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class OCRService {

	@Value("${ocr.api.url}")
	private String OCR_API_URL;

	private final RestTemplate restTemplate;

	public String sendToOcr(MultipartFile file) throws CustomException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", file.getResource());

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			log.info(" >>> Enviando requisição de OCR para o serviço externo.");
			ResponseEntity<String> response = restTemplate.exchange(OCR_API_URL, HttpMethod.POST, requestEntity,
					String.class);

			return response.getBody();
		} catch (Exception e) {
			log.error("Erro Interno no serviço de OCR: " + e.getMessage());
			throw new CustomException("Erro interno no serviço de OCR.", SeverityStatus.ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
