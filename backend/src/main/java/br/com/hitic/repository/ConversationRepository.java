package br.com.hitic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hitic.model.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
