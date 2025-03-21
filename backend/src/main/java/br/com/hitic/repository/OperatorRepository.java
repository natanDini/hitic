package br.com.hitic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hitic.model.Operator;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {

	boolean existsByName(String name);
}
