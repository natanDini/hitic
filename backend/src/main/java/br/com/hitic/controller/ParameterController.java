package br.com.hitic.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.hitic.dto.request.ParameterReqDTO;
import br.com.hitic.dto.response.GeneralResDTO;
import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Parameter;
import br.com.hitic.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@RestController
@RequestMapping("/parameter")
public class ParameterController {

	private final ParameterService parameterServcie;

	@Operation(summary = "Registrar Parameter")
	@PostMapping("/register")
	public ResponseEntity<GeneralResDTO> register(@RequestBody ParameterReqDTO parameterReqDTO) throws CustomException {
		log.info(" >>> Tentando registrar um parameter na aplicação.");
		return parameterServcie.register(parameterReqDTO);
	}

	@Operation(summary = "Editar Parameter")
	@PutMapping("/edit/{parameterId}")
	public ResponseEntity<GeneralResDTO> edit(@PathVariable Long parameterId,
			@RequestBody ParameterReqDTO parameterReqDTO) throws CustomException {
		log.info(" >>> Tentando editar um parameter na aplicação.");
		return parameterServcie.edit(parameterId, parameterReqDTO);
	}

	@Operation(summary = "Deletar Parameter")
	@DeleteMapping("/delete/{parameterId}")
	public ResponseEntity<GeneralResDTO> delete(@PathVariable Long parameterId) throws CustomException {
		log.info(" >>> Tentando deletar um parameter na aplicação.");
		return parameterServcie.delete(parameterId);
	}

	@Operation(summary = "Listar todos os Parameters")
	@GetMapping("/list")
	public ResponseEntity<List<Parameter>> listarParametros() throws CustomException {
		log.info(" >>> Tentando listar todos os parâmetros.");
		return parameterServcie.list();
	}
}
