package br.com.barberdesk.ui.controller;

import br.com.barberdesk.app.AppContext;
import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.Barbearia;
import br.com.barberdesk.model.Servico;
import br.com.barberdesk.model.StatusAgendamento;
import br.com.barberdesk.service.AgendaService;
import br.com.barberdesk.ui.TelaEditarAgendamento;
import br.com.barberdesk.ui.TelaNovoAgendamento;
import br.com.barberdesk.ui.support.StatusRowRenderer;
import br.com.barberdesk.util.DateTimeUtil;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controla a aba "Home": tabela de agendamentos pendentes, menu de contexto
 * (editar/iniciar/terminar/cancelar) e abertura de {@link TelaNovoAgendamento}.
 * Extraído de {@code TelaHome} para isolar a orquestração da agenda do
 * restante da tela (SRP — Fase 5).
 */
public class AgendaController {

    private static final Logger logger = LoggerFactory.getLogger(AgendaController.class);

    private final Component parent;
    private final JTable tblAgendamentos;
    private final AgendaService agendaService;
    private final Runnable onAgendamentoAlterado;

    private List<Agendamento> agendamentosPendentesAtuais;

    /**
     * @param parent                 janela dona dos diálogos abertos por este controller
     * @param tblAgendamentos        tabela de agendamentos pendentes da Home
     * @param agendaService          service de domínio da agenda
     * @param onAgendamentoAlterado  callback disparado sempre que um agendamento muda de
     *                               status (iniciar/concluir/cancelar) ou é editado, para que
     *                               quem monta a tela recarregue outras áreas dependentes (ex.: histórico)
     */
    public AgendaController(Component parent, JTable tblAgendamentos, AgendaService agendaService,
                             Runnable onAgendamentoAlterado) {
        this.parent = parent;
        this.tblAgendamentos = tblAgendamentos;
        this.agendaService = agendaService;
        this.onAgendamentoAlterado = onAgendamentoAlterado;
    }

    /** Configura o menu de contexto, o ordenador e o colorizador de linhas da tabela. */
    public void configurar() {
        tblAgendamentos.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tblAgendamentos.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tblAgendamentos.setRowSelectionInterval(row, row);
                        mostrarMenuContexto(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
        tblAgendamentos.setRowSorter(new TableRowSorter<>((DefaultTableModel) tblAgendamentos.getModel()));
        tblAgendamentos.setDefaultRenderer(Object.class, new StatusRowRenderer(() -> agendamentosPendentesAtuais));
    }

    /**
     * Recarrega a tabela com os agendamentos pendentes da barbearia atual e
     * guarda a lista em {@code agendamentosPendentesAtuais}, consultada pelo
     * {@link StatusRowRenderer} para colorir cada linha.
     */
    public void carregarAgendamentos() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;
            List<Agendamento> lista = agendaService.listarPendentesPorBarbearia(b.getId());
            agendamentosPendentesAtuais = lista;
            DefaultTableModel model = (DefaultTableModel) tblAgendamentos.getModel();
            model.setRowCount(0);
            for (Agendamento a : lista) {
                model.addRow(new Object[]{
                    a.getId(),
                    DateTimeUtil.formatDateTime(a.getDataHora()),
                    a.getClienteNome(),
                    a.getContato(),
                    a.getServicoNome() != null ? a.getServicoNome() : ("Serviço #" + a.getServicoId()),
                    a.getBarbeiroNome() != null ? a.getBarbeiroNome() : ("Barbeiro #" + a.getBarbeiroId()),
                    a.getStatus()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao carregar agendamentos:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Abre {@code TelaNovoAgendamento} sem serviço pré-selecionado (botão "+ Novo Agendamento"). */
    public void abrirNovoAgendamento() {
        new TelaNovoAgendamento(this::carregarAgendamentos).setVisible(true);
    }

    /** Abre {@code TelaNovoAgendamento} já com o serviço pré-selecionado (cartão do grid de serviços). */
    public void abrirNovoAgendamentoComServico(Servico s) {
        new TelaNovoAgendamento(s, this::carregarAgendamentos).setVisible(true);
    }

    private void mostrarMenuContexto(Component comp, int x, int y) {
        int row = tblAgendamentos.getSelectedRow();
        if (row < 0) return;

        int id = (int) tblAgendamentos.getModel().getValueAt(row, 0);
        try {
            Agendamento a = agendaService.buscarPorId(id);
            JPopupMenu menu = new JPopupMenu();

            JMenuItem itemEditar = new JMenuItem("Editar Agendamento");
            itemEditar.addActionListener(e -> {
                new TelaEditarAgendamento(id).setVisible(true);
                carregarAgendamentos();
            });
            menu.add(itemEditar);
            menu.addSeparator();

            if (a.getStatus() == StatusAgendamento.AGENDADO) {
                JMenuItem itemIniciar = new JMenuItem("Iniciar Serviço");
                itemIniciar.addActionListener(e -> iniciarAtendimento(a.getId()));
                menu.add(itemIniciar);
            } else if (a.getStatus() == StatusAgendamento.EM_ATENDIMENTO) {
                JMenuItem itemTerminar = new JMenuItem("Terminar Serviço");
                itemTerminar.addActionListener(e -> concluirAtendimento(a.getId()));
                menu.add(itemTerminar);
            }

            if (a.getStatus() != StatusAgendamento.CONCLUIDO && a.getStatus() != StatusAgendamento.CANCELADO) {
                JMenuItem itemCancelar = new JMenuItem("Cancelar Agendamento");
                itemCancelar.addActionListener(e -> cancelarAgendamento(a.getId()));
                menu.add(itemCancelar);
            }

            menu.show(comp, x, y);
        } catch (SQLException e) { logger.error("Erro ao montar menu de contexto do agendamento", e); }
    }

    private void iniciarAtendimento(int id) {
        try {
            agendaService.iniciarAtendimento(id);
            carregarAgendamentos();
            onAgendamentoAlterado.run();
        } catch (SQLException e) { logger.error("Erro ao iniciar atendimento", e); }
    }

    private void concluirAtendimento(int id) {
        try {
            agendaService.concluirAtendimento(id);
            carregarAgendamentos();
            onAgendamentoAlterado.run();
        } catch (SQLException e) { logger.error("Erro ao concluir atendimento", e); }
    }

    private void cancelarAgendamento(int id) {
        String motivo = JOptionPane.showInputDialog(parent, "Motivo do cancelamento (opcional):", "Cancelar Agendamento", JOptionPane.QUESTION_MESSAGE);
        if (motivo == null) return; // usuário fechou o diálogo sem confirmar — não cancela
        try {
            agendaService.cancelarAgendamento(id, motivo.trim());
            carregarAgendamentos();
            onAgendamentoAlterado.run();
        } catch (SQLException e) { logger.error("Erro ao cancelar agendamento", e); }
    }
}
