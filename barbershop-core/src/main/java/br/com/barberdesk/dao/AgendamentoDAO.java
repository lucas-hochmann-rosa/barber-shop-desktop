package br.com.barberdesk.dao;

import br.com.barberdesk.dao.repository.AgendamentoRepository;
import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.OrigemContato;
import br.com.barberdesk.model.StatusAgendamento;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a dados da tabela {@code agendamentos}: CRUD, listagens filtradas por
 * barbearia/status e verificação de conflito de horário entre agendamentos de um
 * mesmo barbeiro. Os registros guardam um "snapshot" do nome do serviço, do nome
 * do barbeiro e da duração no momento do agendamento (colunas {@code *_snapshot}),
 * de forma que o histórico continue legível mesmo que o serviço ou o barbeiro
 * originais sejam depois renomeados ou removidos.
 */
public class AgendamentoDAO implements AgendamentoRepository {

    /**
     * Insere um novo agendamento, validando antes que não haja conflito de horário
     * para o barbeiro informado (ver {@link #verificarConflito}). Os nomes de
     * serviço e barbeiro são gravados como snapshot junto com os IDs, para
     * preservar o histórico caso o serviço/barbeiro original mude depois.
     *
     * @param a agendamento a ser inserido
     * @return o ID gerado para o novo agendamento, ou -1 se não foi possível obtê-lo
     * @throws SQLException se houver conflito de horário ou falha de acesso ao banco
     */
    public int inserir(Agendamento a) throws SQLException {
        if (verificarConflito(a.getBarbeiroId(), a.getDataHora(), a.getDuracaoMinutos(), 0)) {
            throw new SQLException("Conflito: este barbeiro já possui um agendamento nesse horário.");
        }

        String sql = "INSERT INTO agendamentos " +
                "(barbearia_id, servico_id, barbeiro_id, servico_nome_snapshot, barbeiro_nome_snapshot, duracao_minutos_snapshot, " +
                " cliente_nome, contato, data_hora, origem_contato, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, a.getBarbeariaId());
            if (a.getServicoId() > 0) stmt.setInt(2, a.getServicoId()); else stmt.setNull(2, Types.INTEGER);
            if (a.getBarbeiroId() > 0) stmt.setInt(3, a.getBarbeiroId()); else stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, a.getServicoNome());
            stmt.setString(5, a.getBarbeiroNome());
            stmt.setInt(6, a.getDuracaoMinutos());

