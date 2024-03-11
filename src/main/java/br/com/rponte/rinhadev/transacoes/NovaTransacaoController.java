package br.com.rponte.rinhadev.transacoes;

import br.com.rponte.rinhadev.transacoes.domain.*;
import jakarta.validation.Valid;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

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
            default      -> {
                throw new ResponseStatusException(UNPROCESSABLE_ENTITY, "operação inválida: " + transacao.getTipo());
            }
        };

        repository.save(transacao);

        return ResponseEntity.ok(
                new NovaTransacaoResponse(cliente.getSaldo(), cliente.getLimite())
        );
    }

    /**
     * Handles database CHECK constraint error
     */
    @ExceptionHandler({
            SaldoInsuficienteException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<?> handleCheckConstraintError(RuntimeException e, WebRequest request) {

        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(UNPROCESSABLE_ENTITY, "saldo da conta insuficiente");

        return ResponseEntity
                .unprocessableEntity().body(problem); // HTTP 422
    }
}
