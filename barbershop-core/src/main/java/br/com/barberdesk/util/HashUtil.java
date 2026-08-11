package br.com.barberdesk.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Utilitário de hash de senha da aplicação.
 *
 * O algoritmo atual é PBKDF2WithHmacSHA256 com salt aleatório por usuário
 * (120.000 iterações, chave de 256 bits), usado para toda senha nova. O
 * método {@link #hashSHA256(String)} (SHA-256 puro, sem salt) é mantido
 * apenas para compatibilidade com contas antigas criadas antes da migração
 * para PBKDF2 - não deve ser usado para senhas novas.
 */
public class HashUtil {

    private static final int PBKDF2_ITERACOES = 120_000;
    private static final int PBKDF2_TAMANHO_CHAVE_BITS = 256;

    /** Salt aleatório de 16 bytes, em hexadecimal - um por usuário. */
    public static String gerarSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return bytesParaHex(salt);
    }

    /** PBKDF2-HMAC-SHA256 com salt. Usar para toda senha nova. */
    public static String hashComSalt(String senha, String saltHex) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), hexParaBytes(saltHex),
                    PBKDF2_ITERACOES, PBKDF2_TAMANHO_CHAVE_BITS);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return bytesParaHex(factory.generateSecret(spec).getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Erro ao gerar hash de senha", e);
        }
    }

    /**
     * SHA-256 sem salt - mantido só para autenticar contas criadas antes da
     * migração para hash com salt (ver AuthService.autenticar, que faz upgrade
     * silencioso pra PBKDF2 no primeiro login bem-sucedido). Não usar para
     * senha nova.
     */
    public static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return bytesParaHex(digest.digest(input.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao fazer hash SHA-256", e);
        }
    }

    private static String bytesParaHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] hexParaBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
