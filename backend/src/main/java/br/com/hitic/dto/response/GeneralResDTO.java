package br.com.hitic.dto.response;

import br.com.hitic.enums.SeverityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralResDTO {
	private String message;
	private SeverityStatus severityStatus;
}
