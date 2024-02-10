package br.com.rponte.rinhadev.samples.books;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Since most repository calls are for read operations, it's good practice
 * to define, at class level, that transactions are read-only by default.
 * 
 * Reference: https://vladmihalcea.com/spring-transaction-best-practices/
 */
@Repository
@Transactional(readOnly = true)
public interface BookRepository extends JpaRepository<Book, Long> {

    public Optional<Book> findByIsbn(String isbn);
}