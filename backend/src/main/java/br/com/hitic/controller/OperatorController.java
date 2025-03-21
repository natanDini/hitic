package br.com.hitic.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.hitic.dto.response.GeneralResDTO;
import br.com.hitic.exception.CustomException;
import br.com.hitic.service.OperatorService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@RestController
@RequestMapping("/operator")
public class OperatorController {

	private final OperatorService operatorServcie;

	@PostMapping("/create")
	public ResponseEntity<GeneralResDTO> create(@RequestParam String name, @RequestParam String description,
			@RequestParam String promptTemplate, @RequestParam MultipartFile[] archives)
			throws CustomException, IOException {
		log.info(" >>> Tentativa de resgistro de novo Operator na aplicação.");
		return operatorServcie.create(name, description, promptTemplate, archives);
	}
}
