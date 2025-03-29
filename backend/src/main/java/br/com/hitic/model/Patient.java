package br.com.hitic.model;

import java.time.LocalDate;

import br.com.hitic.enums.PatientStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Entity
@Table(name = "patient")
public class Patient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String cpf;

	@Column
	private String name;

	@Column
	private LocalDate birthDate;

	@Enumerated(EnumType.STRING)
	@Column
	private PatientStatus patientStatus;

	@Column(columnDefinition = "BYTEA")
	private byte[] patientPicture;
}