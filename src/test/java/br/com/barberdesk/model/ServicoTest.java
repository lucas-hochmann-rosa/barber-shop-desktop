package br.com.barberdesk.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ServicoTest {

    @Test
    void servicosComMesmoIdSaoIguaisMesmoComOutrosCamposDiferentes() {
        Servico a = new Servico(1, 10, "Corte", new BigDecimal("30.00"), null, 30);
        Servico b = new Servico(1, 10, "Corte (editado)", new BigDecimal("35.00"), "foto.png", 45);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void servicosComIdsDiferentesNaoSaoIguais() {
        Servico a = new Servico(1, 10, "Corte", new BigDecimal("30.00"), null, 30);
        Servico b = new Servico(2, 10, "Corte", new BigDecimal("30.00"), null, 30);
        assertNotEquals(a, b);
    }

    @Test
    void toStringRetornaONome() {
        Servico s = new Servico(1, 10, "Barba", new BigDecimal("20.00"), null, 20);
        assertEquals("Barba", s.toString());
    }

    @Test
    void duracaoPadraoEhTrintaMinutos() {
        Servico s = new Servico();
        assertEquals(30, s.getDuracaoMinutos());
    }
}
