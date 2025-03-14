package br.com.hitic.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.hitic.service.OCRService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@RestController
@RequestMapping("/ocr")
public class OCRController {

	private final OCRService ocrService;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(MultipartFile file) {
		String response = ocrService.sendToOcr(file);
		return ResponseEntity.ok(response);
	}
}
