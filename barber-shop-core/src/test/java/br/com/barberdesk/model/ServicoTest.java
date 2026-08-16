package br.com.barberdesk.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de {@link Servico}: equals()/hashCode() por id, toString() e o
 * valor padrão de duração usado quando o construtor vazio é chamado.
 */
class ServicoTest {

    /** equals()/hashCode() comparam só o id - o resto do objeto pode divergir. */
    @Test
    void servicosComMesmoIdSaoIguaisMesmoComOutrosCamposDiferentes() {
        Servico a = new Servico(1, 10, "Corte", new BigDecimal("30.00"), null, 30);
        Servico b = new Servico(1, 10, "Corte (editado)", new BigDecimal("35.00"), "foto.png", 45);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    /** Ids diferentes nunca são iguais, mesmo com os demais campos idênticos. */
    @Test
    void servicosComIdsDiferentesNaoSaoIguais() {
        Servico a = new Servico(1, 10, "Corte", new BigDecimal("30.00"), null, 30);
        Servico b = new Servico(2, 10, "Corte", new BigDecimal("30.00"), null, 30);
        assertNotEquals(a, b);
    }

    /** toString() devolve o nome do serviço - usado pelo JComboBox default renderer. */
    @Test
    void toStringRetornaONome() {
        Servico s = new Servico(1, 10, "Barba", new BigDecimal("20.00"), null, 20);
        assertEquals("Barba", s.toString());
    }

    /** O construtor sem argumentos (novo cadastro) já nasce com 30 minutos de duração. */
    @Test
    void duracaoPadraoEhTrintaMinutos() {
        Servico s = new Servico();
        assertEquals(30, s.getDuracaoMinutos());
    }
}
