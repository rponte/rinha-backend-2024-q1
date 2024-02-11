package br.com.rponte.rinhadev.transacoes;

import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long saldo;
    private Long limite;

    @Deprecated
    public Cliente() {}

    public Cliente(Long saldo, Long limite) {
        this.saldo = saldo;
        this.limite = limite;
    }

    public Long getId() {
        return id;
    }
    public Long getSaldo() {
        return saldo;
    }
    public Long getLimite() {
        return limite;
    }

    public Long debita(Long valor) {
        if ((saldo - valor) < limite*(-1)) {
            throw new SaldoInsuficienteException("saldo da conta insuficiente: " + saldo);
        }
        saldo = saldo - valor;
        return saldo;
    }

    public Long credita(Long valor) {
        saldo = saldo + valor;
        return saldo;
    }

}
