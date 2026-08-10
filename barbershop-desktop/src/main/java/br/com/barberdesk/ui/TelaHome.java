package br.com.barberdesk.ui;

import br.com.barberdesk.dao.*;
import br.com.barberdesk.model.*;
import br.com.barberdesk.service.AgendaService;
import br.com.barberdesk.service.RelatorioService;
import br.com.barberdesk.app.AppContext;
import br.com.barberdesk.app.FabricaDeServicos;
import br.com.barberdesk.util.DateTimeUtil;
import br.com.barberdesk.ui.support.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Tela principal do sistema, exibida logo após o login bem-sucedido.
 * <p>
 * Concentra toda a navegação da aplicação através de um menu lateral fixo
 * ({@code pnlSideMenu}) que troca os cartões de um {@link java.awt.CardLayout}
 * ({@code pnlCards}) entre quatro áreas:
 * <ul>
 *   <li><b>Home</b> — grid de serviços para agendamento rápido e tabela de
 *       agendamentos pendentes, com menu de contexto (botão direito) para
 *       iniciar, concluir ou cancelar um atendimento;</li>
 *   <li><b>Minha Barbearia</b> — abas para editar os dados gerais da
 *       barbearia (nome, CEP, cultura/valores, horário de funcionamento) e
 *       gerenciar (cadastrar/editar/excluir) serviços, barbeiros e a
 *       listagem de clientes;</li>
 *   <li><b>Histórico</b> — todos os agendamentos já registrados (qualquer
 *       status), com busca textual;</li>
 *   <li><b>Relatórios</b> — faturamento total, serviços mais vendidos e
 *       ranking de barbeiros num intervalo de datas.</li>
 * </ul>
 * A tela não implementa regras de negócio diretamente: delega para os
 * serviços de domínio ({@link AgendaService}, {@link RelatorioService}) e
 * para os DAOs, mantendo aqui apenas a orquestração de UI (carregar/atualizar
 * tabelas, validar entrada de formulário e reagir a eventos dos componentes
 * gerados pelo NetBeans GUI Builder).
 */
public class TelaHome extends javax.swing.JFrame {

    private static final Logger logger = LoggerFactory.getLogger(TelaHome.class);

    private AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
    private ServicoDAO servicoDAO = new ServicoDAO();
    private BarbeariaDAO barbeariaDAO = new BarbeariaDAO();
    private BarbeiroDAO barbeiroDAO = new BarbeiroDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private final FabricaDeServicos fabricaDeServicos = new FabricaDeServicos();
    private final AgendaService agendaService = fabricaDeServicos.criarAgendaService();
    private final RelatorioService relatorioService = fabricaDeServicos.criarRelatorioService();
    private final NumberFormat moedaFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // Listas de apoio para seleção nas tabelas de gerenciamento
    private List<Servico> servicosGerenciarLista;
    private List<Barbeiro> barbeirosGerenciarLista;

    // Espelham as linhas de tblAgendamentos/tblHistorico, na mesma ordem — usadas
    // pelo StatusRowRenderer pra colorir a linha por status/proximidade (RF11).
    private List<Agendamento> agendamentosPendentesAtuais;
    private List<Agendamento> agendamentosHistoricoAtuais;

    /**
     * Monta a janela: inicializa os componentes gerados pelo NetBeans, aplica
     * o ícone customizado da aplicação, configura os listeners/renderer das
     * tabelas e carrega os dados iniciais (agendamentos, serviços,
     * barbeiros, clientes, dados da barbearia e histórico) a partir do
     * banco.
     */
    public TelaHome() {
        initComponents();
        UIUtil.aplicarIcone(this);
        configurarTabela();
        carregarDados();
    }

    /**
     * Configura o comportamento das tabelas da tela: adiciona o menu de
     * contexto (clique com o botão direito) na tabela de agendamentos
     * pendentes, liga um {@link TableRowSorter} em cada tabela (permitindo
     * ordenar/filtrar sem alterar a ordem dos dados no model) e registra o
     * {@link StatusRowRenderer} responsável por colorir as linhas de
     * agendamentos e histórico conforme o status (RF11).
     */
    private void configurarTabela() {
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
        tblHistorico.setRowSorter(new TableRowSorter<>((DefaultTableModel) tblHistorico.getModel()));
        tblGerenciarServicos.setRowSorter(new TableRowSorter<>((DefaultTableModel) tblGerenciarServicos.getModel()));
        tblGerenciarBarbeiros.setRowSorter(new TableRowSorter<>((DefaultTableModel) tblGerenciarBarbeiros.getModel()));
        tblClientes.setRowSorter(new TableRowSorter<>((DefaultTableModel) tblClientes.getModel()));

        // RF11: classifica visualmente as linhas por status/proximidade do horário.
        tblAgendamentos.setDefaultRenderer(Object.class, new StatusRowRenderer(() -> agendamentosPendentesAtuais));
        tblHistorico.setDefaultRenderer(Object.class, new StatusRowRenderer(() -> agendamentosHistoricoAtuais));
    }

    /**
     * Colore a linha pelo status do agendamento e, quando ainda está AGENDADO,
     * destaca os que começam em até 30 minutos — RF11 (classificação visual por
     * status/proximidade).
     */
    private static class StatusRowRenderer extends DefaultTableCellRenderer {
        private static final Color COR_CANCELADO = new Color(248, 215, 218);
        private static final Color COR_CONCLUIDO = new Color(212, 237, 218);
        private static final Color COR_EM_ATENDIMENTO = new Color(204, 229, 255);
        private static final Color COR_INICIO_IMINENTE = new Color(255, 243, 205);

