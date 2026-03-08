package br.com.barberdesk.service;

import br.com.barberdesk.dao.ConexaoMySQL;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inicializa o banco caso o schema ainda não exista.
 *
 * Objetivo: evitar o cenário "abro o sistema e ele parece vazio" porque o banco
 * não foi importado na máquina do avaliador.
 */
public class DatabaseInitService {

    public void ensureSchema() throws SQLException {
        boolean jaExiste;
        try (Connection conn = ConexaoMySQL.getConexao()) {
            jaExiste = tabelaExiste(conn, "barbearias");
        }

        // Se não existir, cria estrutura base.
        if (!jaExiste) {
            executarScript("db/schema.sql");
        }

        // Migrações (executadas sempre, são idempotentes e não destrutivas)
        try (Connection conn = ConexaoMySQL.getConexao()) {
            aplicarMigracoes(conn);
        }
    }

    private boolean tabelaExiste(Connection conn, String table) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SHOW TABLES LIKE '" + table + "'");) {
            return rs.next();
        }
    }

    private void executarScript(String resourcePath) throws SQLException {
        InputStream in = DatabaseInitService.class.getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new SQLException("Não foi possível localizar o script do banco: " + resourcePath);
        }

        StringBuilder sql = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.isEmpty()) continue;
                sql.append(line).append('\n');
            }
        } catch (Exception e) {
            throw new SQLException("Erro lendo script do banco", e);
        }

        // Split simples por ';' (suficiente para scripts típicos do seu projeto)
        String[] statements = sql.toString().split(";\\s*\\n");
        try (Connection conn = ConexaoMySQL.getConexao();
             Statement st = conn.createStatement()) {
            for (String s : statements) {
                String stmt = s.trim();
                if (stmt.isEmpty()) continue;
                st.execute(stmt);
            }
        }
    }

    /**
     * Migrações para corrigir problemas práticos do sistema.
     *
     * V2: permitir excluir barbeiros/serviços mesmo que já tenham sido usados em agendamentos,
     *     mantendo um "snapshot" de nomes dentro de agendamentos.
     */
    private void aplicarMigracoes(Connection conn) throws SQLException {
        // Se a tabela de agendamentos não existir ainda, não há o que migrar.
        if (!tabelaExiste(conn, "agendamentos")) return;

        try (Statement st = conn.createStatement()) {
            // 1) adiciona colunas de snapshot (se ainda não existirem)
            // MySQL < 8 não tem IF NOT EXISTS em ADD COLUMN, então usamos try/catch.
            try { st.execute("ALTER TABLE agendamentos ADD COLUMN servico_nome_snapshot VARCHAR(120) NULL"); } catch (SQLException ignored) {}
            try { st.execute("ALTER TABLE agendamentos ADD COLUMN barbeiro_nome_snapshot VARCHAR(120) NULL"); } catch (SQLException ignored) {}

            // 2) torna servico_id e barbeiro_id anuláveis (para cenários futuros)
            try { st.execute("ALTER TABLE agendamentos MODIFY servico_id INT NULL"); } catch (SQLException ignored) {}
            try { st.execute("ALTER TABLE agendamentos MODIFY barbeiro_id INT NULL"); } catch (SQLException ignored) {}

            // 3) remove FKs de servico_id e barbeiro_id (para permitir exclusão sem quebrar histórico)
            dropForeignKeysByColumn(conn, "agendamentos", "servico_id");
            dropForeignKeysByColumn(conn, "agendamentos", "barbeiro_id");

            // 4) backfill do snapshot para registros antigos
            try {
                st.execute(
                    "UPDATE agendamentos a " +
                    "LEFT JOIN servicos s ON a.servico_id = s.id " +
                    "SET a.servico_nome_snapshot = COALESCE(a.servico_nome_snapshot, s.nome) " +
                    "WHERE a.servico_nome_snapshot IS NULL"
                );
            } catch (SQLException ignored) {}

            try {
                st.execute(
                    "UPDATE agendamentos a " +
                    "LEFT JOIN barbeiros b ON a.barbeiro_id = b.id " +
                    "SET a.barbeiro_nome_snapshot = COALESCE(a.barbeiro_nome_snapshot, b.nome) " +
                    "WHERE a.barbeiro_nome_snapshot IS NULL"
                );
            } catch (SQLException ignored) {}
        }
    }

    private void dropForeignKeysByColumn(Connection conn, String table, String column) {
        String q = "SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '" + table + "' " +
                "AND COLUMN_NAME = '" + column + "' AND REFERENCED_TABLE_NAME IS NOT NULL";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(q)) {
            while (rs.next()) {
                String fk = rs.getString(1);
                if (fk == null || fk.trim().isEmpty()) continue;
                try (Statement st2 = conn.createStatement()) {
                    st2.execute("ALTER TABLE " + table + " DROP FOREIGN KEY " + fk);
                } catch (SQLException ignored) {
                    // ignora (já removida ou nome diferente)
                }
            }
        } catch (SQLException ignored) {
            // ignora (sem permissão no information_schema ou não suportado)
        }
    }
}
