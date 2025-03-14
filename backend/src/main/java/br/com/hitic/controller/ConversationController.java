package br.com.hitic.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import br.com.hitic.dto.request.SendMessageReqDTO;
import br.com.hitic.dto.response.GeneralResDTO;
import br.com.hitic.dto.response.MessageResDTO;
import br.com.hitic.exception.CustomException;
import br.com.hitic.service.ConversationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@RestController
@RequestMapping("/conversation")
public class ConversationController {

	private final ConversationService conversationService;

	@PostMapping("/send-message")
	public SseEmitter sendMessage(@RequestBody SendMessageReqDTO sendMessageReqDTO) throws CustomException {
		log.info(" >>> Tentativa de envio de nova mensagem");
		return conversationService.sendMessage(sendMessageReqDTO);
	}

	@GetMapping("/{conversationId}/message")
	public ResponseEntity<List<MessageResDTO>> messagesByConversation(@PathVariable Long conversationId)
			throws CustomException {
		log.info(" >>> Tentativa de listagem de messages na conversation de id: " + conversationId);
		return conversationService.messagesByConversation(conversationId);
	}

	@DeleteMapping("/delete/{conversationId}")
	public ResponseEntity<GeneralResDTO> delete(@PathVariable Long conversationId) throws CustomException {
		log.info(" >>> Tentativa de deletar conversation de id: " + conversationId);
		return conversationService.delete(conversationId);
	}
}
