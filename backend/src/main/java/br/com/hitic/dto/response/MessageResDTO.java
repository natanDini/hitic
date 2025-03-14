package br.com.hitic.dto.response;

import lombok.Data;

@Data
public class MessageResDTO {
	private Long id;
	private String questionMessage;
	private String answerMessage;
}
