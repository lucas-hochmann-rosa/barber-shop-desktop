package br.com.barberdesk.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de {@link HashUtil}: determinismo e unicidade do hash com salt
 * (PBKDF2), geração de salt e o hash legado sem salt (SHA-256).
 */
class HashUtilTest {

    /** Mesma senha + mesmo salt sempre produzem o mesmo hash (necessário pra poder autenticar depois). */
    @Test
    void hashComSaltEhDeterministicoParaMesmoSalt() {
        String salt = HashUtil.gerarSalt();
        String hash1 = HashUtil.hashComSalt("minhaSenha123", salt);
        String hash2 = HashUtil.hashComSalt("minhaSenha123", salt);
        assertEquals(hash1, hash2);
    }

    /** Mesma senha com salts diferentes produz hashes diferentes (é essa a função do salt). */
    @Test
    void hashComSaltDiferenteParaSaltsDiferentes() {
        String hash1 = HashUtil.hashComSalt("minhaSenha123", HashUtil.gerarSalt());
        String hash2 = HashUtil.hashComSalt("minhaSenha123", HashUtil.gerarSalt());
        assertNotEquals(hash1, hash2);
    }

    /** Senhas diferentes com o mesmo salt produzem hashes diferentes. */
    @Test
    void hashComSaltDiferenteParaSenhasDiferentes() {
        String salt = HashUtil.gerarSalt();
        String hash1 = HashUtil.hashComSalt("senhaA", salt);
        String hash2 = HashUtil.hashComSalt("senhaB", salt);
        assertNotEquals(hash1, hash2);
    }

    /** Cada chamada gera um salt novo e aleatório, de 16 bytes (32 caracteres em hex). */
    @Test
    void gerarSaltProduzValoresUnicos() {
        String salt1 = HashUtil.gerarSalt();
        String salt2 = HashUtil.gerarSalt();
        assertNotEquals(salt1, salt2);
        assertEquals(32, salt1.length()); // 16 bytes em hex = 32 caracteres
    }

    /** O hash legado (contas antigas, sem salt) também é determinístico — SHA-256 puro. */
    @Test
    void hashSHA256EhDeterministicoESemSalt() {
        String hash1 = HashUtil.hashSHA256("minhaSenha123");
        String hash2 = HashUtil.hashSHA256("minhaSenha123");
        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length()); // SHA-256 = 32 bytes em hex = 64 caracteres
    }
}
