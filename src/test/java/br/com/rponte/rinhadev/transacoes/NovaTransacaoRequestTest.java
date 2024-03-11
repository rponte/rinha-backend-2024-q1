package br.com.rponte.rinhadev.transacoes;

import base.SpringBootIntegrationTest;
import br.com.rponte.rinhadev.transacoes.domain.Cliente;
import br.com.rponte.rinhadev.transacoes.domain.TipoDeTransacao;
import br.com.rponte.rinhadev.transacoes.domain.Transacao;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NovaTransacaoRequestTest extends SpringBootIntegrationTest {

    @Autowired
    private Validator validator;

    @Nested
    @DisplayName("validação dos dados de entrada")
    class validation {

        @Test
        @DisplayName("deve ser valida")
        void t1() {
            // cenário
            NovaTransacaoRequest debito  = new NovaTransacaoRequest(1L, "d", "d".repeat(10));
            NovaTransacaoRequest credito = new NovaTransacaoRequest(1L, "c", "c".repeat(10));

            // ação e validação
            assertThat(validator.validate(debito)).isEmpty();
            assertThat(validator.validate(credito)).isEmpty();
        }

        @Test
        @DisplayName("não deve ser valida quando valores nulos")
        void t2() {
            // cenário
            NovaTransacaoRequest request = new NovaTransacaoRequest(null, null, null);

            // ação
            Set<ConstraintViolation<NovaTransacaoRequest>> constraints = validator.validate(request);

            // validação
            assertConstraintErrors(constraints,
                    tuple("valor", "must not be null"),
                    tuple("tipo", "must not be blank"),
                    tuple("descricao", "must not be blank")
            );
        }

        @Test
        @DisplayName("não deve ser valida quando valores invalidos: positive, not-blank, size e regex")
        void t3() {
            // cenário
            NovaTransacaoRequest request = new NovaTransacaoRequest(
                    0L,
                    " ".repeat(2),
                    " ".repeat(11)
            );

            // ação
            Set<ConstraintViolation<NovaTransacaoRequest>> constraints = validator.validate(request);

            // validação
            assertConstraintErrors(constraints,
                    tuple("valor", "must be greater than 0"),
                    tuple("tipo", "must not be blank"),
                    tuple("tipo", "must match \"c|d\""),
                    tuple("tipo", "size must be between 1 and 1"),
                    tuple("descricao", "must not be blank"),
                    tuple("descricao", "size must be between 1 and 10")
            );
        }

        private <T> void assertConstraintErrors(Set<ConstraintViolation<T>> constraints, Tuple...tuples) {
            assertThat(constraints)
                    .hasSize(tuples.length)
                    .extracting(
                            t -> t.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    )
                    .containsExactlyInAnyOrder(tuples)
            ;
        }
    }


    @Nested
    @DisplayName("conversão para domain model")
    class conversion {
        @Test
        @DisplayName("deve converter para modelo de dominio")
        void t4() {
            // cenário
            Cliente zan = new Cliente("zan", 0L, 1000L);
            NovaTransacaoRequest request  = new NovaTransacaoRequest(15000L, "c", "pix");

            // ação
            Transacao domain = request.toModel(zan);

            // validação
            assertThat(domain)
                    .usingRecursiveComparison()
                    .ignoringFields("realizadaEm")
                    .isEqualTo(
                            new Transacao(15000L, TipoDeTransacao.CREDITO, "pix", zan)
                    );
        }

        @Test
        @DisplayName("não deve converter para modelo de dominio quando cliente não informado")
        void t5() {
            // cenário
            Cliente invalido = null;
            NovaTransacaoRequest request  = new NovaTransacaoRequest(15000L, "c", "pix");

            // ação e validação
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                request.toModel(invalido);
            });

            assertThat(exception)
                    .hasMessage("cliente informado não pode ser nulo");
        }
    }

}