package br.com.rponte.rinhadev.transacoes;

import org.springframework.util.Assert;

public enum TipoDeTransacao {

    DEBITO("d"),
    CREDITO("c");

    private final String sigla;

    private TipoDeTransacao(String sigla) {
        this.sigla = sigla;
    }

    public String getSigla() {
        return sigla;
    }

    public static TipoDeTransacao ofSigla(String sigla) {
        Assert.notNull(sigla, "sigla não pode ser nula");
        Assert.hasLength(sigla, "sigla não pode ser vazia");

        return switch (sigla) {
            case "d" -> DEBITO;
            case "c" -> CREDITO;
            default -> throw new IllegalArgumentException("sigla não suportada: " + sigla);
        };
    }

}
