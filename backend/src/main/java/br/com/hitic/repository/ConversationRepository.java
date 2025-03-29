package br.com.hitic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.hitic.model.Conversation;
import br.com.hitic.model.Operator;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

	List<Conversation> findByOperator(Operator operator);

	@Query("""
			    SELECT c FROM Conversation c
			    JOIN Message m ON m.conversation.id = c.id
			    WHERE c.operator.id = :operatorId
			    GROUP BY c
			    ORDER BY MAX(m.createdAt) DESC
			""")
	List<Conversation> findConversationsByOperatorOrderByLastMessageDesc(@Param("operatorId") Long operatorId);

}
