package br.com.hitic.dto.response;

import java.time.LocalDate;
import java.util.Base64;

import br.com.hitic.model.Patient;
import lombok.Data;

@Data
public class PatientBasePicResDTO {

	private Long id;
	private String cpf;
	private String name;
	private LocalDate birthDate;
	private String patientStatus;
	private String patientPicture;

	public PatientBasePicResDTO(Patient patient) {
		super();
		this.id = patient.getId();
		this.cpf = patient.getCpf();
		this.name = patient.getName();
		this.birthDate = patient.getBirthDate();
		this.patientStatus = patient.getPatientStatus().name();
		this.patientPicture = "data:image/png;base64," +Base64.getEncoder().encodeToString(patient.getPatientPicture());
	}
}
