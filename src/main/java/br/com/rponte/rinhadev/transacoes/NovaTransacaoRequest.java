package br.com.rponte.rinhadev.transacoes;

import br.com.rponte.rinhadev.transacoes.domain.Cliente;
import br.com.rponte.rinhadev.transacoes.domain.TipoDeTransacao;
import br.com.rponte.rinhadev.transacoes.domain.Transacao;
import jakarta.validation.constraints.*;


public record NovaTransacaoRequest(
        @NotNull @Positive Long valor,
        @NotBlank @Size(min = 1, max = 1) @Pattern(regexp = "c|d") String tipo,
        @NotBlank @Size(min = 1, max = 10) String descricao
) {

    public Transacao toModel(Cliente cliente) {

        if (cliente == null)
            throw new IllegalArgumentException("cliente informado não pode ser nulo");

        return new Transacao(
                this.valor,
                TipoDeTransacao.ofSigla(this.tipo),
                this.descricao,
                cliente
        );
    }

}
