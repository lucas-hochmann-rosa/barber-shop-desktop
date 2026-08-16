package br.com.barberdesk.model;

/**
 * Representa um profissional (barbeiro) que atende na barbearia, disponível para
 * seleção ao criar um {@link Agendamento}.
 */
public class Barbeiro {
    private int id;
    private int barbeariaId;
    private String nome;
    // Foto do barbeiro codificada em Base64, salva direto no banco (sem arquivo em disco).
    private String imagemBase64;

    public Barbeiro() {
    }

    public Barbeiro(int barbeariaId, String nome, String imagemBase64) {
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.imagemBase64 = imagemBase64;
    }

    public Barbeiro(int id, int barbeariaId, String nome, String imagemBase64) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.imagemBase64 = imagemBase64;
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

    public String getImagemBase64() {
        return imagemBase64;
    }

    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
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

    // Igualdade por id: permite JComboBox.setSelectedItem(barbeiro) encontrar o
    // item certo mesmo quando é uma instância diferente da carregada no combo.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Barbeiro)) return false;
        return id == ((Barbeiro) o).id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
