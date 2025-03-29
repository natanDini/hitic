package br.com.hitic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendMessageReqDTO {
	private String prompt;
	private Long operatorId;
	private Long conversationId;
}
