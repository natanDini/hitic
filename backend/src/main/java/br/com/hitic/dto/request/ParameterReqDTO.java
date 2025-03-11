package br.com.hitic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParameterReqDTO {
	private String key;
	private String value;
}
