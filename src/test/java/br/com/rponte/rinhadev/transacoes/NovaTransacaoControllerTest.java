package br.com.rponte.rinhadev.transacoes;

import base.SpringBootIntegrationTest;
import br.com.rponte.rinhadev.samples.authors.AuthorRespository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class NovaTransacaoControllerTest extends SpringBootIntegrationTest {

    @Autowired
    private TransacaoRepository repository;
    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        clienteRepository.deleteAll();
    }

    

}