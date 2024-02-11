package br.com.rponte.rinhadev.transacoes;

import javax.validation.constraints.*;

public record NovaTransacaoRequest(
        @NotNull @Positive Long valor,
        @NotBlank @Size(min = 1, max = 1) @Pattern(regexp = "c|d") String tipo,
        @NotBlank @Size(min = 1, max = 10) String descricao
) {

    public Transacao toModel(Cliente cliente) {
        return new Transacao(
                this.valor, TipoDeTransacao.ofSigla(this.tipo),
                this.descricao, cliente
        );
    }

}
