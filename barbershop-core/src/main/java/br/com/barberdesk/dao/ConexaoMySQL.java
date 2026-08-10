package br.com.barberdesk.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Responsável por configurar e disponibilizar o acesso ao banco de dados MySQL.
 * Mantém um pool de conexões único (HikariCP), inicializado uma única vez de forma
 * estática a partir de {@code config.properties} (com possibilidade de sobrescrita
 * via variáveis de ambiente). Todos os DAOs do sistema pedem conexões emprestadas
 * a essa classe através de {@link #getConexao()}.
 */
public class ConexaoMySQL {
    private static final HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            try (InputStream input = ConexaoMySQL.class.getClassLoader()
                    .getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new RuntimeException("Arquivo config.properties não encontrado");
                }
                props.load(input);
            }

            // Permite sobrescrever via variáveis de ambiente (boa prática para não versionar senha)
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(getenvOr(props.getProperty("db.url"), "DB_URL"));
            config.setUsername(getenvOr(props.getProperty("db.user"), "DB_USER"));
            config.setPassword(getenvOr(props.getProperty("db.password"), "DB_PASSWORD"));
            config.setDriverClassName(getenvOr(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"), "DB_DRIVER"));

            // Dimensionado para um app desktop de usuário único: não precisa de um pool grande.
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(1);
            config.setPoolName("BarberDeskPool");

            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar config.properties", e);
        }
    }

    /**
     * Resolve um valor de configuração dando prioridade à variável de ambiente
     * {@code envKey}, se ela estiver definida e não vazia; caso contrário, usa o
     * valor lido do arquivo {@code config.properties} como {@code fallback}.
     */
    private static String getenvOr(String fallback, String envKey) {
        String v = System.getenv(envKey);
        return (v != null && !v.trim().isEmpty()) ? v.trim() : fallback;
    }

    /**
     * Empresta uma conexão do pool HikariCP para uso pelos DAOs.
     *
     * Conexão emprestada do pool (HikariCP). O contrato pro resto do código não
     * muda: continua sendo usada em try-with-resources — close() agora devolve
     * a conexão pro pool em vez de fechar de verdade a conexão física.
     *
     * @return uma conexão ativa, pronta para uso
     * @throws SQLException se não for possível obter uma conexão do pool
     */
    public static Connection getConexao() throws SQLException {
        return dataSource.getConnection();
    }
}
