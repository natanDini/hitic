package br.com.hitic.dto.request;

import lombok.Data;

@Data
public class OperatorQueryPayloadReqDTO {
	private String question;
	private String vectorReference;
}
