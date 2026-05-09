package br.com.barberdesk.ui;

import br.com.barberdesk.dao.*;
import br.com.barberdesk.model.*;
import br.com.barberdesk.service.AgendaService;
import br.com.barberdesk.util.AppContext;
import br.com.barberdesk.util.DateTimeUtil;
import br.com.barberdesk.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

public class TelaNovoAgendamento extends javax.swing.JFrame {

    private static final Logger logger = LoggerFactory.getLogger(TelaNovoAgendamento.class);

    private ServicoDAO servicoDAO = new ServicoDAO();
    private BarbeiroDAO barbeiroDAO = new BarbeiroDAO();
    private AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
    private final AgendaService agendaService = new AgendaService();
    private List<Servico> servicosLista;
    private List<Barbeiro> barbeirosLista;
    private Runnable onAgendamentoSalvo;

    public TelaNovoAgendamento() {
        initComponents();
        setLocationRelativeTo(null);
        // Ajuste de tamanho: o botão de salvar estava ficando fora da área visível
        // por causa das coordenadas do AbsoluteLayout.
        setMinimumSize(new java.awt.Dimension(560, 470));
        setSize(560, 470);
        setResizable(false);
        carregarCombos();
    }

    public TelaNovoAgendamento(Runnable onAgendamentoSalvo) {
        this();
        this.onAgendamentoSalvo = onAgendamentoSalvo;
    }

    public TelaNovoAgendamento(Servico s) {
        this();
        cbServico.setSelectedItem(s);
    }

    public TelaNovoAgendamento(Servico s, Runnable onAgendamentoSalvo) {
        this(s);
        this.onAgendamentoSalvo = onAgendamentoSalvo;
    }

    private void carregarCombos() {
        try {
            int bId = AppContext.getInstance().getBarbeariaAtual().getId();

            servicosLista = servicoDAO.listarPorBarbearia(bId);
            DefaultComboBoxModel<Servico> modelS = new DefaultComboBoxModel<>();
            for (Servico s : servicosLista) modelS.addElement(s);
            cbServico.setModel(modelS);

            barbeirosLista = barbeiroDAO.listarPorBarbearia(bId);
            DefaultComboBoxModel<Barbeiro> modelB = new DefaultComboBoxModel<>();
            for (Barbeiro b : barbeirosLista) modelB.addElement(b);
            cbBarbeiro.setModel(modelB);

            atualizarFotoBarbeiro();

        } catch (SQLException e) { logger.error("Erro ao carregar combos de serviço/barbeiro", e); }
    }

