package br.com.rponte.rinhadev.transacoes.domain;


import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;


@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long valor;

    @Enumerated(STRING)
    @Column(nullable = false, updatable = false, length = 10)
    private TipoDeTransacao tipo;

    @Column(nullable = false, updatable = false, length = 10)
    private String descricao;

    @JoinColumn(nullable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cliente cliente;

    @Column(nullable = false, updatable = false)
    private LocalDateTime realizadaEm = LocalDateTime.now();

    @Deprecated
    public Transacao() {}

    public Transacao(Long valor, TipoDeTransacao tipo, String descricao, Cliente cliente) {
        this.valor = valor;
        this.tipo = tipo;
        this.descricao = descricao;
        this.cliente = cliente;
    }

    public Long getId() {
        return id;
    }
    public Long getValor() {
        return valor;
    }
    public TipoDeTransacao getTipo() {
        return tipo;
    }
    public String getDescricao() {
        return descricao;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public LocalDateTime getRealizadaEm() {
        return realizadaEm;
    }

    @Override
    public String toString() {
        return "Transacao{" +
                "id=" + id +
                ", valor=" + valor +
                ", tipo=" + tipo +
                ", descricao='" + descricao + '\'' +
                ", clienteId=" + cliente.getId() + // evita fetch da entidade
                ", realizadaEm=" + realizadaEm +
                '}';
    }
}
