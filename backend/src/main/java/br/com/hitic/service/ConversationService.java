package br.com.hitic.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.hitic.dto.request.OperatorQueryPayloadReqDTO;
import br.com.hitic.dto.request.SendMessageReqDTO;
import br.com.hitic.dto.response.GeneralResDTO;
import br.com.hitic.dto.response.MessageResDTO;
import br.com.hitic.dto.response.OperatorQueryResDTO;
import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Conversation;
import br.com.hitic.model.Message;
import br.com.hitic.model.Operator;
import br.com.hitic.repository.ConversationRepository;
import br.com.hitic.repository.MessageRepository;
import br.com.hitic.utils.ConversationUtils;
import br.com.hitic.utils.GeneralUtils;
import br.com.hitic.utils.OperatorUtils;
import br.com.hitic.utils.ResponseUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class ConversationService {

	@Value("${vector.api.url.query}")
	private String VECTOR_API_URL_QUERY;

	private final ObjectMapper objectMapper;

	private final RestTemplate restTemplate;

	private final GeneralUtils generalUtils;
	private final ResponseUtils responseUtils;
	private final OperatorUtils operatorUtils;
	private final ConversationUtils conversationUtils;

	private final OpenAiStreamService openAiStreamService;

	private final MessageRepository messageRepository;
	private final ConversationRepository conversationRepository;

	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public SseEmitter sendMessage(SendMessageReqDTO sendMessageReqDTO) throws CustomException {

		log.info(" >>> Requisição de mensagem recebido: " + sendMessageReqDTO.toString());

		Operator operator = operatorUtils.findById(sendMessageReqDTO.getOperatorId());

		Conversation conversation = (sendMessageReqDTO.getConversationId() != null
				&& sendMessageReqDTO.getConversationId() != 0)
						? conversationUtils.findById(sendMessageReqDTO.getConversationId())
						: createConversation(sendMessageReqDTO.getPrompt(), operator);

		Message message = new Message();

		message.setQuestionMessage(sendMessageReqDTO.getPrompt());
		message.setConversation(conversation);

		messageRepository.save(message);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		OperatorQueryPayloadReqDTO payload = new OperatorQueryPayloadReqDTO();

		payload.setQuestion(sendMessageReqDTO.getPrompt());
		payload.setVectorReference(operator.getVectorReference());

		HttpEntity<OperatorQueryPayloadReqDTO> requestEntity = new HttpEntity<>(payload, headers);

		ResponseEntity<OperatorQueryResDTO> response = restTemplate.exchange(VECTOR_API_URL_QUERY, HttpMethod.POST,
				requestEntity, OperatorQueryResDTO.class);

		String finalPrompt = sendMessageReqDTO.getPrompt().concat(operator.getPromptTemplate())
				.concat(response.getBody().getContent());

		log.info(" >>> Pergunta enviada para o modelo: " + finalPrompt);

		log.info(" >>> Enviando nova message da conversation de id: " + conversation.getId() + " - com sucesso.");
		return openAiStreamService.streamChatResponse(finalPrompt, message);
	}

	public Conversation createConversation(String name, Operator operator) {

		Conversation conversation = new Conversation();

		conversation.setName((name.length() < 50 ? name.concat("...") : name.substring(0, 50).concat("...")));
		conversation.setCreatedAt(generalUtils.getLocalDateTime());
		conversation.setOperator(operator);

		return conversationRepository.save(conversation);
	}

	public ResponseEntity<List<MessageResDTO>> messagesByConversation(Long conversationId) throws CustomException {

		Conversation conversation = conversationUtils.findById(conversationId);

		List<Message> messageList = messageRepository.findByConversation(conversation);

		generalUtils.emptyListVerifier(messageList, "Não foram encontradas mensagens para essa conversa.");

		List<MessageResDTO> messageResDTOList = messageList.stream().map(message -> {
			MessageResDTO dto = new MessageResDTO();
			dto.setId(message.getId());
			dto.setQuestionMessage(message.getQuestionMessage());
			dto.setAnswerMessage(message.getAnswerMessage());
			return dto;
		}).collect(Collectors.toList());

		log.info(" >>> Retornando messages da conversation de id: " + conversationId + " - com sucesso.");
		return ResponseEntity.ok(messageResDTOList);
	}

	public ResponseEntity<GeneralResDTO> delete(Long conversationId) throws CustomException {

		Conversation conversation = conversationUtils.findById(conversationId);

		conversationRepository.delete(conversation);

		log.info(" >>> Deletando conversation de id: " + conversationId + " - com sucesso.");
		return responseUtils.successResponse("Conversation deletada com sucesso.");
	}
}
