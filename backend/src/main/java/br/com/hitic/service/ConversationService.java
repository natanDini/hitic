package br.com.hitic.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.hitic.dto.request.SendMessageReqDTO;
import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Conversation;
import br.com.hitic.model.Message;
import br.com.hitic.repository.ConversationRepository;
import br.com.hitic.repository.MessageRepository;
import br.com.hitic.utils.ConversationUtils;
import br.com.hitic.utils.GeneralUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class ConversationService {

	private final ObjectMapper objectMapper;

	private final GeneralUtils generalUtils;
	private final ConversationUtils conversationUtils;

	private final OpenAiStreamService openAiStreamService;

	private final MessageRepository messageRepository;
	private final ConversationRepository conversationRepository;

	public SseEmitter sendMessage(SendMessageReqDTO sendMessageReqDTO) throws CustomException {
		
		log.error(sendMessageReqDTO.toString());

		Conversation conversation = (sendMessageReqDTO.getConversationId() != null
				&& sendMessageReqDTO.getConversationId() != 0)
						? conversationUtils.findById(sendMessageReqDTO.getConversationId())
						: createConversation(sendMessageReqDTO.getPrompt());

		Message message = new Message();

		message.setQuestionMessage(sendMessageReqDTO.getPrompt());
		message.setConversation(conversation);

		messageRepository.save(message);

		return openAiStreamService.streamChatResponse(sendMessageReqDTO.getPrompt());
	}

	public Conversation createConversation(String name) {

		Conversation conversation = new Conversation();

		conversation.setName((name.length() < 50 ? name.concat("...") : name.substring(0, 50).concat("...")));
		conversation.setCreatedAt(generalUtils.getLocalDateTime());

		return conversationRepository.save(conversation);
	}
}
