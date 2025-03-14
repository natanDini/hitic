package br.com.hitic.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import br.com.hitic.dto.response.GeneralResDTO;
import br.com.hitic.enums.SeverityStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Component
public class ResponseUtils {

	public ResponseEntity<GeneralResDTO> customResponse(String message, SeverityStatus severityStatus,
			HttpStatus httpStatus) {
		log.info(" >>> Criando uma resposta personalizada.");

		GeneralResDTO generalResDTO = new GeneralResDTO();

		generalResDTO.setMessage(message);
		generalResDTO.setSeverityStatus(severityStatus);

		return ResponseEntity.status(httpStatus).body(generalResDTO);
	}

	public ResponseEntity<GeneralResDTO> successResponse(String message) {
		log.info(" >>> Criando uma resposta de Sucesso.");

		GeneralResDTO generalResDTO = new GeneralResDTO();

		generalResDTO.setMessage(message);
		generalResDTO.setSeverityStatus(SeverityStatus.SUCCESS);

		return ResponseEntity.status(HttpStatus.OK).body(generalResDTO);
	}

	public ResponseEntity<GeneralResDTO> errorResponse(String message, HttpStatus httpStatus) {
		log.info(" >>> Criando uma resposta de Erro.");

		GeneralResDTO generalResDTO = new GeneralResDTO();

		generalResDTO.setMessage(message);
		generalResDTO.setSeverityStatus(SeverityStatus.ERROR);

		return ResponseEntity.status(httpStatus).body(generalResDTO);
	}

	public ResponseEntity<GeneralResDTO> warnResponse(String message, HttpStatus httpStatus) {
		log.info(" >>> Criando uma resposta de Alerta.");

		GeneralResDTO generalResDTO = new GeneralResDTO();

		generalResDTO.setMessage(message);
		generalResDTO.setSeverityStatus(SeverityStatus.WARN);

		return ResponseEntity.status(httpStatus).body(generalResDTO);
	}

	public ResponseEntity<GeneralResDTO> infoResponse(String message, HttpStatus httpStatus) {
		log.info(" >>> Criando uma resposta de Info.");

		GeneralResDTO generalResDTO = new GeneralResDTO();

		generalResDTO.setMessage(message);
		generalResDTO.setSeverityStatus(SeverityStatus.INFO);

		return ResponseEntity.status(httpStatus).body(generalResDTO);
	}

	public ResponseEntity<GeneralResDTO> internalServerErrorResponse() {
		log.info(" >>> Criando uma resposta de Alerta.");

		GeneralResDTO generalResDTO = new GeneralResDTO();

		generalResDTO.setMessage("Erro interno no servidor.");
		generalResDTO.setSeverityStatus(SeverityStatus.ERROR);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generalResDTO);
	}
}
