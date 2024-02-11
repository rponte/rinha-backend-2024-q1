package br.com.rponte.rinhadev.transacoes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cliente c where c.id = :clienteId")
    public Optional<Cliente> findByIdWithPessimisticLocking(Long clienteId);

    @Query("""
           select c.saldo
             from Cliente c
            where c.id = :clienteId
           """)
    public Long getSaldo(Long clienteId);

}
