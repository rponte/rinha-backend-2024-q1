package br.com.rponte.rinhadev.transacoes;

import base.SpringBootIntegrationTest;
import br.com.rponte.rinhadev.transacoes.domain.Cliente;
import br.com.rponte.rinhadev.transacoes.domain.ClienteRepository;
import br.com.rponte.rinhadev.transacoes.domain.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NovaTransacaoControllerTest extends SpringBootIntegrationTest {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private TransacaoRepository transacaoRepository;

    private static Cliente ZAN;
    private static Cliente RAFAEL;

    @BeforeEach
    public void setUp() {
        transacaoRepository.deleteAll();
        clienteRepository.deleteAll();
        clienteRepository.saveAll(List.of(
                ZAN = new Cliente("Zan", 0L, 1000L),
                RAFAEL = new Cliente("Rafael Ponte", 0L, 1000L)
        ));
    }

    @Test
    @DisplayName("deve processar transação de credito")
    public void t1() throws Exception {
        // cenário
        Long clienteId = ZAN.getId();
        NovaTransacaoRequest request = new NovaTransacaoRequest(400L, "c", "pix");

        // ação (+validação)
        mockMvc.perform(post("/clientes/{id}/transacoes", clienteId)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(400))
                .andExpect(jsonPath("$.limite").value(1000))
        ;

        // validação
        assertAll("ZAN: saldo e transacoes",
                () -> assertEquals(400, clienteRepository.getSaldo(ZAN.getId()), "saldo atual"),
                () -> assertEquals(1, transacaoRepository.countByClienteId(ZAN.getId()), "numero de transações")
        );
        assertAll("RAFAEL: saldo e transacoes",
                () -> assertEquals(0, clienteRepository.getSaldo(RAFAEL.getId()), "saldo atual"),
                () -> assertEquals(0, transacaoRepository.countByClienteId(RAFAEL.getId()), "numero de transações")
        );
    }

    @Test
    @DisplayName("deve processar transação de debito")
    public void t2() throws Exception {
        // cenário
        Long clienteId = ZAN.getId();
        NovaTransacaoRequest request = new NovaTransacaoRequest(300L, "d", "pix");

        // ação (+validação)
        mockMvc.perform(post("/clientes/{id}/transacoes", clienteId)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(-300))
                .andExpect(jsonPath("$.limite").value(1000))
        ;

        // validação
        assertAll("ZAN: saldo e transacoes",
                () -> assertEquals(-300, clienteRepository.getSaldo(ZAN.getId()), "saldo atual"),
                () -> assertEquals(1, transacaoRepository.countByClienteId(ZAN.getId()), "numero de transações")
        );
        assertAll("RAFAEL: saldo e transacoes",
                () -> assertEquals(0, clienteRepository.getSaldo(RAFAEL.getId()), "saldo atual"),
                () -> assertEquals(0, transacaoRepository.countByClienteId(RAFAEL.getId()), "numero de transações")
        );
    }

    @Test
    @DisplayName("deve processar transação de debito até o limite da conta")
    public void t3() throws Exception {
        // cenário
        Long clienteId = ZAN.getId();
        NovaTransacaoRequest request = new NovaTransacaoRequest(ZAN.getLimite(), "d", "pix");

        // ação (+validação)
        mockMvc.perform(post("/clientes/{id}/transacoes", clienteId)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(-1000))
                .andExpect(jsonPath("$.limite").value(1000))
        ;

        // validação
        assertAll("ZAN: saldo e transacoes",
                () -> assertEquals(-1000, clienteRepository.getSaldo(ZAN.getId()), "saldo atual"),
                () -> assertEquals(1, transacaoRepository.countByClienteId(ZAN.getId()), "numero de transações")
        );
        assertAll("RAFAEL: saldo e transacoes",
                () -> assertEquals(0, clienteRepository.getSaldo(RAFAEL.getId()), "saldo atual"),
                () -> assertEquals(0, transacaoRepository.countByClienteId(RAFAEL.getId()), "numero de transações")
        );
    }

    /**
     * Testes de integração necessários para garantir que não há race-condition (lost update)
     * ao processar transações em ambiente de alta-concorrência <br/><br/>
     * <p>
     * ⭐️ Para entender mais sobre o tema, assista a talk
     * "<b>Por que testes de unidade NÃO SÃO SUFICIENTES para seus microsserviços</b>"<br/>
     * https://youtu.be/ZV4Fl1uEbqw?si=PGDoPqkRvpR3MDhK
     */
    @Nested
    @DisplayName("⭐️ | Ambiente de alta-concorrência (Race conditions)")
    class t4 {

        private static final int NUMBER_OF_USERS = 10;

        @Test
        @DisplayName("🥳 | deve processar transação de credito com alta-concorrência")
        public void t4a() throws Exception {
            // cenário
            Long clienteId = ZAN.getId();
            NovaTransacaoRequest request = new NovaTransacaoRequest(200L, "c", "pix");

            // ação
            doSyncAndConcurrently(NUMBER_OF_USERS, s -> {
                mockMvc.perform(post("/clientes/{id}/transacoes", clienteId)
                                .contentType(APPLICATION_JSON)
                                .content(toJson(request))
                                .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                        .andExpect(status().isOk())
                ;
            });

            // validação
            assertAll("ZAN: saldo e transacoes",
                    () -> assertEquals(2000, clienteRepository.getSaldo(ZAN.getId()), "saldo atual"),
                    () -> assertEquals(10, transacaoRepository.countByClienteId(ZAN.getId()), "numero de transações")
            );
        }

        @Test
        @DisplayName("🥳 | deve processar transação de debito até o limite da conta com alta-concorrência")
        public void t4b() throws Exception {
            // cenário
            Long clienteId = ZAN.getId();
            NovaTransacaoRequest request = new NovaTransacaoRequest(200L, "d", "pix");

            // ação
            doSyncAndConcurrently(NUMBER_OF_USERS, s -> {
                mockMvc.perform(post("/clientes/{id}/transacoes", clienteId)
                                .contentType(APPLICATION_JSON)
                                .content(toJson(request))
                                .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                        .andExpect(status().isOk())
                ;
            });

            // validação
            assertAll("ZAN: saldo e transacoes",
                    () -> assertEquals(-1000, clienteRepository.getSaldo(ZAN.getId()), "saldo atual"),
                    () -> assertEquals(5, transacaoRepository.countByClienteId(ZAN.getId()), "numero de transações")
            );
        }

    }

    @Test
    @DisplayName("não deve processar transação de debito além do limite da conta")
    public void t5() throws Exception {
        // cenário
        Long clienteId = ZAN.getId();
        NovaTransacaoRequest request = new NovaTransacaoRequest(ZAN.getLimite()+1, "d", "pix");

        // ação (+validação)
        mockMvc.perform(post("/clientes/{id}/transacoes", clienteId)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail", is("saldo da conta insuficiente")))
        ;

        // validação
        assertAll("ZAN: saldo e transacoes",
                () -> assertEquals(0, clienteRepository.getSaldo(ZAN.getId()), "saldo atual"),
                () -> assertEquals(0, transacaoRepository.countByClienteId(ZAN.getId()), "numero de transações")
        );
    }

    @Test
    @DisplayName("não deve processar transação quando cliente não encontrado")
    public void t6() throws Exception {
        // cenário
        Long clienteInexistenteId = -9999L;
        NovaTransacaoRequest request = new NovaTransacaoRequest(100L, "d", "pix");

        // ação (+validação)
        mockMvc.perform(post("/clientes/{id}/transacoes", clienteInexistenteId)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", is("cliente não encontrado")))
        ;

        // validação
        assertEquals(0, transacaoRepository.count(), "numero de transações");
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "a", "Zan"})
    @DisplayName("não deve processar transação quando cliente invalido")
    public void t7(String clienteInvalidoId) throws Exception {
        // cenário
        NovaTransacaoRequest request = new NovaTransacaoRequest(100L, "d", "pix");

        // ação (+validação)
        mockMvc.perform(post("/clientes/{id}/transacoes", clienteInvalidoId)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isBadRequest())
        ;

        // validação
        assertEquals(0, transacaoRepository.count(), "numero de transações");
    }

    /**
     * Testes de granularidade fina estão na classe {@link NovaTransacaoRequestTest}
     */
    @Test
    @DisplayName("não deve processar transação quando dados invalidos")
    public void t8() throws Exception {
        // cenário
        Long clienteId = ZAN.getId();
        NovaTransacaoRequest request = new NovaTransacaoRequest(-1L, "x", "a".repeat(11));

        // ação (+validação)
        mockMvc.perform(post("/clientes/{id}/transacoes", clienteId)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isBadRequest())
        ;

        // validação
        assertEquals(0, transacaoRepository.count(), "numero de transações");
    }

}