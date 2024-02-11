package br.com.rponte.rinhadev.transacoes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    public int countByClienteId(Long clienteId);

}
