package br.com.hitic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParameterReqDTO {
	private String paramKey;
	private String value;
}
