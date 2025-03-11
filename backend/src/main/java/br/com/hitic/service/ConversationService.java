package br.com.hitic.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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

	private final WebClient openAiWebClient;
	private final ObjectMapper objectMapper;

	private final GeneralUtils generalUtils;
	private final ConversationUtils conversationUtils;

	private final MessageRepository messageRepository;
	private final ConversationRepository conversationRepository;

	public SseEmitter sendMessage(SendMessageReqDTO sendMessageReqDTO) throws CustomException {

		Conversation existingConversation = conversationUtils.findById(sendMessageReqDTO.getConversationId());

		Conversation conversation = (existingConversation != null) ? existingConversation
				: createConversation(sendMessageReqDTO.getPrompt());

		Message message = new Message();

		message.setQuestionMessage(sendMessageReqDTO.getPrompt());
		message.setConversation(conversation);

		messageRepository.save(message);

		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

		// Corpo da requisição para OpenAI
		Map<String, Object> requestBody = Map.of("model", "gpt-4-turbo", "messages",
				List.of(Map.of("role", "user", "content", sendMessageReqDTO.getPrompt())), "stream", true);

		openAiWebClient.post().bodyValue(requestBody).retrieve().bodyToFlux(String.class)
				.doOnNext(response -> processStream(response, emitter))
				.doOnError(error -> emitter.completeWithError(error)).doOnComplete(emitter::complete);

		return emitter;
	}

	private void processStream(String response, SseEmitter emitter) {
		try {
			String[] lines = response.split("\n");
			for (String line : lines) {
				if (line.startsWith("data: ")) {
					String json = line.substring(6).trim(); // Remove "data: " do início
					if (!json.equals("[DONE]")) {
						Map<String, Object> parsed = objectMapper.readValue(json, Map.class);
						List<Map<String, String>> choices = (List<Map<String, String>>) parsed.get("choices");
						if (!choices.isEmpty()) {
							String content = choices.get(0).get("delta").get("content");
							if (content != null) {
								emitter.send(content, MediaType.TEXT_PLAIN);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			emitter.completeWithError(e);
		}
	}

	public Conversation createConversation(String name) {

		Conversation conversation = new Conversation();

		conversation.setName((name.length() < 50 ? name.concat("...") : name.substring(0, 50).concat("...")));
		conversation.setCreatedAt(generalUtils.getLocalDateTime());

		return conversationRepository.save(conversation);
	}
}
