package br.com.rponte.rinhadev.transacoes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class ExtratoController {

    @Autowired
    private TransacaoRepository repository;
    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    @GetMapping("/clientes/{id}/extrato")
    public ResponseEntity<?> imprime(@PathVariable Long id) {

        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> {
            return new ResponseStatusException(NOT_FOUND, "cliente não encontrado");
        });

        List<Transacao> ultimasTransacoes = repository.findTop10ByClienteIdOrderByRealizadaEmDesc(id);
        return ResponseEntity.ok(
                ExtratoResponse.of(cliente, ultimasTransacoes)
        );
    }
}
