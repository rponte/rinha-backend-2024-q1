package br.com.rponte.rinhadev.transacoes;

import br.com.rponte.rinhadev.transacoes.domain.Cliente;
import br.com.rponte.rinhadev.transacoes.domain.ClienteRepository;
import br.com.rponte.rinhadev.transacoes.domain.Transacao;
import br.com.rponte.rinhadev.transacoes.domain.TransacaoRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import javax.validation.Valid;
import java.net.URI;

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
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleCheckConstraintError(ConstraintViolationException ex, WebRequest request) {

        Status status = Status.UNPROCESSABLE_ENTITY;
        URI type = URI.create(request.getDescription(false).replace("uri=", ""));

        ThrowableProblem problem = Problem.builder()
                .withType(type)
                .withStatus(status)
                .withTitle(status.getReasonPhrase())
                .withDetail("saldo da conta insuficiente")
                .build();

        return ResponseEntity
                .unprocessableEntity().body(problem); // HTTP 422
    }
}
