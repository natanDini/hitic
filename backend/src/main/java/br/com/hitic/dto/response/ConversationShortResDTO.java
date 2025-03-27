package br.com.hitic.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConversationShortResDTO {
	private Long id;
	private String name;
	private LocalDateTime createdAt;
}
