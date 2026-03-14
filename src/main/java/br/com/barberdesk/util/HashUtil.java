package br.com.barberdesk.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Hash sem salt por escopo do projeto atual (uso local/rede interna, sem exposição
// pública). Para um cenário exposto à internet, trocar por BCrypt/PBKDF2 com salt
// por usuário antes de reaproveitar este código.
public class HashUtil {
    public static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao fazer hash SHA-256", e);
        }
    }
}
