package br.com.barberdesk.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de {@link Barbeiro}: equals()/hashCode() por id (usado nos
 * JComboBox tipados da UI) e o alias getFotoCaminho()/imagemBase64.
 */
class BarbeiroTest {

    /** equals()/hashCode() comparam só o id — o resto do objeto pode divergir. */
    @Test
    void barbeirosComMesmoIdSaoIguaisMesmoComOutrosCamposDiferentes() {
        Barbeiro a = new Barbeiro(1, 10, "João", null);
        Barbeiro b = new Barbeiro(1, 10, "João Silva", "foto.png");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    /** Ids diferentes nunca são iguais, mesmo com os demais campos idênticos. */
    @Test
    void barbeirosComIdsDiferentesNaoSaoIguais() {
        Barbeiro a = new Barbeiro(1, 10, "João", null);
        Barbeiro b = new Barbeiro(2, 10, "João", null);
        assertNotEquals(a, b);
    }

    /** getFotoCaminho() é um alias legado de getImagemBase64(), mantido por compatibilidade. */
    @Test
    void getFotoCaminhoEhAliasDeImagemBase64() {
        Barbeiro b = new Barbeiro(1, 10, "João", "foto.png");
        assertEquals("foto.png", b.getFotoCaminho());
    }
}
