package br.com.rponte.rinhadev.transacoes;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ResponseStatus(
        code = UNPROCESSABLE_ENTITY,
        reason = "saldo da conta insuficiente"
)
public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(String message) {
        super(message);
    }

}
