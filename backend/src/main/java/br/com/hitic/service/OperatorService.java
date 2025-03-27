package br.com.hitic.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import br.com.hitic.dto.request.OperatorInsertPayloadReqDTO;
import br.com.hitic.dto.response.ConversationShortResDTO;
import br.com.hitic.dto.response.GeneralResDTO;
import br.com.hitic.dto.response.OperatorInsertResDTO;
import br.com.hitic.dto.response.OperatorShortResDTO;
import br.com.hitic.enums.SeverityStatus;
import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Conversation;
import br.com.hitic.model.Operator;
import br.com.hitic.repository.ConversationRepository;
import br.com.hitic.repository.OperatorRepository;
import br.com.hitic.utils.GeneralUtils;
import br.com.hitic.utils.OperatorUtils;
import br.com.hitic.utils.ResponseUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class OperatorService {

	@Value("${vector.api.url.insert}")
	private String VECTOR_API_URL_INSERT;

	private final RestTemplate restTemplate;

	private final GeneralUtils generalUtils;
	private final ResponseUtils responseUtils;
	private final OperatorUtils operatorUtils;

	private final ArchiveService archiveService;

	private final ConversationRepository conversationRepository;
	private final OperatorRepository operatorRepository;

	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public ResponseEntity<GeneralResDTO> create(String name, String description, String promptTemplate,
			MultipartFile[] archives) throws CustomException, IOException {

		operatorUtils.existsByName(name);

		if (archives == null || archives.length == 0 || Arrays.stream(archives).allMatch(MultipartFile::isEmpty)) {
			log.info(" >>> Erro ao tentar criar Operator: pelo menos um arquivo deve ser enviado.");
			throw new CustomException("Pelo menos um arquivo deve ser enviado.", SeverityStatus.ERROR,
					HttpStatus.BAD_REQUEST);
		}

		Operator operator = new Operator();

		operator.setName(name);
		operator.setDescription(description);
		operator.setPromptTemplate(promptTemplate);

		operatorRepository.save(operator);

		String processedArchives = archiveService.processArchives(archives, operator);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		OperatorInsertPayloadReqDTO payload = new OperatorInsertPayloadReqDTO();

		payload.setName(name);
		payload.setText(processedArchives);

		HttpEntity<OperatorInsertPayloadReqDTO> requestEntity = new HttpEntity<>(payload, headers);

		log.info(" >>> Enviando requisição de OCR para o serviço externo.");
		ResponseEntity<OperatorInsertResDTO> response = restTemplate.exchange(VECTOR_API_URL_INSERT, HttpMethod.POST,
				requestEntity, OperatorInsertResDTO.class);

		operator.setVectorReference(response.getBody().getVectorReference());

		operatorRepository.save(operator);

		log.info(" >>> Operator criado com sucesso.");
		return responseUtils.successResponse("Operator criado com sucesso!");
	}

	public ResponseEntity<List<OperatorShortResDTO>> list() throws CustomException {

		List<Operator> operatorsList = operatorRepository.findAll();

		generalUtils.emptyListVerifier(operatorsList, "Não foram encontrados operators cadastrados na aplicação.");

		List<OperatorShortResDTO> operatorsListDTO = operatorsList.stream()
				.map(opr -> new OperatorShortResDTO(opr.getId(), opr.getName())).collect(Collectors.toList());

		log.info(" >>> Listando Operators com sucesso.");
		return ResponseEntity.ok(operatorsListDTO);
	}

	public ResponseEntity<List<ConversationShortResDTO>> listConversationByOperator(Long operatorId)
			throws CustomException {

		Operator operator = operatorUtils.findById(operatorId);

		List<Conversation> listConversations = conversationRepository.findByOperator(operator);

		generalUtils.emptyListVerifier(listConversations,
				"Não foram encontrados conversas cadastrados para esse operator.");

		List<ConversationShortResDTO> listConversationDTO = listConversations.stream()
				.map(cnv -> new ConversationShortResDTO(cnv.getId(), cnv.getName(), cnv.getCreatedAt()))
				.collect(Collectors.toList());

		log.info(" >>> Listando conversas de Operator Id: {} - com sucesso.", operatorId);
		return ResponseEntity.ok(listConversationDTO);
	}

	public ResponseEntity<GeneralResDTO> delete(Long operatorId) throws CustomException {

		Operator operator = operatorUtils.findById(operatorId);

		operatorRepository.delete(operator);

		log.info(" >>> Operator Id: {} - deletado com sucesso.", operatorId);
		return responseUtils.successResponse("Operator deletado com sucesso!");
	}
}
