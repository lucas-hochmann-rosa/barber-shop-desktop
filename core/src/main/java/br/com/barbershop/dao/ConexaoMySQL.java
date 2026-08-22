package br.com.barbershop.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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

            String jdbcUrl = getenvOr(props.getProperty("db.url"), "DB_URL");
            String dbUser = getenvOr(props.getProperty("db.user"), "DB_USER");
            String dbPassword = getenvOr(props.getProperty("db.password"), "DB_PASSWORD");
            String dbDriver = getenvOr(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"), "DB_DRIVER");

            // Garante que o schema/database exista no MySQL antes de abrir o pool de conexões
            garantirDatabaseExiste(jdbcUrl, dbUser, dbPassword, dbDriver);

            // Permite sobrescrever via variáveis de ambiente (boa prática para não versionar senha)
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPassword);
            config.setDriverClassName(dbDriver);

            // Dimensionado para o sistema operacional: pool enxuto e seguro
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(1);
            config.setPoolName("BarbershopPool");

            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar config.properties", e);
        }
    }

    /**
     * Conecta diretamente ao servidor MySQL e cria o banco/schema caso ainda não exista.
     */
    private static void garantirDatabaseExiste(String jdbcUrl, String user, String password, String driver) {
        if (jdbcUrl == null || !jdbcUrl.contains("://")) {
            return;
        }
        try {
            Class.forName(driver);
            int barraIdx = jdbcUrl.indexOf("://");
            int slashAfterHost = jdbcUrl.indexOf('/', barraIdx + 3);
            if (slashAfterHost > 0) {
                int queryIdx = jdbcUrl.indexOf('?', slashAfterHost);
                String dbName = queryIdx > 0
                        ? jdbcUrl.substring(slashAfterHost + 1, queryIdx)
                        : jdbcUrl.substring(slashAfterHost + 1);

                if (!dbName.trim().isEmpty()) {
                    String serverUrl = queryIdx > 0
                            ? jdbcUrl.substring(0, slashAfterHost + 1) + jdbcUrl.substring(queryIdx)
                            : jdbcUrl.substring(0, slashAfterHost + 1);

                    try (Connection conn = DriverManager.getConnection(serverUrl, user, password);
                         Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                    }
                }
            }
        } catch (Exception ignored) {
            // Em caso de falha de conexão root ou ambiente restrito, segue para a inicialização normal do Hikari
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
     * @return uma conexão ativa, pronta para uso
     * @throws SQLException se não for possível obter uma conexão do pool
     */
    public static Connection getConexao() throws SQLException {
        return dataSource.getConnection();
    }
}
