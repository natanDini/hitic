package br.com.hitic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hitic.model.Parameter;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Long> {
	boolean existsByKey(String key);

	String findByKey(String key);
}
