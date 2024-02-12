package br.com.rponte.rinhadev.transacoes;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record ExtratoResponse(
        SaldoResponse saldo,
        List<TransacaoResponse> ultimasTransacoes
) {

    public static ExtratoResponse of(Cliente cliente, List<Transacao> transacoes) {
        return new ExtratoResponse(
                new SaldoResponse(
                        cliente.getSaldo(),
                        cliente.getLimite(),
                        LocalDateTime.now()
                ),
                transacoes.stream().map((t) -> new TransacaoResponse(
                        t.getValor(),
                        t.getTipo().getSigla(),
                        t.getDescricao(),
                        t.getRealizadaEm()
                )).toList()
        );
    }

    private record SaldoResponse(
            Long total,
            Long limite,
            @JsonProperty("data_extrato") LocalDateTime dataDoExtrato
    ) {}

    private record TransacaoResponse(
            Long valor,
            String tipo,
            String descricao,
            LocalDateTime realizadaEm
    ) {}

}


