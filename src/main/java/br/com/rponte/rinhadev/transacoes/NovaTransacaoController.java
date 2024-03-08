package br.com.rponte.rinhadev.transacoes;

import br.com.rponte.rinhadev.transacoes.domain.Cliente;
import br.com.rponte.rinhadev.transacoes.domain.ClienteRepository;
import br.com.rponte.rinhadev.transacoes.domain.Transacao;
import br.com.rponte.rinhadev.transacoes.domain.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class NovaTransacaoController {

    @Autowired
    private TransacaoRepository repository;
    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    @PostMapping("/clientes/{id}/transacoes")
    public ResponseEntity<?> processa(
            @PathVariable("id") Long clienteId,
            @Valid @RequestBody NovaTransacaoRequest request) {

        Cliente cliente = clienteRepository.findByIdWithPessimisticLocking(clienteId).orElseThrow(() -> {
            return new ResponseStatusException(NOT_FOUND, "cliente não encontrado");
        });

        Transacao transacao = request.toModel(cliente);
        switch (transacao.getTipo()) {
            case DEBITO  -> cliente.debita(transacao.getValor());
            case CREDITO -> cliente.credita(transacao.getValor());
        };

        repository.save(transacao);

        return ResponseEntity.ok(
                new NovaTransacaoResponse(cliente.getSaldo(), cliente.getLimite())
        );
    }
}
