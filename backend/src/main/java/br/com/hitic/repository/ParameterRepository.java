package br.com.hitic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hitic.model.Parameter;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Long> {
	boolean existsByParamKey(String paramKey);

	Optional<Parameter> findByParamKey(String paramKey);
}
