package br.com.barberdesk.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashUtilTest {

    @Test
    void hashComSaltEhDeterministicoParaMesmoSalt() {
        String salt = HashUtil.gerarSalt();
        String hash1 = HashUtil.hashComSalt("minhaSenha123", salt);
        String hash2 = HashUtil.hashComSalt("minhaSenha123", salt);
        assertEquals(hash1, hash2);
    }

    @Test
    void hashComSaltDiferenteParaSaltsDiferentes() {
        String hash1 = HashUtil.hashComSalt("minhaSenha123", HashUtil.gerarSalt());
        String hash2 = HashUtil.hashComSalt("minhaSenha123", HashUtil.gerarSalt());
        assertNotEquals(hash1, hash2);
    }

    @Test
    void hashComSaltDiferenteParaSenhasDiferentes() {
        String salt = HashUtil.gerarSalt();
        String hash1 = HashUtil.hashComSalt("senhaA", salt);
        String hash2 = HashUtil.hashComSalt("senhaB", salt);
        assertNotEquals(hash1, hash2);
    }

    @Test
    void gerarSaltProduzValoresUnicos() {
        String salt1 = HashUtil.gerarSalt();
        String salt2 = HashUtil.gerarSalt();
        assertNotEquals(salt1, salt2);
        assertEquals(32, salt1.length()); // 16 bytes em hex = 32 caracteres
    }

    @Test
    void hashSHA256EhDeterministicoESemSalt() {
        String hash1 = HashUtil.hashSHA256("minhaSenha123");
        String hash2 = HashUtil.hashSHA256("minhaSenha123");
        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length()); // SHA-256 = 32 bytes em hex = 64 caracteres
    }
}
