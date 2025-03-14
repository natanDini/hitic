package br.com.hitic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Entity
@Table(name = "archive")
public class Archive {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	@Column(columnDefinition = "BYTEA")
	private byte[] archiveBytes;

	@ManyToOne
	@JoinColumn(name = "conversation_id", referencedColumnName = "id", nullable = false)
	private Conversation conversation;

	@ManyToOne
	@JoinColumn(name = "message_id", referencedColumnName = "id", nullable = false)
	private Message message;
}
