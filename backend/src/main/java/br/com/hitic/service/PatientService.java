package br.com.hitic.service;

import java.io.IOException;
import java.time.LocalDate;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import br.com.hitic.dto.response.GeneralResDTO;
import br.com.hitic.dto.response.PatientBasePicResDTO;
import br.com.hitic.dto.response.PatientFaceRecognitionResDTO;
import br.com.hitic.dto.response.PatientShortResDTO;
import br.com.hitic.enums.PatientStatus;
import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Patient;
import br.com.hitic.repository.PatientRepository;
import br.com.hitic.utils.GeneralUtils;
import br.com.hitic.utils.MultipartInputStreamFileResourceUtils;
import br.com.hitic.utils.PatientUtils;
import br.com.hitic.utils.ResponseUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class PatientService {

	@Value("${attendance.api.url}")
	private String ATTENDANCE_CHECKER_URL;

	private final RestTemplate restTemplate;

	private final PatientUtils patientUtils;
	private final GeneralUtils generalUtils;
	private final ResponseUtils responseUtils;

	private final PatientRepository patientRepository;

	public ResponseEntity<GeneralResDTO> register(String cpf, String name, LocalDate birthDate,
			MultipartFile patientPicture) throws IOException {

		Patient patient = new Patient();

		patient.setCpf(cpf);
		patient.setName(name);
		patient.setBirthDate(birthDate);
		patient.setPatientPicture(patientPicture.getBytes());
		patient.setPatientStatus(PatientStatus.ABSENT);

		patientRepository.save(patient);

		log.info(" >>> Paciente registrado com sucesso.");
		return responseUtils.successResponse("Paciente registrado com sucesso.");
	}

	public ResponseEntity<GeneralResDTO> delete(Long patientId) throws CustomException {

		Patient patient = patientUtils.findById(patientId);

		patientRepository.delete(patient);

		log.info(" >>> Paciente deletado com sucesso.");
		return responseUtils.successResponse("Paciente deletado com sucesso.");
	}

	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public ResponseEntity<PatientBasePicResDTO> recognize(MultipartFile file) throws IOException, CustomException {

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new MultipartInputStreamFileResourceUtils(file.getInputStream(), file.getOriginalFilename()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		ResponseEntity<PatientFaceRecognitionResDTO> response = restTemplate.exchange(ATTENDANCE_CHECKER_URL,
				HttpMethod.POST, requestEntity, PatientFaceRecognitionResDTO.class);

		Patient patient = patientUtils.findById(response.getBody().getUserId());

		patient.setPatientStatus(PatientStatus.PRESENT);

		patientRepository.save(patient);

		log.info(" >>> Paciente reconhecido com sucesso.");
		return ResponseEntity.ok(new PatientBasePicResDTO(patient));
	}

	public ResponseEntity<List<PatientShortResDTO>> listAbsent() throws CustomException {

		List<Patient> listAbsent = patientRepository.findByPatientStatus(PatientStatus.ABSENT);

		generalUtils.emptyListVerifier(listAbsent, "Não foram encontrados pacientes ausentes.");

		List<PatientShortResDTO> listAbsentPatientShortResDTO = listAbsent.stream()
				.map(patient -> new PatientShortResDTO(patient.getCpf(), patient.getName()))
				.collect(Collectors.toList());

		log.info(" >>> Pacientes ausentes listados com sucesso.");
		return ResponseEntity.ok(listAbsentPatientShortResDTO);
	}

	public ResponseEntity<List<PatientShortResDTO>> listPresent() throws CustomException {

		List<Patient> listPresent = patientRepository.findByPatientStatus(PatientStatus.PRESENT);

		generalUtils.emptyListVerifier(listPresent, "Não foram encontrados pacientes ausentes.");

		List<PatientShortResDTO> listPresentPatientShortResDTO = listPresent.stream()
				.map(patient -> new PatientShortResDTO(patient.getCpf(), patient.getName()))
				.collect(Collectors.toList());

		log.info(" >>> Pacientes presentes listados com sucesso.");
		return ResponseEntity.ok(listPresentPatientShortResDTO);
	}

	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public ResponseEntity<GeneralResDTO> switchToAbsent() {

		patientRepository.updateAllPatientStatus(PatientStatus.ABSENT);

		log.info(" >>> Todos os pacientes tiveram seu status alterado para ausente com sucesso.");
		return responseUtils.successResponse("Todos os pacientes tiveram seu status alterado para ausente.");
	}
}
