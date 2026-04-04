package br.com.barberdesk.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BarbeiroTest {

    @Test
    void barbeirosComMesmoIdSaoIguaisMesmoComOutrosCamposDiferentes() {
        Barbeiro a = new Barbeiro(1, 10, "João", null);
        Barbeiro b = new Barbeiro(1, 10, "João Silva", "foto.png");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void barbeirosComIdsDiferentesNaoSaoIguais() {
        Barbeiro a = new Barbeiro(1, 10, "João", null);
        Barbeiro b = new Barbeiro(2, 10, "João", null);
        assertNotEquals(a, b);
    }

    @Test
    void getFotoCaminhoEhAliasDeImagemPath() {
        Barbeiro b = new Barbeiro(1, 10, "João", "foto.png");
        assertEquals("foto.png", b.getFotoCaminho());
    }
}
