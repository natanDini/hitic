package br.com.hitic.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.hitic.dto.response.ConversationShortResDTO;
import br.com.hitic.dto.response.GeneralResDTO;
import br.com.hitic.dto.response.OperatorShortResDTO;
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

	@GetMapping("/list")
	public ResponseEntity<List<OperatorShortResDTO>> list() throws CustomException {
		log.info(" >>> Tentativa de listar todos os operators na aplicação.");
		return operatorServcie.list();
	}

	@GetMapping("/{operatorId}/conversations")
	public ResponseEntity<List<ConversationShortResDTO>> listConversationByOperator(@PathVariable Long operatorId)
			throws CustomException {
		log.info(" >>> Tentativa de listar conversations com o operator de ID: {} .", operatorId);
		return operatorServcie.listConversationByOperator(operatorId);
	}

	@DeleteMapping("/delete/{operatorId}")
	public ResponseEntity<GeneralResDTO> delete(@PathVariable Long operatorId) throws CustomException {
		log.info(" >>> Tentativa de deletar o operator de ID: {} .", operatorId);
		return operatorServcie.delete(operatorId);
	}
}
