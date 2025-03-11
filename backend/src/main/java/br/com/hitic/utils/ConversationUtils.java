package br.com.hitic.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.hitic.enums.SeverityStatus;
import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Conversation;
import br.com.hitic.repository.ConversationRepository;
import lombok.Data;

@Data
@Component
public class ConversationUtils {

	private final ConversationRepository conversationRepository;

	public Conversation findById(Long conversationId) throws CustomException {
		return conversationRepository.findById(conversationId)
				.orElseThrow(() -> new CustomException("Parameter informado não encontrado.", SeverityStatus.ERROR,
						HttpStatus.NOT_FOUND));
	}
}
