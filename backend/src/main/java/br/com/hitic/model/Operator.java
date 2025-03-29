package br.com.hitic.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Entity
@Table(name = "operator")
public class Operator {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	@Column
	private String description;

	@Column
	private String promptTemplate;

	@Column
	private String vectorReference;

	@JsonIgnore
	@OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Conversation> conversations = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Archive> archives = new ArrayList<>();
}
