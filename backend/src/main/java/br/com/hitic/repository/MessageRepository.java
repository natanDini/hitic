package br.com.hitic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.hitic.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
