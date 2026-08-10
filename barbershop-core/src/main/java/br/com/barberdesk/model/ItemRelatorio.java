package br.com.barberdesk.model;

import java.math.BigDecimal;

/**
 * Linha genérica de um relatório de ranking (serviço mais vendido,
 * barbeiro com mais atendimentos etc.): um nome, uma quantidade de
 * ocorrências e um valor total associado.
 */
public class ItemRelatorio {
    private final String nome;
    private final long quantidade;
    private final BigDecimal total;

    public ItemRelatorio(String nome, long quantidade, BigDecimal total) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.total = total;
    }

    public String getNome() { return nome; }
    public long getQuantidade() { return quantidade; }
    public BigDecimal getTotal() { return total; }
}
