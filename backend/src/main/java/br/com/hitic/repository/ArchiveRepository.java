package br.com.hitic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hitic.model.Archive;

@Repository
public interface ArchiveRepository extends JpaRepository<Archive, Long> {

}
