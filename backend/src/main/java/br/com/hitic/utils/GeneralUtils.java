package br.com.hitic.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Component
public class GeneralUtils {

	public LocalDateTime getLocalDateTime() {
		return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
	}
}