        private final java.util.function.Supplier<List<Agendamento>> origemSupplier;

        StatusRowRenderer(java.util.function.Supplier<List<Agendamento>> origemSupplier) {
            this.origemSupplier = origemSupplier;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(corDaLinha(table, row));
            }
            return c;
        }

        private Color corDaLinha(JTable table, int viewRow) {
            List<Agendamento> origem = origemSupplier.get();
            if (origem == null) return Color.WHITE;

            int modelRow = table.convertRowIndexToModel(viewRow);
            if (modelRow < 0 || modelRow >= origem.size()) return Color.WHITE;

            Agendamento a = origem.get(modelRow);
            if (a.getStatus() == StatusAgendamento.CANCELADO) return COR_CANCELADO;
            if (a.getStatus() == StatusAgendamento.CONCLUIDO) return COR_CONCLUIDO;
            if (a.getStatus() == StatusAgendamento.EM_ATENDIMENTO) return COR_EM_ATENDIMENTO;

            if (a.getStatus() == StatusAgendamento.AGENDADO && a.getDataHora() != null) {
                long minutosParaComecar = java.time.Duration.between(LocalDateTime.now(), a.getDataHora()).toMinutes();
                if (minutosParaComecar >= 0 && minutosParaComecar <= 30) return COR_INICIO_IMINENTE;
            }

            return Color.WHITE;
        }
    }

    /**
     * Monta e exibe o menu de contexto (clique com o botão direito) da linha
     * selecionada em {@code tblAgendamentos}, oferecendo as ações válidas
     * para o status atual do agendamento: editar sempre; iniciar serviço se
     * estiver AGENDADO; terminar serviço se estiver EM_ATENDIMENTO; cancelar
     * se ainda não estiver CONCLUIDO nem CANCELADO.
     *
     * @param comp componente de referência para posicionar o popup
     * @param x    posição X do popup, relativa a {@code comp}
     * @param y    posição Y do popup, relativa a {@code comp}
     */
    private void mostrarMenuContexto(Component comp, int x, int y) {
        int row = tblAgendamentos.getSelectedRow();
        if (row < 0) return;

        int id = (int) tblAgendamentos.getModel().getValueAt(row, 0);
        try {
            Agendamento a = agendamentoDAO.buscarPorId(id);
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

    /**
     * Marca o agendamento como EM_ATENDIMENTO através do
     * {@link AgendaService} e recarrega as tabelas de agendamentos
     * pendentes e histórico para refletir a mudança de status.
     *
     * @param id identificador do agendamento
     */
    private void iniciarAtendimento(int id) {
        try {
            agendaService.iniciarAtendimento(id);
            carregarAgendamentos();
            carregarHistorico();
        } catch (SQLException e) { logger.error("Erro ao iniciar atendimento", e); }
    }

    /**
     * Marca o agendamento como CONCLUIDO através do {@link AgendaService} e
     * recarrega as tabelas de agendamentos pendentes e histórico para
     * refletir a mudança de status.
     *
     * @param id identificador do agendamento
     */
    private void concluirAtendimento(int id) {
        try {
            agendaService.concluirAtendimento(id);
            carregarAgendamentos();
            carregarHistorico();
        } catch (SQLException e) { logger.error("Erro ao concluir atendimento", e); }
    }

    /**
     * Pede ao usuário um motivo (opcional) e cancela o agendamento através
     * do {@link AgendaService}, recarregando em seguida as tabelas de
     * agendamentos pendentes e histórico.
     *
     * @param id identificador do agendamento
     */
    private void cancelarAgendamento(int id) {
        String motivo = JOptionPane.showInputDialog(this, "Motivo do cancelamento (opcional):", "Cancelar Agendamento", JOptionPane.QUESTION_MESSAGE);
        if (motivo == null) return; // usuário fechou o diálogo sem confirmar — não cancela
        try {
            agendaService.cancelarAgendamento(id, motivo.trim());
            carregarAgendamentos();
            carregarHistorico();
        } catch (SQLException e) { logger.error("Erro ao cancelar agendamento", e); }
    }

    /**
     * Recarrega, de uma só vez, todos os dados exibidos pela tela:
     * agendamentos pendentes, grid de serviços, tabelas de gerenciamento
     * (serviços, barbeiros, clientes), dados gerais da barbearia e
     * histórico. Chamado no construtor para popular a tela na abertura.
     */
    private void carregarDados() {
        carregarAgendamentos();
        carregarGridServicos();
        carregarTabelaServicos();
        carregarTabelaBarbeiros();
        carregarTabelaClientes();
        carregarDadosBarbearia();
        carregarHistorico();
    }

    /**
     * Recarrega {@code tblClientes} com os clientes da barbearia atual
     * ({@link AppContext#getBarbeariaAtual()}), limpando as linhas
     * existentes antes de repopular.
     */
    private void carregarTabelaClientes() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;

            DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
            model.setRowCount(0);
            for (Cliente c : clienteDAO.listarPorBarbearia(b.getId())) {
                model.addRow(new Object[]{ c.getNome(), c.getContato() });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar clientes:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aplica em {@code tblClientes} o filtro de texto digitado em
     * {@code txtBuscaClientes}, usando um {@link RowFilter} de regex
     * case-insensitive sobre todas as colunas visíveis. Texto vazio remove
     * o filtro (volta a mostrar todas as linhas).
     */
    private void aplicarFiltroClientes() {
        TableRowSorter<?> sorter = (TableRowSorter<?>) tblClientes.getRowSorter();
        String texto = txtBuscaClientes.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto)));
        }
    }

    /**
     * Recarrega {@code tblGerenciarServicos} com os serviços da barbearia
     * atual e guarda a lista em {@code servicosGerenciarLista}, que
     * espelha a ordem das linhas do model e é usada pelos handlers de
     * editar/excluir para resolver a linha selecionada na tabela de volta
     * para o {@link Servico} correspondente.
     */
    private void carregarTabelaServicos() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;

            servicosGerenciarLista = servicoDAO.listarPorBarbearia(b.getId());
            DefaultTableModel model = (DefaultTableModel) tblGerenciarServicos.getModel();
            model.setRowCount(0);
            for (Servico s : servicosGerenciarLista) {
                model.addRow(new Object[]{
                    s.getNome(),
                    s.getPreco()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar serviços:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Recarrega {@code tblGerenciarBarbeiros} com os barbeiros da barbearia
     * atual e guarda a lista em {@code barbeirosGerenciarLista}, que
     * espelha a ordem das linhas do model e é usada pelos handlers de
     * editar/excluir para resolver a linha selecionada na tabela de volta
     * para o {@link Barbeiro} correspondente.
     */
    private void carregarTabelaBarbeiros() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;

            barbeirosGerenciarLista = barbeiroDAO.listarPorBarbearia(b.getId());
            DefaultTableModel model = (DefaultTableModel) tblGerenciarBarbeiros.getModel();
            model.setRowCount(0);
            for (Barbeiro barb : barbeirosGerenciarLista) {
                model.addRow(new Object[]{
                    barb.getNome()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar barbeiros:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Recarrega {@code tblAgendamentos} com os agendamentos pendentes da
     * barbearia atual e guarda a lista em {@code agendamentosPendentesAtuais}.
     * Essa lista espelha, na mesma ordem, as linhas do model da tabela e é
     * consultada pelo {@link StatusRowRenderer} para decidir a cor de cada
     * linha — por isso, ao mapear uma linha visível de volta para o
     * {@link Agendamento} real, é preciso passar por
     * {@code table.convertRowIndexToModel(viewRow)} quando há um
     * {@link TableRowSorter} ativo, já que a ordem visual (ordenada/filtrada
     * pelo usuário) pode não bater com a ordem do model. Método público
     * porque também é chamado por outras telas (ex.: edição/novo
     * agendamento) para atualizar esta tela após uma alteração.
     */
    public void carregarAgendamentos() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;
            List<Agendamento> lista = agendamentoDAO.listarPendentesPorBarbearia(b.getId());
            agendamentosPendentesAtuais = lista;
            DefaultTableModel model = (DefaultTableModel) tblAgendamentos.getModel();
            model.setRowCount(0);
            // Adicionar coluna oculta para ID se necessário, ou usar a primeira coluna
            // Para simplificar, vamos assumir que a primeira coluna é o ID e ocultá-la ou tratá-la
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
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar agendamentos:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reconstrói o grid de cartões de serviço em {@code pnlServicosGrid} (um
     * {@link JPanel} por serviço, com miniatura, nome, preço e botão
     * "Agendar") a partir dos serviços cadastrados na barbearia atual.
     * Remove todos os cartões existentes antes de recriá-los.
     */
    private void carregarGridServicos() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;
            List<Servico> servicos = servicoDAO.listarPorBarbearia(b.getId());
            pnlServicosGrid.removeAll();
            for (Servico s : servicos) {
                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setPreferredSize(new Dimension(120, 160));
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                JLabel lblImg = new JLabel();
                lblImg.setAlignmentX(Component.CENTER_ALIGNMENT);
                UIUtil.exibirMiniatura(lblImg, s.getFotoCaminho(), 100, 80);
                
                JLabel lblNome = new JLabel(s.getNome());
                lblNome.setAlignmentX(Component.CENTER_ALIGNMENT);
                lblNome.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                JLabel lblPreco = new JLabel(moedaFormat.format(s.getPreco()));
                lblPreco.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                JButton btn = new JButton("Agendar");
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.addActionListener(e -> abrirNovoAgendamentoComServico(s));

                card.add(Box.createVerticalStrut(5));
                card.add(lblImg);
                card.add(Box.createVerticalStrut(5));
                card.add(lblNome);
                card.add(lblPreco);
                card.add(Box.createVerticalStrut(5));
                card.add(btn);
                
                pnlServicosGrid.add(card);
            }
            pnlServicosGrid.revalidate();
            pnlServicosGrid.repaint();
        } catch (SQLException e) { logger.error("Erro ao carregar grid de serviços", e); }
    }

    /**
     * Preenche os campos da aba "Dados Gerais" (nome, CEP, cultura/valores e
     * horário de funcionamento) com os dados atuais da barbearia. Antes de
     * exibir, recarrega a barbearia diretamente do banco em vez de confiar
     * apenas no {@link AppContext}, para evitar mostrar dados desatualizados
     * caso o contexto em memória esteja obsoleto, e já atualiza o
     * {@link AppContext} com o resultado.
     */
    private void carregarDadosBarbearia() {
        try {
            Barbearia ctx = AppContext.getInstance().getBarbeariaAtual();
            if (ctx == null) return;

            // Recarrega do banco para evitar inconsistências caso o AppContext esteja desatualizado
            Barbearia b = barbeariaDAO.buscarPorId(ctx.getId());
            if (b == null) b = ctx;
            // Reconstrói a sessão inteira (Session é imutável) mantendo o mesmo usuário logado
            AppContext.getInstance().setSessaoAtual(new Session(AppContext.getInstance().getUsuarioLogado(), b));

            txtNomeB.setText(b.getNome() != null ? b.getNome() : "");
            txtCEPB.setText(b.getCep() != null ? b.getCep() : "");
            txtCulturaB.setText(b.getCulturaValores() != null ? b.getCulturaValores() : "");
            txtHorarioAbertura.setText(DateTimeUtil.formatTime(b.getHorarioAbertura()));
            txtHorarioFechamento.setText(DateTimeUtil.formatTime(b.getHorarioFechamento()));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar dados da barbearia:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Recarrega {@code tblHistorico} com todos os agendamentos da barbearia
     * atual (qualquer status) e guarda a lista em
     * {@code agendamentosHistoricoAtuais}, que espelha a ordem das linhas do
     * model e é usada pelo {@link StatusRowRenderer} para colorir cada linha
     * (mesmo raciocínio de {@link #carregarAgendamentos()}).
     */
    private void carregarHistorico() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;
            List<Agendamento> lista = agendamentoDAO.listarPorBarbearia(b.getId());
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
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar histórico:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre {@code TelaNovoAgendamento} já com o serviço pré-selecionado
     * (atalho a partir do botão "Agendar" de um cartão do grid de
     * serviços) e recarrega os agendamentos pendentes quando a tela é
     * fechada.
     *
     * @param s serviço a ser pré-selecionado no novo agendamento
     */
    private void abrirNovoAgendamentoComServico(Servico s) {
        new TelaNovoAgendamento(s, this::carregarAgendamentos).setVisible(true);
    }

    /**
     * Aplica em {@code tblHistorico} o filtro de texto digitado em
     * {@code txtBuscaHistorico}, usando um {@link RowFilter} de regex
     * case-insensitive sobre todas as colunas visíveis (cliente, contato,
     * serviço, barbeiro, origem e status). Texto vazio remove o filtro.
     */
    private void aplicarFiltroHistorico() {
        TableRowSorter<?> sorter = (TableRowSorter<?>) tblHistorico.getRowSorter();
        String texto = txtBuscaHistorico.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto)));
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSideMenu = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        btnHome = new javax.swing.JButton();
        btnMinhaBarbearia = new javax.swing.JButton();
        btnHistorico = new javax.swing.JButton();
        btnRelatorios = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        pnlCards = new javax.swing.JPanel();
        pnlHome = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        btnAgendar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAgendamentos = new javax.swing.JTable();
        lblSubtitulo = new javax.swing.JLabel();
        lblHintAgendamentos = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        pnlServicosGrid = new javax.swing.JPanel();
        pnlMinhaBarbearia = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        tabBarbearia = new javax.swing.JTabbedPane();
        pnlDadosGerais = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtNomeB = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtCEPB = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtCulturaB = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtHorarioAbertura = UIUtil.criarCampoMascarado("##:##");
        jLabel15 = new javax.swing.JLabel();
        txtHorarioFechamento = UIUtil.criarCampoMascarado("##:##");
        btnSalvarB = new javax.swing.JButton();
        pnlGerenciarServicos = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblGerenciarServicos = new javax.swing.JTable();
        btnNovoServico = new javax.swing.JButton();
        btnEditarServicoB = new javax.swing.JButton();
        btnExcluirServico = new javax.swing.JButton();
        pnlGerenciarBarbeiros = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblGerenciarBarbeiros = new javax.swing.JTable();
        btnNovoBarbeiro = new javax.swing.JButton();
        btnEditarBarbeiroB = new javax.swing.JButton();
        btnExcluirBarbeiro = new javax.swing.JButton();
        pnlClientes = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        txtBuscaClientes = new javax.swing.JTextField();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        pnlHistorico = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtBuscaHistorico = new javax.swing.JTextField();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblHistorico = new javax.swing.JTable();
        pnlRelatorios = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtRelatorioDe = UIUtil.criarCampoMascarado("##/##/####");
        jLabel19 = new javax.swing.JLabel();
        txtRelatorioAte = UIUtil.criarCampoMascarado("##/##/####");
        btnGerarRelatorio = new javax.swing.JButton();
        lblFaturamentoTotal = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tblServicosVendidos = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        tblRankingBarbeiros = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BarberDesk - Sistema de Gerenciamento");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlSideMenu.setBackground(new java.awt.Color(51, 51, 51));
        pnlSideMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblLogo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblLogo.setForeground(new java.awt.Color(255, 255, 255));
        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setText("BarberDesk");
        pnlSideMenu.add(lblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 200, -1));

        btnHome.setText("Home");
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });
        pnlSideMenu.add(btnHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 180, 40));

        btnMinhaBarbearia.setText("Minha Barbearia");
        btnMinhaBarbearia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMinhaBarbeariaActionPerformed(evt);
            }
        });
        pnlSideMenu.add(btnMinhaBarbearia, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 180, 40));

        btnHistorico.setText("Histórico");
        btnHistorico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoricoActionPerformed(evt);
            }
        });
        pnlSideMenu.add(btnHistorico, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 180, 40));

        btnRelatorios.setText("Relatórios");
        btnRelatorios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRelatoriosActionPerformed(evt);
            }
        });
        pnlSideMenu.add(btnRelatorios, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 180, 40));

        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });
        pnlSideMenu.add(btnSair, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 540, 180, 40));

        getContentPane().add(pnlSideMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 600));

        pnlCards.setLayout(new java.awt.CardLayout());

        pnlHome.setBackground(new java.awt.Color(242, 242, 242));
        pnlHome.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitulo.setText("Painel de Controle");
        pnlHome.add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        btnAgendar.setBackground(new java.awt.Color(0, 102, 0));
        btnAgendar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAgendar.setForeground(new java.awt.Color(255, 255, 255));
        btnAgendar.setText("+ Novo Agendamento");
        btnAgendar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgendarActionPerformed(evt);
            }
        });
        pnlHome.add(btnAgendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 20, 170, 40));

        tblAgendamentos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Data/Hora", "Cliente", "Contato", "Serviço", "Barbeiro", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAgendamentos.setRowHeight(30);
        jScrollPane1.setViewportView(tblAgendamentos);

        pnlHome.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, 560, 320));

        lblSubtitulo.setText("Agendamentos Pendentes");
        pnlHome.add(lblSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 225, -1, -1));

        lblHintAgendamentos.setForeground(new java.awt.Color(102, 102, 102));
        lblHintAgendamentos.setText("- clique com botão direito para exibir ações");
        pnlHome.add(lblHintAgendamentos, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 225, -1, -1));

        pnlServicosGrid.setBackground(new java.awt.Color(255, 255, 255));
        pnlServicosGrid.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        jScrollPane4.setViewportView(pnlServicosGrid);

        pnlHome.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 560, 140));

        pnlCards.add(pnlHome, "cardHome");

        pnlMinhaBarbearia.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setText("Minha Barbearia");
        pnlMinhaBarbearia.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        pnlDadosGerais.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setText("Nome:");
        pnlDadosGerais.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));
        pnlDadosGerais.add(txtNomeB, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 530, 30));

        jLabel9.setText("CEP:");
        pnlDadosGerais.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));
        pnlDadosGerais.add(txtCEPB, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 150, 30));

        jLabel10.setText("Cultura e Valores:");
        pnlDadosGerais.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, -1));

        txtCulturaB.setColumns(20);
        txtCulturaB.setRows(5);
        jScrollPane5.setViewportView(txtCulturaB);

        pnlDadosGerais.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 530, 100));

        jLabel13.setText("Horário de Funcionamento (deixe em branco para não restringir):");
        pnlDadosGerais.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 265, -1, -1));

        jLabel14.setText("Abertura:");
        pnlDadosGerais.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, -1, -1));
        pnlDadosGerais.add(txtHorarioAbertura, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 285, 80, 30));

        jLabel15.setText("Fechamento:");
        pnlDadosGerais.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 290, -1, -1));
        pnlDadosGerais.add(txtHorarioFechamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 285, 80, 30));

        btnSalvarB.setText("Salvar Alterações");
        btnSalvarB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarBActionPerformed(evt);
            }
        });
        pnlDadosGerais.add(btnSalvarB, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 420, 150, 35));

        tabBarbearia.addTab("Dados Gerais", pnlDadosGerais);

        pnlGerenciarServicos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblGerenciarServicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "Preço"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(tblGerenciarServicos);

        pnlGerenciarServicos.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 530, 350));

        btnNovoServico.setText("Novo");
        btnNovoServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoServicoActionPerformed(evt);
            }
        });
        pnlGerenciarServicos.add(btnNovoServico, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 100, 30));

        btnEditarServicoB.setText("Editar");
        btnEditarServicoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarServicoBActionPerformed(evt);
            }
        });
        pnlGerenciarServicos.add(btnEditarServicoB, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 370, 100, 30));

        btnExcluirServico.setText("Excluir");
        btnExcluirServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirServicoActionPerformed(evt);
            }
        });
        pnlGerenciarServicos.add(btnExcluirServico, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 370, 100, 30));

        tabBarbearia.addTab("Serviços", pnlGerenciarServicos);

        pnlGerenciarBarbeiros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblGerenciarBarbeiros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(tblGerenciarBarbeiros);

        pnlGerenciarBarbeiros.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 530, 350));

        btnNovoBarbeiro.setText("Novo");
        btnNovoBarbeiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoBarbeiroActionPerformed(evt);
            }
        });
        pnlGerenciarBarbeiros.add(btnNovoBarbeiro, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 100, 30));

        btnEditarBarbeiroB.setText("Editar");
        btnEditarBarbeiroB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarBarbeiroBActionPerformed(evt);
            }
        });
        pnlGerenciarBarbeiros.add(btnEditarBarbeiroB, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 370, 100, 30));

        btnExcluirBarbeiro.setText("Excluir");
        btnExcluirBarbeiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirBarbeiroActionPerformed(evt);
            }
        });
        pnlGerenciarBarbeiros.add(btnExcluirBarbeiro, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 370, 100, 30));

        tabBarbearia.addTab("Barbeiros", pnlGerenciarBarbeiros);

        pnlClientes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setText("Buscar:");
        pnlClientes.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 13, -1, -1));

        txtBuscaClientes.setToolTipText("Filtra por nome ou contato");
        txtBuscaClientes.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltroClientes(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltroClientes(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltroClientes(); }
        });
        pnlClientes.add(txtBuscaClientes, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 8, 300, 30));

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "Contato"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(tblClientes);

        pnlClientes.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 530, 400));

        tabBarbearia.addTab("Clientes", pnlClientes);

        pnlMinhaBarbearia.add(tabBarbearia, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 560, 500));

        pnlCards.add(pnlMinhaBarbearia, "cardBarbearia");

        pnlHistorico.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setText("Histórico de Agendamentos");
        pnlHistorico.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel12.setText("Buscar:");
        pnlHistorico.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 78, -1, -1));

        txtBuscaHistorico.setToolTipText("Filtra por cliente, contato, serviço, barbeiro ou status");
        txtBuscaHistorico.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltroHistorico(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltroHistorico(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltroHistorico(); }
        });
        pnlHistorico.add(txtBuscaHistorico, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 73, 350, 30));

        tblHistorico.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Data/Hora", "Cliente", "Contato", "Serviço", "Barbeiro", "Origem", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(tblHistorico);

        pnlHistorico.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 560, 460));

        pnlCards.add(pnlHistorico, "cardHistorico");

        pnlRelatorios.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel17.setText("Relatórios");
        pnlRelatorios.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel18.setText("De:");
        pnlRelatorios.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 78, -1, -1));
        pnlRelatorios.add(txtRelatorioDe, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 73, 100, 30));

        jLabel19.setText("Até:");
        pnlRelatorios.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 78, -1, -1));
        pnlRelatorios.add(txtRelatorioAte, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 73, 100, 30));

        btnGerarRelatorio.setText("Gerar");
        btnGerarRelatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarRelatorioActionPerformed(evt);
            }
        });
        pnlRelatorios.add(btnGerarRelatorio, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 73, 100, 30));

        lblFaturamentoTotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblFaturamentoTotal.setText("Faturamento no período: —");
        pnlRelatorios.add(lblFaturamentoTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 400, -1));

        jLabel20.setText("Serviços mais vendidos");
        pnlRelatorios.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 155, -1, -1));

        tblServicosVendidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Serviço", "Qtd.", "Faturamento"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane10.setViewportView(tblServicosVendidos);

        pnlRelatorios.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 175, 270, 300));

        jLabel21.setText("Ranking de Barbeiros");
        pnlRelatorios.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 155, -1, -1));

        tblRankingBarbeiros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Barbeiro", "Atendimentos"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane11.setViewportView(tblRankingBarbeiros);

        pnlRelatorios.add(jScrollPane11, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 175, 270, 300));

        pnlCards.add(pnlRelatorios, "cardRelatorios");

        getContentPane().add(pnlCards, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 600, 600));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Alterna o {@link CardLayout} de {@code pnlCards} para o cartão "Home"
     * e recarrega os agendamentos pendentes e o grid de serviços, já que
     * esses dados podem ter mudado enquanto o usuário estava em outra aba.
     */
    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        cl.show(pnlCards, "cardHome");
        carregarAgendamentos();
        carregarGridServicos();
    }

    /**
     * Alterna o {@link CardLayout} de {@code pnlCards} para o cartão
     * "Minha Barbearia" e recarrega os dados da barbearia, a tabela de
     * serviços e a tabela de barbeiros.
     */
    private void btnMinhaBarbeariaActionPerformed(java.awt.event.ActionEvent evt) {
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        cl.show(pnlCards, "cardBarbearia");
        carregarDadosBarbearia();
        carregarTabelaServicos();
        carregarTabelaBarbeiros();
    }

    /**
     * Alterna o {@link CardLayout} de {@code pnlCards} para o cartão
     * "Histórico" e recarrega a tabela de histórico de agendamentos.
     */
    private void btnHistoricoActionPerformed(java.awt.event.ActionEvent evt) {
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        cl.show(pnlCards, "cardHistorico");
        carregarHistorico();
    }

    /**
     * Alterna o {@link CardLayout} de {@code pnlCards} para o cartão
     * "Relatórios". Se os campos de data ainda não foram preenchidos (campo
     * mascarado vazio), assume como período padrão o mês corrente até hoje.
     * Em seguida já dispara a geração do relatório para o período definido.
     */
    private void btnRelatoriosActionPerformed(java.awt.event.ActionEvent evt) {
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        cl.show(pnlCards, "cardRelatorios");
        if (UIUtil.campoMascaradoVazio(txtRelatorioDe)) {
            LocalDate hoje = LocalDate.now();
            txtRelatorioDe.setText(DateTimeUtil.formatDate(hoje.withDayOfMonth(1)));
            txtRelatorioAte.setText(DateTimeUtil.formatDate(hoje));
        }
        gerarRelatorio();
    }

    /** Aciona a geração do relatório para o período informado pelo usuário. */
    private void btnGerarRelatorioActionPerformed(java.awt.event.ActionEvent evt) {
        gerarRelatorio();
    }

    /**
     * Lê o intervalo de datas informado nos campos "De"/"Até", valida que a
     * data inicial não é posterior à final e usa o {@link RelatorioService}
     * para calcular o faturamento total, os serviços mais vendidos e o
     * ranking de barbeiros no período, preenchendo os respectivos
     * componentes da tela.
     * <p>
     * Observação: o faturamento é calculado com o preço ATUAL de cada
     * serviço, não com um snapshot do preço no momento do agendamento (não
     * existe esse histórico — só de nome/duração). Se o preço de um serviço
     * for alterado, relatórios de períodos passados passam a refletir o
     * preço novo retroativamente; é uma limitação conhecida, já documentada
     * no javadoc de {@link RelatorioService}.
     */
    private void gerarRelatorio() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;

            LocalDate inicio = DateTimeUtil.parseDate(txtRelatorioDe.getText().trim());
            LocalDate fim = DateTimeUtil.parseDate(txtRelatorioAte.getText().trim());
            if (inicio.isAfter(fim)) {
                JOptionPane.showMessageDialog(this, "A data \"De\" deve ser antes da data \"Até\".", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal total = relatorioService.faturamentoTotal(b.getId(), inicio, fim);
            lblFaturamentoTotal.setText("Faturamento no período: " + moedaFormat.format(total));

            DefaultTableModel modelServicos = (DefaultTableModel) tblServicosVendidos.getModel();
            modelServicos.setRowCount(0);
            for (ItemRelatorio item : relatorioService.servicosMaisVendidos(b.getId(), inicio, fim)) {
                modelServicos.addRow(new Object[]{ item.getNome(), item.getQuantidade(), moedaFormat.format(item.getTotal()) });
            }

            DefaultTableModel modelBarbeiros = (DefaultTableModel) tblRankingBarbeiros.getModel();
            modelBarbeiros.setRowCount(0);
            for (ItemRelatorio item : relatorioService.rankingBarbeiros(b.getId(), inicio, fim)) {
                modelBarbeiros.addRow(new Object[]{ item.getNome(), item.getQuantidade() });
            }
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy.", "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Encerra a aplicação imediatamente. */
    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    /**
     * Abre {@code TelaNovoAgendamento} sem serviço pré-selecionado (botão
     * "+ Novo Agendamento" da Home) e recarrega os agendamentos pendentes
     * quando a tela é fechada.
     */
    private void btnAgendarActionPerformed(java.awt.event.ActionEvent evt) {
        new TelaNovoAgendamento(this::carregarAgendamentos).setVisible(true);
    }

    /**
     * Salva as alterações da aba "Dados Gerais" (nome, CEP, cultura/valores
     * e horário de funcionamento) na barbearia atual. Valida que, quando
     * ambos os horários são informados, a abertura seja antes do
     * fechamento; campos de horário em branco (ou só com placeholder) são
     * tratados como "sem restrição de horário" via
     * {@link #parseHorarioOuNulo(String)}.
     */
    private void btnSalvarBActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            LocalTime abertura = parseHorarioOuNulo(txtHorarioAbertura.getText());
            LocalTime fechamento = parseHorarioOuNulo(txtHorarioFechamento.getText());
            if (abertura != null && fechamento != null && !abertura.isBefore(fechamento)) {
                JOptionPane.showMessageDialog(this, "O horário de abertura deve ser antes do fechamento.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            b.setNome(txtNomeB.getText());
            b.setCep(txtCEPB.getText());
            b.setCulturaValores(txtCulturaB.getText());
            b.setHorarioAbertura(abertura);
            b.setHorarioFechamento(fechamento);
            barbeariaDAO.atualizar(b);
            JOptionPane.showMessageDialog(this, "Dados atualizados!");
        } catch (SQLException e) {
            logger.error("Erro ao salvar dados da barbearia", e);
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Horário inválido. Use o formato HH:mm.", "Validação", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Texto do campo mascarado ainda com placeholders ("__:__") ou vazio conta como "sem restrição". */
    private LocalTime parseHorarioOuNulo(String texto) {
        if (texto == null || texto.replaceAll("[^0-9]", "").isEmpty()) return null;
        return DateTimeUtil.parseTime(texto.trim());
    }

    /**
     * Abre o diálogo de cadastro de serviço; se o usuário salvar, valida que
     * não existe outro serviço com o mesmo nome na barbearia, insere o novo
     * serviço no banco e recarrega a tabela de gerenciamento e o grid de
     * serviços da Home.
     */
    private void btnNovoServicoActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            DialogServico dlg = new DialogServico(this, true);
            dlg.setVisible(true);

            if (!dlg.isSalvo()) return;
            Servico s = dlg.getServico();

            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) throw new IllegalStateException("Sessão inválida: barbearia não definida.");

            if (servicoDAO.existePorNome(b.getId(), s.getNome(), 0)) {
                JOptionPane.showMessageDialog(this, "Já existe um serviço com esse nome.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            s.setBarbeariaId(b.getId());
            servicoDAO.inserir(s);

            carregarTabelaServicos();
            carregarGridServicos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar serviço:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Edita o serviço selecionado em {@code tblGerenciarServicos},
     * resolvendo a linha selecionada para o {@link Servico} correspondente
     * via {@code servicosGerenciarLista}. Abre o diálogo com uma cópia do
     * serviço original (para não refletir alterações na tabela caso o
     * usuário cancele), valida nome duplicado e, se salvo, persiste e
     * recarrega a tabela de gerenciamento e o grid de serviços da Home.
     */
    private void btnEditarServicoBActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblGerenciarServicos.getSelectedRow();
        if (row < 0 || servicosGerenciarLista == null || row >= servicosGerenciarLista.size()) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço para editar.");
            return;
        }

        try {
            Servico original = servicosGerenciarLista.get(row);
            // Trabalhar com uma cópia para evitar alterar a tabela caso cancele
            Servico copia = new Servico(original.getId(), original.getBarbeariaId(), original.getNome(), original.getPreco(), original.getImagemBase64(), original.getDuracaoMinutos());

            DialogServico dlg = new DialogServico(this, true, copia);
            dlg.setVisible(true);
            if (!dlg.isSalvo()) return;

            Servico editado = dlg.getServico();
            if (servicoDAO.existePorNome(editado.getBarbeariaId(), editado.getNome(), editado.getId())) {
                JOptionPane.showMessageDialog(this, "Já existe um serviço com esse nome.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            servicoDAO.atualizar(editado);
            carregarTabelaServicos();
            carregarGridServicos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao editar serviço:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exclui o serviço selecionado em {@code tblGerenciarServicos}, após
     * confirmação do usuário (avisando que agendamentos existentes podem
     * ser afetados), e recarrega a tabela de gerenciamento e o grid de
     * serviços da Home.
     */
    private void btnExcluirServicoActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblGerenciarServicos.getSelectedRow();
        if (row < 0 || servicosGerenciarLista == null || row >= servicosGerenciarLista.size()) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço para excluir.");
            return;
        }

        Servico s = servicosGerenciarLista.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Excluir o serviço '" + s.getNome() + "'?\nIsso pode afetar agendamentos existentes.",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            servicoDAO.deletar(s.getId());
            carregarTabelaServicos();
            carregarGridServicos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir serviço:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre o diálogo de cadastro de barbeiro; se o usuário salvar, valida
     * que não existe outro barbeiro com o mesmo nome na barbearia, insere o
     * novo barbeiro no banco e recarrega a tabela de gerenciamento de
     * barbeiros.
     */
    private void btnNovoBarbeiroActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            DialogBarbeiro dlg = new DialogBarbeiro(this, true);
            dlg.setVisible(true);
            if (!dlg.isSalvo()) return;

            Barbeiro barb = dlg.getBarbeiro();

            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) throw new IllegalStateException("Sessão inválida: barbearia não definida.");

            if (barbeiroDAO.existePorNome(b.getId(), barb.getNome(), 0)) {
                JOptionPane.showMessageDialog(this, "Já existe um barbeiro com esse nome.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            barb.setBarbeariaId(b.getId());
            barbeiroDAO.inserir(barb);

            carregarTabelaBarbeiros();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar barbeiro:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Edita o barbeiro selecionado em {@code tblGerenciarBarbeiros},
     * resolvendo a linha selecionada para o {@link Barbeiro} correspondente
     * via {@code barbeirosGerenciarLista}. Abre o diálogo com uma cópia do
     * barbeiro original (para não refletir alterações na tabela caso o
     * usuário cancele), valida nome duplicado e, se salvo, persiste e
     * recarrega a tabela de gerenciamento de barbeiros.
     */
    private void btnEditarBarbeiroBActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblGerenciarBarbeiros.getSelectedRow();
        if (row < 0 || barbeirosGerenciarLista == null || row >= barbeirosGerenciarLista.size()) {
            JOptionPane.showMessageDialog(this, "Selecione um barbeiro para editar.");
            return;
        }

        try {
            Barbeiro original = barbeirosGerenciarLista.get(row);
            Barbeiro copia = new Barbeiro(original.getId(), original.getBarbeariaId(), original.getNome(), original.getImagemBase64());

            DialogBarbeiro dlg = new DialogBarbeiro(this, true, copia);
            dlg.setVisible(true);
            if (!dlg.isSalvo()) return;

            Barbeiro editado = dlg.getBarbeiro();
            if (barbeiroDAO.existePorNome(editado.getBarbeariaId(), editado.getNome(), editado.getId())) {
                JOptionPane.showMessageDialog(this, "Já existe um barbeiro com esse nome.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            barbeiroDAO.atualizar(editado);
            carregarTabelaBarbeiros();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao editar barbeiro:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exclui o barbeiro selecionado em {@code tblGerenciarBarbeiros}, após
     * confirmação do usuário (avisando que agendamentos existentes podem
     * ser afetados), e recarrega a tabela de gerenciamento de barbeiros.
     */
    private void btnExcluirBarbeiroActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblGerenciarBarbeiros.getSelectedRow();
        if (row < 0 || barbeirosGerenciarLista == null || row >= barbeirosGerenciarLista.size()) {
            JOptionPane.showMessageDialog(this, "Selecione um barbeiro para excluir.");
            return;
        }

        Barbeiro barb = barbeirosGerenciarLista.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Excluir o barbeiro '" + barb.getNome() + "'?\nIsso pode afetar agendamentos existentes.",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            barbeiroDAO.deletar(barb.getId());
            carregarTabelaBarbeiros();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir barbeiro:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgendar;
    private javax.swing.JButton btnEditarBarbeiroB;
    private javax.swing.JButton btnEditarServicoB;
    private javax.swing.JButton btnExcluirBarbeiro;
    private javax.swing.JButton btnExcluirServico;
    private javax.swing.JButton btnGerarRelatorio;
    private javax.swing.JButton btnHistorico;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnMinhaBarbearia;
    private javax.swing.JButton btnNovoBarbeiro;
    private javax.swing.JButton btnNovoServico;
    private javax.swing.JButton btnRelatorios;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSalvarB;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel lblFaturamentoTotal;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblHintAgendamentos;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlCards;
    private javax.swing.JPanel pnlClientes;
    private javax.swing.JPanel pnlDadosGerais;
    private javax.swing.JPanel pnlGerenciarBarbeiros;
    private javax.swing.JPanel pnlGerenciarServicos;
    private javax.swing.JPanel pnlHistorico;
    private javax.swing.JPanel pnlHome;
    private javax.swing.JPanel pnlMinhaBarbearia;
    private javax.swing.JPanel pnlRelatorios;
    private javax.swing.JPanel pnlServicosGrid;
    private javax.swing.JPanel pnlSideMenu;
    private javax.swing.JTabbedPane tabBarbearia;
    private javax.swing.JTable tblAgendamentos;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTable tblGerenciarBarbeiros;
    private javax.swing.JTable tblGerenciarServicos;
    private javax.swing.JTable tblHistorico;
    private javax.swing.JTable tblRankingBarbeiros;
    private javax.swing.JTable tblServicosVendidos;
    private javax.swing.JTextField txtBuscaClientes;
    private javax.swing.JTextField txtBuscaHistorico;
    private javax.swing.JTextField txtCEPB;
    private javax.swing.JTextArea txtCulturaB;
    private javax.swing.JFormattedTextField txtHorarioAbertura;
    private javax.swing.JFormattedTextField txtHorarioFechamento;
    private javax.swing.JTextField txtNomeB;
    private javax.swing.JFormattedTextField txtRelatorioAte;
    private javax.swing.JFormattedTextField txtRelatorioDe;
    // End of variables declaration//GEN-END:variables
}
