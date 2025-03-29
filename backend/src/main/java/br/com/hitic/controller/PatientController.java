package br.com.hitic.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.hitic.dto.response.GeneralResDTO;
import br.com.hitic.dto.response.PatientBasePicResDTO;
import br.com.hitic.dto.response.PatientShortResDTO;
import br.com.hitic.exception.CustomException;
import br.com.hitic.service.PatientService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@RestController
@RequestMapping("/patient")
public class PatientController {

	private final PatientService patientService;

	@PostMapping("/register")
	public ResponseEntity<GeneralResDTO> register(@RequestParam String cpf, @RequestParam String name,
			@RequestParam LocalDate birthDate, @RequestParam MultipartFile patientPicture) throws IOException {
		log.info(" >>> Recebendo solicitação de registro de paciente.");
		return patientService.register(cpf, name, birthDate, patientPicture);
	}

	@DeleteMapping("/delete/{patientId}")
	public ResponseEntity<GeneralResDTO> delete(@PathVariable Long patientId) throws CustomException {
		log.info(" >>> Recebendo solicitação de deleção de paciente de id {}.", patientId);
		return patientService.delete(patientId);
	}

	@PostMapping("/recognize")
	public ResponseEntity<PatientBasePicResDTO> recognize(@RequestParam MultipartFile file)
			throws IOException, CustomException {
		log.info(" >>> Recebendo solicitação de reconhecimento facial.");
		return patientService.recognize(file);
	}

	@GetMapping("/list/absent")
	public ResponseEntity<List<PatientShortResDTO>> listAbsent() throws CustomException {
		log.info(" >>> Recebendo solicitação para listar pacientes ausentes.");
		return patientService.listAbsent();
	}

	@GetMapping("/list/present")
	public ResponseEntity<List<PatientShortResDTO>> listPresent() throws CustomException {
		log.info(" >>> Recebendo solicitação para listar pacientes presentes.");
		return patientService.listPresent();
	}

	@PostMapping("/switch-absent")
	public ResponseEntity<GeneralResDTO> switchToAbsent() {
		log.info(" >>> Recebendo solicitação para tornar todos os pacientes em ausente.");
		return patientService.switchToAbsent();
	}
}
