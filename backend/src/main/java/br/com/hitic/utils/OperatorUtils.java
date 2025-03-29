package br.com.hitic.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.hitic.enums.SeverityStatus;
import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Operator;
import br.com.hitic.repository.OperatorRepository;
import lombok.Data;

@Data
@Component
public class OperatorUtils {

	private final OperatorRepository operatorRepository;

	public Operator findById(Long operatorId) throws CustomException {
		return operatorRepository.findById(operatorId)
				.orElseThrow(() -> new CustomException("Operator informado não encontrado.", SeverityStatus.ERROR,
						HttpStatus.NOT_FOUND));
	}

	public void existsByName(String name) throws CustomException {

		if (operatorRepository.existsByName(name)) {
			throw new CustomException("Já existe um Operator registrado com esse nome.", SeverityStatus.ERROR,
					HttpStatus.BAD_REQUEST);
		}
	}
}
