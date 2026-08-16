package br.com.barberdesk.ui;

import br.com.barberdesk.app.FabricaDeServicos;
import br.com.barberdesk.service.AgendaService;
import br.com.barberdesk.ui.controller.AgendaController;
import br.com.barberdesk.ui.controller.BarbeariaController;
import br.com.barberdesk.ui.controller.CatalogoController;
import br.com.barberdesk.ui.controller.ClienteController;
import br.com.barberdesk.ui.controller.HistoricoController;
import br.com.barberdesk.ui.controller.RelatorioController;
import br.com.barberdesk.ui.support.UIUtil;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Tela principal do sistema, exibida logo após o login bem-sucedido.
 * <p>
 * Concentra toda a navegação da aplicação através de um menu lateral fixo
 * ({@code pnlSideMenu}) que troca os cartões de um {@link java.awt.CardLayout}
 * ({@code pnlCards}) entre quatro áreas:
 * <ul>
 *   <li><b>Home</b> - grid de serviços para agendamento rápido e tabela de
 *       agendamentos pendentes, com menu de contexto (botão direito) para
 *       iniciar, concluir ou cancelar um atendimento;</li>
 *   <li><b>Minha Barbearia</b> - abas para editar os dados gerais da
 *       barbearia (nome, CEP, cultura/valores, horário de funcionamento) e
 *       gerenciar (cadastrar/editar/excluir) serviços, barbeiros e a
 *       listagem de clientes;</li>
 *   <li><b>Histórico</b> - todos os agendamentos já registrados (qualquer
 *       status), com busca textual;</li>
 *   <li><b>Relatórios</b> - faturamento total, serviços mais vendidos e
 *       ranking de barbeiros num intervalo de datas.</li>
 * </ul>
 * A tela não implementa regras de negócio nem acessa o banco diretamente:
 * cada área é delegada a um controller especializado do pacote
 * {@code br.com.barberdesk.ui.controller} ({@link AgendaController},
 * {@link HistoricoController}, {@link CatalogoController},
 * {@link ClienteController}, {@link BarbeariaController},
 * {@link RelatorioController}), mantendo aqui apenas a orquestração de UI
 * gerada pelo NetBeans GUI Builder: montar a janela, ligar cada componente
 * ao controller responsável e reagir aos eventos repassando para o método
 * correspondente.
 */
public class TelaHome extends javax.swing.JFrame {

    private final FabricaDeServicos fabricaDeServicos = new FabricaDeServicos();

    private final AgendaController agendaController;
    private final HistoricoController historicoController;
    private final CatalogoController catalogoController;
    private final ClienteController clienteController;
    private final BarbeariaController barbeariaController;
    private final RelatorioController relatorioController;

    /**
     * Monta a janela: inicializa os componentes gerados pelo NetBeans, aplica
     * o ícone customizado da aplicação, monta os controllers de cada área
     * (injetando os componentes Swing e services que cada um precisa),
     * configura seus listeners/renderers e carrega os dados iniciais a
     * partir do banco.
     */
    public TelaHome() {
        initComponents();
        UIUtil.aplicarIcone(this);

        AgendaService agendaService = fabricaDeServicos.criarAgendaService();
        historicoController = new HistoricoController(this, tblHistorico, txtBuscaHistorico, agendaService);
        agendaController = new AgendaController(this, tblAgendamentos, agendaService,
                () -> historicoController.carregarHistorico());
        catalogoController = new CatalogoController(this, pnlServicosGrid, tblGerenciarServicos, tblGerenciarBarbeiros,
                fabricaDeServicos.criarCatalogoService(), agendaController::abrirNovoAgendamentoComServico);
        clienteController = new ClienteController(this, tblClientes, txtBuscaClientes, fabricaDeServicos.criarClienteService());
        barbeariaController = new BarbeariaController(this, txtNomeB, txtCEPB, txtCulturaB, txtHorarioAbertura,
                txtHorarioFechamento, fabricaDeServicos.criarBarbeariaService());
        relatorioController = new RelatorioController(this, txtRelatorioDe, txtRelatorioAte, lblFaturamentoTotal,
                tblServicosVendidos, tblRankingBarbeiros, fabricaDeServicos.criarRelatorioService());

        agendaController.configurar();
        historicoController.configurar();
        catalogoController.configurar();
        clienteController.configurar();

        carregarDados();
    }

