package br.com.rponte.rinhadev.transacoes;

import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String nome;

    @Column(nullable = false)
    private Long saldo;

    @Column(nullable = false)
    private Long limite;

    @Deprecated
    public Cliente() {}

    public Cliente(String nome, Long saldo, Long limite) {
        this.nome = nome;
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
