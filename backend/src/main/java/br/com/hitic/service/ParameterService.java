package br.com.hitic.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.hitic.dto.request.ParameterReqDTO;
import br.com.hitic.dto.response.GeralResDTO;
import br.com.hitic.exception.CustomException;
import br.com.hitic.model.Parameter;
import br.com.hitic.repository.ParameterRepository;
import br.com.hitic.utils.ParameterUtils;
import br.com.hitic.utils.ResponseUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class ParameterService {

	private final ResponseUtils responseUtils;

	private final ParameterUtils parameterUtils;

	private final ParameterRepository parameterRepository;

	public ResponseEntity<GeralResDTO> register(ParameterReqDTO parameterReqDTO) throws CustomException {

		parameterUtils.existsByParamKey(parameterReqDTO.getParamKey());

		Parameter parameter = new Parameter();

		parameter.setParamKey(parameterReqDTO.getParamKey());
		parameter.setValue(parameterReqDTO.getValue());

		parameterRepository.save(parameter);

		log.info("Parameter registrado com sucesso.");
		return responseUtils.successResponse("Parameter registrado com sucesso!");
	}

	public ResponseEntity<GeralResDTO> edit(Long parameterId, ParameterReqDTO parameterReqDTO) throws CustomException {

		Parameter parameter = parameterUtils.findById(parameterId);

		parameterUtils.existsByParamKey(parameterReqDTO.getParamKey());

		parameter.setParamKey(parameterReqDTO.getParamKey());
		parameter.setValue(parameterReqDTO.getValue());

		parameterRepository.save(parameter);

		log.info("Parameter editado com sucesso.");
		return responseUtils.successResponse("Parameter editado com sucesso!");

	}

	public ResponseEntity<GeralResDTO> delete(Long parameterId) throws CustomException {

		Parameter parameter = parameterUtils.findById(parameterId);

		parameterRepository.delete(parameter);

		log.info("Parameter deletado com sucesso.");
		return responseUtils.successResponse("Parameter deletado com sucesso!");
	}

	public ResponseEntity<?> list() throws CustomException {

		List<Parameter> parametersList = parameterRepository.findAll();

		return ResponseEntity.ok().body(parametersList);
	}
}