    /**
     * Recarrega, de uma só vez, todos os dados exibidos pela tela através
     * dos controllers de cada área. Chamado no construtor para popular a
     * tela na abertura.
     */
    private void carregarDados() {
        agendaController.carregarAgendamentos();
        catalogoController.carregarGridServicos();
        catalogoController.carregarTabelaServicos();
        catalogoController.carregarTabelaBarbeiros();
        clienteController.carregarClientes();
        barbeariaController.carregarDados();
        historicoController.carregarHistorico();
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
        setTitle("Barbershop");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlSideMenu.setBackground(new java.awt.Color(51, 51, 51));
        pnlSideMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblLogo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblLogo.setForeground(new java.awt.Color(255, 255, 255));
        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setText("Barbershop");
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
        lblFaturamentoTotal.setText("Faturamento no período: -");
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
        agendaController.carregarAgendamentos();
        catalogoController.carregarGridServicos();
    }

    /**
     * Alterna o {@link CardLayout} de {@code pnlCards} para o cartão
     * "Minha Barbearia" e recarrega os dados da barbearia, a tabela de
     * serviços e a tabela de barbeiros.
     */
    private void btnMinhaBarbeariaActionPerformed(java.awt.event.ActionEvent evt) {
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        cl.show(pnlCards, "cardBarbearia");
        barbeariaController.carregarDados();
        catalogoController.carregarTabelaServicos();
        catalogoController.carregarTabelaBarbeiros();
    }

    /**
     * Alterna o {@link CardLayout} de {@code pnlCards} para o cartão
     * "Histórico" e recarrega a tabela de histórico de agendamentos.
     */
    private void btnHistoricoActionPerformed(java.awt.event.ActionEvent evt) {
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        cl.show(pnlCards, "cardHistorico");
        historicoController.carregarHistorico();
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
        relatorioController.prepararPeriodoPadraoSeVazio();
        relatorioController.gerar();
    }

    /** Aciona a geração do relatório para o período informado pelo usuário. */
    private void btnGerarRelatorioActionPerformed(java.awt.event.ActionEvent evt) {
        relatorioController.gerar();
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
        agendaController.abrirNovoAgendamento();
    }

    /** Salva as alterações da aba "Dados Gerais" na barbearia atual. */
    private void btnSalvarBActionPerformed(java.awt.event.ActionEvent evt) {
        barbeariaController.salvar();
    }

    /** Abre o diálogo de cadastro de serviço e recarrega as tabelas/grid ao salvar. */
    private void btnNovoServicoActionPerformed(java.awt.event.ActionEvent evt) {
        catalogoController.novoServico();
    }

    /** Edita o serviço selecionado em {@code tblGerenciarServicos}. */
    private void btnEditarServicoBActionPerformed(java.awt.event.ActionEvent evt) {
        catalogoController.editarServico();
    }

    /** Exclui o serviço selecionado em {@code tblGerenciarServicos}, após confirmação. */
    private void btnExcluirServicoActionPerformed(java.awt.event.ActionEvent evt) {
        catalogoController.excluirServico();
    }

    /** Abre o diálogo de cadastro de barbeiro e recarrega a tabela ao salvar. */
    private void btnNovoBarbeiroActionPerformed(java.awt.event.ActionEvent evt) {
        catalogoController.novoBarbeiro();
    }

    /** Edita o barbeiro selecionado em {@code tblGerenciarBarbeiros}. */
    private void btnEditarBarbeiroBActionPerformed(java.awt.event.ActionEvent evt) {
        catalogoController.editarBarbeiro();
    }

    /** Exclui o barbeiro selecionado em {@code tblGerenciarBarbeiros}, após confirmação. */
    private void btnExcluirBarbeiroActionPerformed(java.awt.event.ActionEvent evt) {
        catalogoController.excluirBarbeiro();
    }

    /** Aplica em {@code tblClientes} o filtro de texto digitado em {@code txtBuscaClientes}. */
    private void aplicarFiltroClientes() {
        clienteController.aplicarFiltro();
    }

    /** Aplica em {@code tblHistorico} o filtro de texto digitado em {@code txtBuscaHistorico}. */
    private void aplicarFiltroHistorico() {
        historicoController.aplicarFiltro();
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
