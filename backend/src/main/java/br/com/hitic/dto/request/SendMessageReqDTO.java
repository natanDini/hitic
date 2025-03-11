package br.com.hitic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendMessageReqDTO {
	private Long conversationId;
	private String prompt;
}
