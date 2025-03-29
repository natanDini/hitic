package br.com.hitic.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.hitic.utils.ResponseUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@ControllerAdvice
public class GlobalHandlerException {

	private final ResponseUtils responseUtils;

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException(Exception ex) {
		log.error(" >>> Erro interno no servidor: " + ex);
		return responseUtils.internalServerErrorResponse();
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
		log.error(" >>> Erro interno no servidor: " + ex);
		return responseUtils.internalServerErrorResponse();
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<?> handleCustomException(CustomException ex) {
		log.error(" >>> Erro interno no servidor: " + ex);
		return responseUtils.customResponse(ex.getMessage(), ex.getSeverityStatus(), ex.getHttpStatus());
	}
}