    private void atualizarFotoBarbeiro() {
        Barbeiro b = (Barbeiro) cbBarbeiro.getSelectedItem();
        if (b != null) {
            UIUtil.exibirMiniatura(lblFotoBarbeiro, b.getFotoCaminho());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtContato = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cbServico = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cbBarbeiro = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtData = UIUtil.criarCampoMascarado("##/##/####");
        jLabel6 = new javax.swing.JLabel();
        txtHora = UIUtil.criarCampoMascarado("##:##");
        jLabel7 = new javax.swing.JLabel();
        cbOrigem = new javax.swing.JComboBox<>();
        btnSalvar = new javax.swing.JButton();
        lblFotoBarbeiro = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("BarberDesk - Novo Agendamento");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Cliente:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));
        getContentPane().add(txtCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 360, 30));

        jLabel2.setText("Contato:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));
        getContentPane().add(txtContato, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 360, 30));

        jLabel3.setText("Serviço:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, -1, -1));
        getContentPane().add(cbServico, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 360, 30));

        jLabel4.setText("Barbeiro:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, -1, -1));

        cbBarbeiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBarbeiroActionPerformed(evt);
            }
        });
        getContentPane().add(cbBarbeiro, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 250, 30));

        jLabel5.setText("Data (dd/MM/yyyy):");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, -1, -1));
        getContentPane().add(txtData, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, 170, 30));

        jLabel6.setText("Hora (HH:mm):");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 260, -1, -1));
        getContentPane().add(txtHora, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 280, 170, 30));

        jLabel7.setText("Origem:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, -1, -1));

        cbOrigem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Presencial", "WhatsApp", "Instagram", "Telefone" }));
        getContentPane().add(cbOrigem, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 360, 30));

        btnSalvar.setBackground(new java.awt.Color(0, 102, 0));
        btnSalvar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSalvar.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvar.setText("Agendar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });
        // Botão estava fora da área visível (y=410) considerando Insets comuns.
        getContentPane().add(btnSalvar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 390, 360, 40));

        // Foto do barbeiro não pode sobrepor campos (estava cortando a caixa de hora).
        lblFotoBarbeiro.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        getContentPane().add(lblFotoBarbeiro, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 40, 130, 130));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cbBarbeiroActionPerformed(java.awt.event.ActionEvent evt) {
        atualizarFotoBarbeiro();
    }

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            String cliente = txtCliente.getText();
            String contato = txtContato.getText();

            if (cliente.isEmpty() || contato.isEmpty() || txtData.getText().isEmpty() || txtHora.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                return;
            }

            Barbeiro barb = (Barbeiro) cbBarbeiro.getSelectedItem();
            Servico serv = (Servico) cbServico.getSelectedItem();
            if (barb == null || serv == null) {
                JOptionPane.showMessageDialog(this, "Selecione o serviço e o barbeiro.");
                return;
            }

            LocalDateTime dataHora = DateTimeUtil.parseDateTime(txtData.getText(), txtHora.getText());
            Barbearia barbearia = AppContext.getInstance().getBarbeariaAtual();
            int bId = barbearia.getId();

            if (!agendaService.dentroDoHorarioFuncionamento(barbearia, dataHora, serv.getDuracaoMinutos())) {
                JOptionPane.showMessageDialog(this,
                        "Fora do horário de funcionamento da barbearia (" +
                                DateTimeUtil.formatTime(barbearia.getHorarioAbertura()) + " às " +
                                DateTimeUtil.formatTime(barbearia.getHorarioFechamento()) + ").",
                        "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (agendamentoDAO.verificarConflito(barb.getId(), dataHora, serv.getDuracaoMinutos())) {
                JOptionPane.showMessageDialog(this, "Erro: este barbeiro já possui um agendamento nesse horário!", "Conflito", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Agendamento a = new Agendamento();
            a.setBarbeariaId(bId);
            a.setServicoId(serv.getId());
            a.setBarbeiroId(barb.getId());
            // Snapshot para histórico: permite excluir serviço/barbeiro sem quebrar agendamentos antigos.
            a.setServicoNome(serv.getNome());
            a.setBarbeiroNome(barb.getNome());
            a.setDuracaoMinutos(serv.getDuracaoMinutos());
            a.setClienteNome(cliente);
            a.setContato(contato);
            a.setDataHora(dataHora);
            a.setOrigemContato(OrigemContato.valueOf(cbOrigem.getSelectedItem().toString().toUpperCase()));
            a.setStatus(StatusAgendamento.AGENDADO);
            
            agendamentoDAO.inserir(a);
            JOptionPane.showMessageDialog(this, "Agendamento realizado com sucesso!");

            // Atualiza a Home imediatamente (sem precisar trocar de tela)
            if (onAgendamentoSalvo != null) {
                try {
                    javax.swing.SwingUtilities.invokeLater(onAgendamentoSalvo);
                } catch (Exception ignored) {}
            }
            this.dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao agendar: " + e.getMessage());
            logger.error("Erro ao salvar novo agendamento", e);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSalvar;
    private javax.swing.JComboBox<Barbeiro> cbBarbeiro;
    private javax.swing.JComboBox<String> cbOrigem;
    private javax.swing.JComboBox<Servico> cbServico;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lblFotoBarbeiro;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtContato;
    private javax.swing.JFormattedTextField txtData;
    private javax.swing.JFormattedTextField txtHora;
    // End of variables declaration//GEN-END:variables
}
