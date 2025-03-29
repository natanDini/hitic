package br.com.hitic.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.hitic.enums.SeverityStatus;
import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Parameter;
import br.com.hitic.repository.ParameterRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Component
public class ParameterUtils {

	private final ParameterRepository parameterRepository;

	public void existsByParamKey(String paramKey) throws CustomException {
		if (parameterRepository.existsByParamKey(paramKey)) {
			throw new CustomException("Já existe uma chave cadastrada com esse nome.", SeverityStatus.ERROR,
					HttpStatus.BAD_REQUEST);
		}
	}

	public Parameter findById(Long parameterId) throws CustomException {
		return parameterRepository.findById(parameterId)
				.orElseThrow(() -> new CustomException("Parameter informado não encontrado.", SeverityStatus.ERROR,
						HttpStatus.NOT_FOUND));
	}

	public Parameter findByParamKey(String paramKey) throws CustomException {
		return parameterRepository.findByParamKey(paramKey)
				.orElseThrow(() -> new CustomException("Parameter informado não encontrado.", SeverityStatus.ERROR,
						HttpStatus.NOT_FOUND));
	}
}
