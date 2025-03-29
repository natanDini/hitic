package br.com.hitic.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.hitic.enums.SeverityStatus;
import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Patient;
import br.com.hitic.repository.PatientRepository;
import lombok.Data;

@Data
@Component
public class PatientUtils {

	private final PatientRepository patientRepository;

	public Patient findById(Long patientId) throws CustomException {
		return patientRepository.findById(patientId)
				.orElseThrow(() -> new CustomException("Paciente informado não encontrado.", SeverityStatus.ERROR,
						HttpStatus.NOT_FOUND));
	}
}
