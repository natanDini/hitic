package br.com.hitic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.hitic.model.Conversation;
import br.com.hitic.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
	List<Message> findByConversation(Conversation conversation);
}
