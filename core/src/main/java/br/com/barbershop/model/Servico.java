package br.com.barbershop.model;

import java.math.BigDecimal;

/**
 * Representa um serviço oferecido pela barbearia (ex.: corte, barba), com preço e duração
 * padrão, disponível para seleção ao criar um {@link Agendamento}.
 */
public class Servico {
    private int id;
    private int barbeariaId;
    private String nome;
    private BigDecimal preco;
    // Foto do serviço codificada em Base64, salva direto no banco (sem arquivo em disco).
    private String imagemBase64;
    private int duracaoMinutos = 30;

    public Servico() {
    }

    public Servico(int barbeariaId, String nome, BigDecimal preco, String imagemBase64) {
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.preco = preco;
        this.imagemBase64 = imagemBase64;
    }

    public Servico(int id, int barbeariaId, String nome, BigDecimal preco, String imagemBase64) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.preco = preco;
        this.imagemBase64 = imagemBase64;
    }

    /**
     * Construtor completo, incluindo a duração - usado ao carregar um serviço já
     * existente do banco (os demais construtores mantêm a duração padrão de 30 min).
     */
    public Servico(int id, int barbeariaId, String nome, BigDecimal preco, String imagemBase64, int duracaoMinutos) {
        this(id, barbeariaId, nome, preco, imagemBase64);
        this.duracaoMinutos = duracaoMinutos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBarbeariaId() {
        return barbeariaId;
    }

    public void setBarbeariaId(int barbeariaId) {
        this.barbeariaId = barbeariaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getImagemBase64() {
        return imagemBase64;
    }

    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    /**
     * Alias legado para {@link #getImagemBase64()}, mantido para compatibilidade com telas
     * que ainda referenciam a foto como "caminho" - na prática retorna a mesma string Base64.
     */
    public String getFotoCaminho() {
        return imagemBase64;
    }

    /**
     * Alias legado para {@link #setImagemBase64(String)}. Ver {@link #getFotoCaminho()}.
     */
    public void setFotoCaminho(String fotoCaminho) {
        this.imagemBase64 = fotoCaminho;
    }

    @Override
    public String toString() {
        return nome;
    }

    // Igualdade por id: permite JComboBox.setSelectedItem(servico) encontrar o
    // item certo mesmo quando é uma instância diferente da carregada no combo.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Servico)) return false;
        return id == ((Servico) o).id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
