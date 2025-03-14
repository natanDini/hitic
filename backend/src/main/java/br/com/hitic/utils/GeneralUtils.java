package br.com.hitic.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.hitic.enums.SeverityStatus;
import br.com.hitic.exception.CustomException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Component
public class GeneralUtils {

	public LocalDateTime getLocalDateTime() {
		return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
	}

	public void emptyListVerifier(List<?> list, String message) throws CustomException {

		if (list.isEmpty()) {
			log.info(" >>> Lista está vazia: " + message);
			throw new CustomException(message, SeverityStatus.WARN, HttpStatus.NOT_FOUND);
		}
	}
}
