package br.com.barbershop.ui.controller;

import br.com.barbershop.app.AppContext;
import br.com.barbershop.model.Agendamento;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.service.AgendaService;
import br.com.barbershop.ui.support.StatusRowRenderer;
import br.com.barbershop.util.DateTimeUtil;
import java.awt.Component;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Controla a aba "Histórico": tabela com todos os agendamentos da barbearia
 * (qualquer status) e filtro textual. Extraído de {@code TelaHome} (SRP -
 * Fase 5).
 */
public class HistoricoController {

    private final Component parent;
    private final JTable tblHistorico;
    private final JTextField txtBusca;
    private final AgendaService agendaService;

    private List<Agendamento> agendamentosHistoricoAtuais;

    public HistoricoController(Component parent, JTable tblHistorico, JTextField txtBusca, AgendaService agendaService) {
        this.parent = parent;
        this.tblHistorico = tblHistorico;
        this.txtBusca = txtBusca;
        this.agendaService = agendaService;
    }

    /** Configura o ordenador e o colorizador de linhas da tabela. */
    public void configurar() {
        tblHistorico.setRowSorter(new TableRowSorter<>((DefaultTableModel) tblHistorico.getModel()));
        tblHistorico.setDefaultRenderer(Object.class, new StatusRowRenderer(() -> agendamentosHistoricoAtuais));
    }

    /**
     * Recarrega a tabela com todos os agendamentos da barbearia atual
     * (qualquer status) e guarda a lista em {@code agendamentosHistoricoAtuais},
     * consultada pelo {@link StatusRowRenderer} para colorir cada linha.
     */
    public void carregarHistorico() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;
            List<Agendamento> lista = agendaService.listarPorBarbearia(b.getId());
            agendamentosHistoricoAtuais = lista;
            DefaultTableModel model = (DefaultTableModel) tblHistorico.getModel();
            model.setRowCount(0);
            for (Agendamento a : lista) {
                model.addRow(new Object[]{
                    DateTimeUtil.formatDateTime(a.getDataHora()),
                    a.getClienteNome(),
                    a.getContato(),
                    a.getServicoNome() != null ? a.getServicoNome() : ("Serviço #" + a.getServicoId()),
                    a.getBarbeiroNome() != null ? a.getBarbeiroNome() : ("Barbeiro #" + a.getBarbeiroId()),
                    a.getOrigemContato(),
                    a.getStatus()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao carregar histórico:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aplica em {@code tblHistorico} o filtro de texto digitado em
     * {@code txtBusca}, usando um {@link RowFilter} de regex
     * case-insensitive sobre todas as colunas visíveis (cliente, contato,
     * serviço, barbeiro, origem e status). Texto vazio remove o filtro.
     */
    public void aplicarFiltro() {
        TableRowSorter<?> sorter = (TableRowSorter<?>) tblHistorico.getRowSorter();
        String texto = txtBusca.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
        }
    }
}
