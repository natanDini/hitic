package br.com.hitic.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Message;
import br.com.hitic.repository.MessageRepository;
import br.com.hitic.utils.ParameterUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class OpenAiStreamService {

	private OpenAiService openAiService;

	private final ParameterUtils parameterUtils;

	private final MessageRepository messageRepository;

	@PostConstruct
	public void init() throws CustomException {
		this.openAiService = new OpenAiService(parameterUtils.findByParamKey("OPENAI_API").getValue());
	}

	public SseEmitter streamChatResponse(String userMessage, Message message) {
		SseEmitter emitter = new SseEmitter();
		StringBuilder fullResponse = new StringBuilder();

		ChatCompletionRequest request = ChatCompletionRequest.builder().model("gpt-4")
				.messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), userMessage))).stream(true).build();

		new Thread(() -> {
			try {
				openAiService.streamChatCompletion(request).doOnNext(response -> {
					response.getChoices().forEach(choice -> {
						String content = choice.getMessage().getContent();
						if (content != null) {
							fullResponse.append(content);

							try {
								emitter.send(SseEmitter.event().data(content));
							} catch (IOException e) {
								emitter.completeWithError(e);
							}
						}
					});
				}).doOnComplete(() -> {
					message.setAnswerMessage(fullResponse.toString());
					messageRepository.save(message);
					emitter.complete();
				}).subscribe();
			} catch (Exception e) {
				emitter.completeWithError(e);
			}
		}).start();

		return emitter;
	}
}
