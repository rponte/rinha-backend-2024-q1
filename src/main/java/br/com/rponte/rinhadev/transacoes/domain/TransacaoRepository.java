package br.com.rponte.rinhadev.transacoes.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    public int countByClienteId(Long clienteId);

    public List<Transacao> findTop10ByClienteIdOrderByRealizadaEmDesc(Long id);
}
