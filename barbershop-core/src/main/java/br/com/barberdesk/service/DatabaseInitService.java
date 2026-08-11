package br.com.barberdesk.service;

import br.com.barberdesk.dao.SchemaInitializer;

import java.sql.SQLException;

/**
 * Garante que o schema do banco exista e esteja migrado antes da aplicação
 * começar a usar o banco - evita o cenário "abro o sistema e ele parece
 * vazio" porque o banco não foi importado na máquina do avaliador.
 *
 * A execução em si (DDL/DML de infraestrutura: criar tabelas, aplicar
 * migrações) vive em {@link SchemaInitializer}; este service só expõe o
 * ponto de entrada esperado pelo restante da aplicação.
 */
public class DatabaseInitService {

    private final SchemaInitializer schemaInitializer;

    public DatabaseInitService(SchemaInitializer schemaInitializer) {
        this.schemaInitializer = schemaInitializer;
    }

    /**
     * Garante que o schema do banco exista e esteja atualizado.
     *
     * @throws SQLException em caso de falha de acesso ao banco de dados
     */
    public void ensureSchema() throws SQLException {
        schemaInitializer.ensureSchema();
    }
}
