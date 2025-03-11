package br.com.hitic.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import br.com.hitic.dto.request.SendMessageReqDTO;
import br.com.hitic.service.ConversationService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@RestController
@RequestMapping("/conversation")
public class ConversationController {

	private final ConversationService converstionService;

	@PostMapping("/send-message")
	public SseEmitter sendMessage(@RequestBody SendMessageReqDTO sendMessageReqDTO) throws CustomExeption {
		log.info("Tentativa de envio de nova mensagem");
		return conversationService.sendMessage(sendMessageReqDTO);
	}
}
