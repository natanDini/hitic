package br.com.hitic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientShortResDTO {
	private String cpf;
	private String name;
}
