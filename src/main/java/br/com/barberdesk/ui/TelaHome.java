package br.com.barberdesk.ui;

import br.com.barberdesk.dao.*;
import br.com.barberdesk.model.*;
import br.com.barberdesk.util.AppContext;
import br.com.barberdesk.util.DateTimeUtil;
import br.com.barberdesk.util.UIUtil;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class TelaHome extends javax.swing.JFrame {

    private AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
    private ServicoDAO servicoDAO = new ServicoDAO();
    private BarbeariaDAO barbeariaDAO = new BarbeariaDAO();
    private BarbeiroDAO barbeiroDAO = new BarbeiroDAO();
    private final NumberFormat moedaFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // Listas de apoio para seleção nas tabelas de gerenciamento
    private List<Servico> servicosGerenciarLista;
    private List<Barbeiro> barbeirosGerenciarLista;

    public TelaHome() {
        initComponents();
        configurarTabela();
        carregarDados();
    }

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
    }

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
                itemIniciar.addActionListener(e -> mudarStatus(a, StatusAgendamento.EM_ATENDIMENTO));
                menu.add(itemIniciar);
            } else if (a.getStatus() == StatusAgendamento.EM_ATENDIMENTO) {
                JMenuItem itemTerminar = new JMenuItem("Terminar Serviço");
                itemTerminar.addActionListener(e -> mudarStatus(a, StatusAgendamento.CONCLUIDO));
                menu.add(itemTerminar);
            }

            if (a.getStatus() != StatusAgendamento.CONCLUIDO && a.getStatus() != StatusAgendamento.CANCELADO) {
                JMenuItem itemCancelar = new JMenuItem("Cancelar Agendamento");
                itemCancelar.addActionListener(e -> mudarStatus(a, StatusAgendamento.CANCELADO));
                menu.add(itemCancelar);
            }

            menu.show(comp, x, y);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void mudarStatus(Agendamento a, StatusAgendamento novo) {
        try {
            a.setStatus(novo);
            agendamentoDAO.atualizar(a);
            carregarAgendamentos();
            carregarHistorico();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void carregarDados() {
        carregarAgendamentos();
        carregarGridServicos();
        carregarTabelaServicos();
        carregarTabelaBarbeiros();
        carregarDadosBarbearia();
        carregarHistorico();
    }

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

    public void carregarAgendamentos() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;
            List<Agendamento> lista = agendamentoDAO.listarPendentesPorBarbearia(b.getId());
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
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void carregarDadosBarbearia() {
        try {
            Barbearia ctx = AppContext.getInstance().getBarbeariaAtual();
            if (ctx == null) return;

            // Recarrega do banco para evitar inconsistências caso o AppContext esteja desatualizado
            Barbearia b = barbeariaDAO.buscarPorId(ctx.getId());
            if (b == null) b = ctx;
            AppContext.getInstance().setBarbeariaAtual(b);

            txtNomeB.setText(b.getNome() != null ? b.getNome() : "");
            txtCEPB.setText(b.getCep() != null ? b.getCep() : "");
            txtCulturaB.setText(b.getCulturaValores() != null ? b.getCulturaValores() : "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar dados da barbearia:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarHistorico() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;
            List<Agendamento> lista = agendamentoDAO.listarPorBarbearia(b.getId());
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

    private void abrirNovoAgendamentoComServico(Servico s) {
        new TelaNovoAgendamento(s, this::carregarAgendamentos).setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSideMenu = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        btnHome = new javax.swing.JButton();
        btnMinhaBarbearia = new javax.swing.JButton();
        btnHistorico = new javax.swing.JButton();
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
        pnlHistorico = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblHistorico = new javax.swing.JTable();

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

        pnlMinhaBarbearia.add(tabBarbearia, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 560, 500));

        pnlCards.add(pnlMinhaBarbearia, "cardBarbearia");

        pnlHistorico.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setText("Histórico de Agendamentos");
        pnlHistorico.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

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

        pnlHistorico.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 560, 500));

        pnlCards.add(pnlHistorico, "cardHistorico");

        getContentPane().add(pnlCards, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 600, 600));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        cl.show(pnlCards, "cardHome");
        carregarAgendamentos();
        carregarGridServicos();
    }

    private void btnMinhaBarbeariaActionPerformed(java.awt.event.ActionEvent evt) {
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        cl.show(pnlCards, "cardBarbearia");
        carregarDadosBarbearia();
        carregarTabelaServicos();
        carregarTabelaBarbeiros();
    }

    private void btnHistoricoActionPerformed(java.awt.event.ActionEvent evt) {
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        cl.show(pnlCards, "cardHistorico");
        carregarHistorico();
    }

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    private void btnAgendarActionPerformed(java.awt.event.ActionEvent evt) {
        new TelaNovoAgendamento(this::carregarAgendamentos).setVisible(true);
    }

    private void btnSalvarBActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            b.setNome(txtNomeB.getText());
            b.setCep(txtCEPB.getText());
            b.setCulturaValores(txtCulturaB.getText());
            barbeariaDAO.atualizar(b);
            JOptionPane.showMessageDialog(this, "Dados atualizados!");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void btnNovoServicoActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            DialogServico dlg = new DialogServico(this, true);
            dlg.setVisible(true);

            if (!dlg.isSalvo()) return;
            Servico s = dlg.getServico();

            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) throw new IllegalStateException("Sessão inválida: barbearia não definida.");

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

    private void btnEditarServicoBActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblGerenciarServicos.getSelectedRow();
        if (row < 0 || servicosGerenciarLista == null || row >= servicosGerenciarLista.size()) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço para editar.");
            return;
        }

        try {
            Servico original = servicosGerenciarLista.get(row);
            // Trabalhar com uma cópia para evitar alterar a tabela caso cancele
            Servico copia = new Servico(original.getId(), original.getBarbeariaId(), original.getNome(), original.getPreco(), original.getImagemPath());

            DialogServico dlg = new DialogServico(this, true, copia);
            dlg.setVisible(true);
            if (!dlg.isSalvo()) return;

            servicoDAO.atualizar(dlg.getServico());
            carregarTabelaServicos();
            carregarGridServicos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao editar serviço:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

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

    private void btnNovoBarbeiroActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            DialogBarbeiro dlg = new DialogBarbeiro(this, true);
            dlg.setVisible(true);
            if (!dlg.isSalvo()) return;

            Barbeiro barb = dlg.getBarbeiro();

            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) throw new IllegalStateException("Sessão inválida: barbearia não definida.");

            barb.setBarbeariaId(b.getId());
            barbeiroDAO.inserir(barb);

            carregarTabelaBarbeiros();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar barbeiro:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnEditarBarbeiroBActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblGerenciarBarbeiros.getSelectedRow();
        if (row < 0 || barbeirosGerenciarLista == null || row >= barbeirosGerenciarLista.size()) {
            JOptionPane.showMessageDialog(this, "Selecione um barbeiro para editar.");
            return;
        }

        try {
            Barbeiro original = barbeirosGerenciarLista.get(row);
            Barbeiro copia = new Barbeiro(original.getId(), original.getBarbeariaId(), original.getNome(), original.getImagemPath());

            DialogBarbeiro dlg = new DialogBarbeiro(this, true, copia);
            dlg.setVisible(true);
            if (!dlg.isSalvo()) return;

            barbeiroDAO.atualizar(dlg.getBarbeiro());
            carregarTabelaBarbeiros();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao editar barbeiro:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

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
    private javax.swing.JButton btnHistorico;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnMinhaBarbearia;
    private javax.swing.JButton btnNovoBarbeiro;
    private javax.swing.JButton btnNovoServico;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSalvarB;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblHintAgendamentos;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlCards;
    private javax.swing.JPanel pnlDadosGerais;
    private javax.swing.JPanel pnlGerenciarBarbeiros;
    private javax.swing.JPanel pnlGerenciarServicos;
    private javax.swing.JPanel pnlHistorico;
    private javax.swing.JPanel pnlHome;
    private javax.swing.JPanel pnlMinhaBarbearia;
    private javax.swing.JPanel pnlServicosGrid;
    private javax.swing.JPanel pnlSideMenu;
    private javax.swing.JTabbedPane tabBarbearia;
    private javax.swing.JTable tblAgendamentos;
    private javax.swing.JTable tblGerenciarBarbeiros;
    private javax.swing.JTable tblGerenciarServicos;
    private javax.swing.JTable tblHistorico;
    private javax.swing.JTextField txtCEPB;
    private javax.swing.JTextArea txtCulturaB;
    private javax.swing.JTextField txtNomeB;
    // End of variables declaration//GEN-END:variables
}
