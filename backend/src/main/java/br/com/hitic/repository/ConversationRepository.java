package br.com.hitic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hitic.model.Conversation;
import br.com.hitic.model.Operator;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

	List<Conversation> findByOperator(Operator operator);
}
