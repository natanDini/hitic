package br.com.hitic.exception;

import org.springframework.http.HttpStatus;

import br.com.hitic.enums.SeverityStatus;

public class CustomException extends Exception {
	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;
	private final SeverityStatus severityStatus;

	public CustomException(String message, SeverityStatus severityStatus, HttpStatus httpStatus) {
		super(message);
		this.severityStatus = severityStatus;
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public SeverityStatus getSeverityStatus() {
		return severityStatus;
	}
}
