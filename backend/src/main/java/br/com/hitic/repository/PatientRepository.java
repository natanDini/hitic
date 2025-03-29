package br.com.hitic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.hitic.enums.PatientStatus;
import br.com.hitic.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

	List<Patient> findByPatientStatus(PatientStatus status);

	@Modifying
	@Query("UPDATE Patient p SET p.patientStatus = :status")
	void updateAllPatientStatus(@Param("status") PatientStatus status);
}
