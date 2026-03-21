package br.com.barberdesk.ui;

import br.com.barberdesk.dao.AgendamentoDAO;
import br.com.barberdesk.dao.BarbeiroDAO;
import br.com.barberdesk.dao.ServicoDAO;
import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.Barbeiro;
import br.com.barberdesk.model.OrigemContato;
import br.com.barberdesk.model.Servico;
import br.com.barberdesk.model.StatusAgendamento;
import br.com.barberdesk.util.AppContext;
import br.com.barberdesk.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaEditarAgendamento extends JFrame {

    private final int agendamentoId;

    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();
    private final BarbeiroDAO barbeiroDAO = new BarbeiroDAO();

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JTextField txtCliente;
    private JTextField txtContato;
    private JTextField txtDataHora;
    private JComboBox<Servico> cbServico;
    private JComboBox<Barbeiro> cbBarbeiro;
    private JComboBox<OrigemContato> cbOrigem;
    private JLabel lblStatus;
    private JLabel lblFotoBarbeiro;

    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnAcaoStatus;

    private Agendamento atual;

    public TelaEditarAgendamento(int agendamentoId) {
        this.agendamentoId = agendamentoId;
        setTitle("BarberDesk - Editar Agendamento");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        initUI();
        carregarDados();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        int row = 0;

        lblStatus = new JLabel("Status: -");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        form.add(lblStatus, gc);

        row++;

        txtCliente = new JTextField();
        addRow(form, gc, row++, "Cliente:", txtCliente);

        txtContato = new JTextField();
        addRow(form, gc, row++, "Contato:", txtContato);

        txtDataHora = new JTextField();
        addRow(form, gc, row++, "Data/Hora (dd/MM/yyyy HH:mm):", txtDataHora);

        cbServico = new JComboBox<>();
        addRow(form, gc, row++, "Serviço:", cbServico);

        cbBarbeiro = new JComboBox<>();
        cbBarbeiro.addActionListener(e -> atualizarFotoBarbeiro());
        addRow(form, gc, row++, "Barbeiro:", cbBarbeiro);

        lblFotoBarbeiro = new JLabel();
        lblFotoBarbeiro.setPreferredSize(new Dimension(100, 100));
        lblFotoBarbeiro.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        gc.gridx = 1; gc.gridy = row; gc.gridwidth = 1; gc.fill = GridBagConstraints.NONE; gc.anchor = GridBagConstraints.WEST;
        form.add(lblFotoBarbeiro, gc);
        row++;

        cbOrigem = new JComboBox<>(OrigemContato.values());
        gc.fill = GridBagConstraints.HORIZONTAL;
        addRow(form, gc, row++, "Origem:", cbOrigem);

        root.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnExcluir = new JButton("Excluir");
        btnSalvar = new JButton("Salvar");
        btnAcaoStatus = new JButton("Iniciar");

        btnExcluir.addActionListener(e -> excluir());
        btnSalvar.addActionListener(e -> salvar());
        btnAcaoStatus.addActionListener(e -> alternarStatus());

        actions.add(btnAcaoStatus);
        actions.add(btnExcluir);
        actions.add(btnSalvar);

        root.add(actions, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void addRow(JPanel form, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridwidth = 1;
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0;
        form.add(new JLabel(label), gc);

        gc.gridx = 1; gc.gridy = row; gc.weightx = 1;
        form.add(field, gc);
    }

    private void carregarDados() {
        try {
            int bId = AppContext.getInstance().getBarbeariaAtual().getId();

            List<Servico> servicos = servicoDAO.listarPorBarbearia(bId);
            DefaultComboBoxModel<Servico> modelS = new DefaultComboBoxModel<>();
            for (Servico s : servicos) modelS.addElement(s);
            cbServico.setModel(modelS);

            List<Barbeiro> barbeiros = barbeiroDAO.listarPorBarbearia(bId);
            DefaultComboBoxModel<Barbeiro> modelB = new DefaultComboBoxModel<>();
            for (Barbeiro b : barbeiros) modelB.addElement(b);
            cbBarbeiro.setModel(modelB);

            atual = agendamentoDAO.buscarPorId(agendamentoId);
            if (atual == null) {
                JOptionPane.showMessageDialog(this, "Agendamento não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            txtCliente.setText(atual.getClienteNome());
            txtContato.setText(atual.getContato());
            txtDataHora.setText(atual.getDataHora() != null ? atual.getDataHora().format(dtf) : "");
            cbOrigem.setSelectedItem(atual.getOrigemContato());

            selecionarComboPorId(cbServico, atual.getServicoId());
            selecionarBarbeiroPorId(cbBarbeiro, atual.getBarbeiroId());

            atualizarFotoBarbeiro();
            atualizarUIStatus();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void atualizarFotoBarbeiro() {
        Barbeiro b = (Barbeiro) cbBarbeiro.getSelectedItem();
        if (b != null) {
            UIUtil.exibirMiniatura(lblFotoBarbeiro, b.getFotoCaminho(), 100, 100);
        }
    }

    private void selecionarComboPorId(JComboBox<Servico> combo, int id) {
        ComboBoxModel<Servico> model = combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selecionarBarbeiroPorId(JComboBox<Barbeiro> combo, int id) {
        ComboBoxModel<Barbeiro> model = combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void atualizarUIStatus() {
        StatusAgendamento st = atual.getStatus();
        lblStatus.setText("Status: " + (st != null ? st.name() : "-"));

        if (st == StatusAgendamento.AGENDADO) {
            btnAcaoStatus.setText("Iniciar Serviço");
            btnAcaoStatus.setEnabled(true);
        } else if (st == StatusAgendamento.EM_ATENDIMENTO) {
            btnAcaoStatus.setText("Terminar Serviço");
            btnAcaoStatus.setEnabled(true);
        } else {
            btnAcaoStatus.setEnabled(false);
        }
    }

    private void salvar() {
        try {
            String cliente = txtCliente.getText().trim();
            String contato = txtContato.getText().trim();
            String dh = txtDataHora.getText().trim();

            if (cliente.isEmpty() || contato.isEmpty() || dh.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha Cliente, Contato e Data/Hora.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDateTime dataHora = LocalDateTime.parse(dh, dtf);
            Servico servico = (Servico) cbServico.getSelectedItem();
            Barbeiro barbeiro = (Barbeiro) cbBarbeiro.getSelectedItem();
            OrigemContato origem = (OrigemContato) cbOrigem.getSelectedItem();

            if (servico == null || barbeiro == null || origem == null) {
                JOptionPane.showMessageDialog(this, "Selecione Serviço, Barbeiro e Origem.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            atual.setClienteNome(cliente);
            atual.setContato(contato);
            atual.setDataHora(dataHora);
            atual.setServicoId(servico.getId());
            atual.setBarbeiroId(barbeiro.getId());
            // Mantém snapshot atualizado
            atual.setServicoNome(servico.getNome());
            atual.setBarbeiroNome(barbeiro.getNome());
            atual.setDuracaoMinutos(servico.getDuracaoMinutos());
            atual.setOrigemContato(origem);

            agendamentoDAO.atualizar(atual);
            JOptionPane.showMessageDialog(this, "Agendamento atualizado.");
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data/Hora inválida. Use dd/MM/yyyy HH:mm", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        int opt = JOptionPane.showConfirmDialog(this, "Excluir este agendamento?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;
        try {
            agendamentoDAO.deletar(agendamentoId);
            JOptionPane.showMessageDialog(this, "Agendamento excluído.");
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alternarStatus() {
        try {
            if (atual.getStatus() == StatusAgendamento.AGENDADO) {
                atual.setStatus(StatusAgendamento.EM_ATENDIMENTO);
            } else if (atual.getStatus() == StatusAgendamento.EM_ATENDIMENTO) {
                atual.setStatus(StatusAgendamento.CONCLUIDO);
            } else { return; }

            agendamentoDAO.atualizar(atual);
            atualizarUIStatus();
            JOptionPane.showMessageDialog(this, "Status atualizado para " + atual.getStatus().name() + ".");
            if (atual.getStatus() == StatusAgendamento.CONCLUIDO) dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar status: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