            stmt.setString(7, a.getClienteNome());
            stmt.setString(8, a.getContato());
            stmt.setTimestamp(9, Timestamp.valueOf(a.getDataHora()));
            stmt.setString(10, a.getOrigemContato() != null ? a.getOrigemContato().name() : OrigemContato.OUTRO.name());
            stmt.setString(11, a.getStatus() != null ? a.getStatus().name() : StatusAgendamento.AGENDADO.name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Atualiza os dados de um agendamento existente, revalidando o conflito de
     * horário (excluindo o próprio registro da checagem, já que ele mesmo ocupa
     * aquele horário).
     *
     * @param a agendamento com os dados atualizados (deve conter o ID)
     * @throws SQLException se houver conflito de horário ou falha de acesso ao banco
     */
    public void atualizar(Agendamento a) throws SQLException {
        if (verificarConflito(a.getBarbeiroId(), a.getDataHora(), a.getDuracaoMinutos(), a.getId())) {
            throw new SQLException("Conflito: este barbeiro já possui um agendamento nesse horário.");
        }

        String sql = "UPDATE agendamentos SET " +
                "servico_id=?, barbeiro_id=?, servico_nome_snapshot=?, barbeiro_nome_snapshot=?, duracao_minutos_snapshot=?, " +
                "cliente_nome=?, contato=?, data_hora=?, origem_contato=?, status=?, motivo_cancelamento=? " +
                "WHERE id=?";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (a.getServicoId() > 0) stmt.setInt(1, a.getServicoId()); else stmt.setNull(1, Types.INTEGER);
            if (a.getBarbeiroId() > 0) stmt.setInt(2, a.getBarbeiroId()); else stmt.setNull(2, Types.INTEGER);
            stmt.setString(3, a.getServicoNome());
            stmt.setString(4, a.getBarbeiroNome());
            stmt.setInt(5, a.getDuracaoMinutos());
            stmt.setString(6, a.getClienteNome());
            stmt.setString(7, a.getContato());
            stmt.setTimestamp(8, Timestamp.valueOf(a.getDataHora()));
            stmt.setString(9, a.getOrigemContato() != null ? a.getOrigemContato().name() : OrigemContato.OUTRO.name());
            stmt.setString(10, a.getStatus() != null ? a.getStatus().name() : StatusAgendamento.AGENDADO.name());
            stmt.setString(11, a.getMotivoCancelamento());
            stmt.setInt(12, a.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Remove definitivamente um agendamento pelo ID.
     */
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM agendamentos WHERE id=?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Busca um agendamento pelo ID. O nome do serviço e do barbeiro exibidos vêm
     * do snapshot gravado na criação (via {@code COALESCE}); só se recorre ao
     * nome atual da tabela {@code servicos}/{@code barbeiros} quando o snapshot
     * estiver vazio (registros antigos, anteriores à introdução do snapshot).
     *
     * @param id identificador do agendamento
     * @return o agendamento encontrado, ou {@code null} se não existir
     */
    public Agendamento buscarPorId(int id) throws SQLException {
        String sql = "SELECT a.*, " +
                "COALESCE(a.servico_nome_snapshot, s.nome) AS servico_nome, " +
                "COALESCE(a.barbeiro_nome_snapshot, b.nome) AS barbeiro_nome " +
                "FROM agendamentos a " +
                "LEFT JOIN servicos s ON a.servico_id = s.id " +
                "LEFT JOIN barbeiros b ON a.barbeiro_id = b.id " +
                "WHERE a.id=?";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return extrairAgendamento(rs);
            }
        }
        return null;
    }

    /**
     * Lista os agendamentos de uma barbearia que ainda estão em aberto, ou seja,
     * com status {@code AGENDADO} ou {@code EM_ATENDIMENTO} (exclui cancelados,
     * concluídos etc.), ordenados por data/hora.
     */
    public List<Agendamento> listarPendentesPorBarbearia(int barbeariaId) throws SQLException {
        String filtro = " AND a.status IN ('" + StatusAgendamento.AGENDADO.name() + "','" + StatusAgendamento.EM_ATENDIMENTO.name() + "')";
        return listar(barbeariaId, filtro);
    }

    /**
     * Alias para compatibilidade (camada service/UI usa listarPendentes).
     */
    public List<Agendamento> listarPendentes(int barbeariaId) throws SQLException {
        return listarPendentesPorBarbearia(barbeariaId);
    }

    /**
     * Lista todos os agendamentos de uma barbearia, independentemente do status,
     * ordenados por data/hora.
     */
    public List<Agendamento> listarPorBarbearia(int barbeariaId) throws SQLException {
        return listar(barbeariaId, "");
    }

    /**
     * Alias para compatibilidade (camada service/UI usa listarTodos).
     */
    public List<Agendamento> listarTodos(int barbeariaId) throws SQLException {
        return listarPorBarbearia(barbeariaId);
    }

    // Duração máxima aceita para um serviço (ver DialogServico) — usada só para
    // dimensionar a janela de busca abaixo, largura o bastante para conter
    // qualquer agendamento existente que possa se sobrepor ao novo.
    private static final int DURACAO_MAXIMA_MINUTOS = 480;

    /**
     * Conflito = sobreposição real de intervalo (início/fim, considerando a duração de
     * cada serviço), não mais uma janela fixa de 30 minutos. Barbeiros diferentes podem
     * atender no mesmo horário normalmente (não há conflito de recurso entre eles).
     * Agendamentos "encostados" (um termina exatamente quando o outro começa) não contam
     * como conflito.
     */
    /**
     * Verifica se existe outro agendamento do mesmo barbeiro cujo intervalo
     * (início/fim, considerando a duração de cada um) se sobreponha ao intervalo
     * informado. A busca no banco é restrita a uma janela de +/- {@link
     * #DURACAO_MAXIMA_MINUTOS} em torno do horário novo (suficiente para conter
     * qualquer agendamento que possa se sobrepor), e a checagem fina de
     * sobreposição é feita em memória.
     *
     * @param barbeiroId      barbeiro a checar; se <= 0, não há conflito possível
     * @param dataHora        início do novo agendamento
     * @param duracaoMinutos  duração do novo agendamento (usa 30 min se <= 0)
     * @param excluirId       ID de agendamento a ignorar na busca (0 para nenhum) —
     *                        usado ao editar um agendamento existente, para que ele
     *                        não conflite consigo mesmo
     * @return {@code true} se houver sobreposição de horário com outro agendamento
     *         em aberto ({@code AGENDADO} ou {@code EM_ATENDIMENTO}) do mesmo barbeiro
     */
    public boolean verificarConflito(int barbeiroId, LocalDateTime dataHora, int duracaoMinutos, int excluirId) throws SQLException {
        if (barbeiroId <= 0 || dataHora == null) return false;
        int duracaoNova = duracaoMinutos > 0 ? duracaoMinutos : 30;
        LocalDateTime inicioNovo = dataHora;
        LocalDateTime fimNovo = dataHora.plusMinutes(duracaoNova);

        String sql = "SELECT data_hora, duracao_minutos_snapshot FROM agendamentos " +
                "WHERE barbeiro_id=? " +
                "AND data_hora > ? AND data_hora < ? " +
                "AND status IN ('" + StatusAgendamento.AGENDADO.name() + "','" + StatusAgendamento.EM_ATENDIMENTO.name() + "')" +
                (excluirId > 0 ? " AND id <> ?" : "");

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, barbeiroId);
            stmt.setTimestamp(2, Timestamp.valueOf(dataHora.minusMinutes(DURACAO_MAXIMA_MINUTOS)));
            stmt.setTimestamp(3, Timestamp.valueOf(dataHora.plusMinutes(DURACAO_MAXIMA_MINUTOS)));
            if (excluirId > 0) stmt.setInt(4, excluirId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime inicioExistente = rs.getTimestamp("data_hora").toLocalDateTime();
                    int duracaoExistente = rs.getInt("duracao_minutos_snapshot");
                    if (rs.wasNull() || duracaoExistente <= 0) duracaoExistente = 30;
                    LocalDateTime fimExistente = inicioExistente.plusMinutes(duracaoExistente);

                    if (inicioExistente.isBefore(fimNovo) && fimExistente.isAfter(inicioNovo)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Overload de conveniência para checagem antes de existir um Agendamento
     * montado (ex.: pré-validação na tela de novo agendamento) — não há id
     * ainda para excluir da busca.
     */
    public boolean verificarConflito(int barbeiroId, LocalDateTime dataHora, int duracaoMinutos) throws SQLException {
        return verificarConflito(barbeiroId, dataHora, duracaoMinutos, 0);
    }

    /**
     * Monta e executa a consulta de listagem de agendamentos de uma barbearia,
     * aplicando um filtro SQL adicional opcional (ex.: restrição por status) e
     * ordenando sempre por data/hora crescente.
     */
    private List<Agendamento> listar(int barbeariaId, String extraSql) throws SQLException {
        List<Agendamento> lista = new ArrayList<>();
        String sql = "SELECT a.*, " +
                "COALESCE(a.servico_nome_snapshot, s.nome) AS servico_nome, " +
                "COALESCE(a.barbeiro_nome_snapshot, b.nome) AS barbeiro_nome " +
                "FROM agendamentos a " +
                "LEFT JOIN servicos s ON a.servico_id = s.id " +
                "LEFT JOIN barbeiros b ON a.barbeiro_id = b.id " +
                "WHERE a.barbearia_id=? " + (extraSql == null ? "" : extraSql) +
                " ORDER BY a.data_hora ASC";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, barbeariaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(extrairAgendamento(rs));
            }
        }
        return lista;
    }

    /**
     * Converte a linha atual do {@link ResultSet} em um objeto {@link Agendamento},
     * tratando valores nulos (IDs de serviço/barbeiro zerados quando ausentes,
     * origem/status inválidos revertidos para o valor padrão, duração ausente
     * assumida como 30 minutos).
     */
    private Agendamento extrairAgendamento(ResultSet rs) throws SQLException {
        Agendamento a = new Agendamento();
        a.setId(rs.getInt("id"));
        a.setBarbeariaId(rs.getInt("barbearia_id"));
        a.setServicoId(rs.getInt("servico_id"));
        if (rs.wasNull()) a.setServicoId(0);
        a.setBarbeiroId(rs.getInt("barbeiro_id"));
        if (rs.wasNull()) a.setBarbeiroId(0);
        a.setClienteNome(rs.getString("cliente_nome"));
        a.setContato(rs.getString("contato"));
        Timestamp ts = rs.getTimestamp("data_hora");
        if (ts != null) a.setDataHora(ts.toLocalDateTime());

        String origem = rs.getString("origem_contato");
        try { a.setOrigemContato(OrigemContato.valueOf(origem)); }
        catch (Exception e) { a.setOrigemContato(OrigemContato.OUTRO); }

        String st = rs.getString("status");
        try { a.setStatus(StatusAgendamento.valueOf(st)); }
        catch (Exception e) { a.setStatus(StatusAgendamento.AGENDADO); }

        a.setServicoNome(rs.getString("servico_nome"));
        a.setBarbeiroNome(rs.getString("barbeiro_nome"));

        int duracao = rs.getInt("duracao_minutos_snapshot");
        a.setDuracaoMinutos(rs.wasNull() || duracao <= 0 ? 30 : duracao);

        a.setMotivoCancelamento(rs.getString("motivo_cancelamento"));

        return a;
    }
}
