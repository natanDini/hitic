package br.com.hitic.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import br.com.hitic.dto.response.GeralResDTO;
import br.com.hitic.enums.SeverityStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Component
public class ResponseUtils {

	public ResponseEntity<GeralResDTO> customResponse(String message, SeverityStatus severityStatus,
			HttpStatus httpStatus) {
		log.info(" >>> Criando uma resposta personalizada.");

		GeralResDTO geralResDTO = new GeralResDTO();

		geralResDTO.setMessage(message);
		geralResDTO.setSeverityStatus(severityStatus);

		return ResponseEntity.status(httpStatus).body(geralResDTO);
	}

	public ResponseEntity<GeralResDTO> successResponse(String message) {
		log.info(" >>> Criando uma resposta de Sucesso.");

		GeralResDTO geralResDTO = new GeralResDTO();

		geralResDTO.setMessage(message);
		geralResDTO.setSeverityStatus(SeverityStatus.SUCCESS);

		return ResponseEntity.status(HttpStatus.OK).body(geralResDTO);
	}

	public ResponseEntity<GeralResDTO> errorResponse(String message, HttpStatus httpStatus) {
		log.info(" >>> Criando uma resposta de Erro.");

		GeralResDTO geralResDTO = new GeralResDTO();

		geralResDTO.setMessage(message);
		geralResDTO.setSeverityStatus(SeverityStatus.ERROR);

		return ResponseEntity.status(httpStatus).body(geralResDTO);
	}

	public ResponseEntity<GeralResDTO> warnResponse(String message, HttpStatus httpStatus) {
		log.info(" >>> Criando uma resposta de Alerta.");

		GeralResDTO geralResDTO = new GeralResDTO();

		geralResDTO.setMessage(message);
		geralResDTO.setSeverityStatus(SeverityStatus.WARN);

		return ResponseEntity.status(httpStatus).body(geralResDTO);
	}

	public ResponseEntity<GeralResDTO> infoResponse(String message, HttpStatus httpStatus) {
		log.info(" >>> Criando uma resposta de Info.");

		GeralResDTO geralResDTO = new GeralResDTO();

		geralResDTO.setMessage(message);
		geralResDTO.setSeverityStatus(SeverityStatus.INFO);

		return ResponseEntity.status(httpStatus).body(geralResDTO);
	}

	public ResponseEntity<GeralResDTO> internalServerErrorResponse() {
		log.info(" >>> Criando uma resposta de Alerta.");

		GeralResDTO geralResDTO = new GeralResDTO();

		geralResDTO.setMessage("Erro interno no servidor.");
		geralResDTO.setSeverityStatus(SeverityStatus.ERROR);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(geralResDTO);
	}
}
