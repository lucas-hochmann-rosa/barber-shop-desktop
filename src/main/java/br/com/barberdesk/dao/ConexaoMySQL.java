package br.com.barberdesk.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexaoMySQL {
    private static String url;
    private static String user;
    private static String password;
    private static String driver;

    static {
        try {
            carregarConfiguracao();
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL não encontrado", e);
        }
    }

    private static void carregarConfiguracao() {
        Properties props = new Properties();
        try (InputStream input = ConexaoMySQL.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Arquivo config.properties não encontrado");
            }
            props.load(input);
            // Permite sobrescrever via variáveis de ambiente (boa prática para não versionar senha)
            url = getenvOr(props.getProperty("db.url"), "DB_URL");
            user = getenvOr(props.getProperty("db.user"), "DB_USER");
            password = getenvOr(props.getProperty("db.password"), "DB_PASSWORD");
            driver = getenvOr(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"), "DB_DRIVER");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar config.properties", e);
        }
    }

    private static String getenvOr(String fallback, String envKey) {
        String v = System.getenv(envKey);
        return (v != null && !v.trim().isEmpty()) ? v.trim() : fallback;
    }

    public static Connection getConexao() throws SQLException {
        // Retorna uma NOVA conexão a cada chamada.
        // Isso evita bugs de "conexão compartilhada" (fechada por outra tela/DAO) e é mais seguro.
        return DriverManager.getConnection(url, user, password);
    }
}
