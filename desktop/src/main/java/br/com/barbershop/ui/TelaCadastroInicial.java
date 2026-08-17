package br.com.barbershop.ui;

import br.com.barbershop.model.*;
import br.com.barbershop.app.AppContext;
import br.com.barbershop.app.FabricaDeServicos;
import br.com.barbershop.ui.support.UIUtil;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Tela de configuração inicial: primeiro contato com o sistema, exibida
 * pelo {@link br.com.barbershop.app.Main} quando ainda não existe nenhuma
 * barbearia cadastrada no banco. Reúne num só formulário os dados da
 * barbearia, os serviços/barbeiros iniciais (mantidos em memória até
 * salvar) e o usuário administrador, e delega a persistência para
 * {@link br.com.barbershop.service.SetupService#criarCadastroInicial}.
 * Gerada com GUI Builder do NetBeans - não editar o método initComponents().
 */
public class TelaCadastroInicial extends javax.swing.JFrame {

    /** Serviços adicionados nesta sessão de cadastro, ainda não gravados no banco. */
    private List<Servico> servicosTemporarios = new ArrayList<>();
    /** Barbeiros adicionados nesta sessão de cadastro, ainda não gravados no banco. */
    private List<Barbeiro> barbeirosTemporarios = new ArrayList<>();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaCadastroInicial() {
        initComponents();
        UIUtil.aplicarIcone(this);
        setLocationRelativeTo(null);
    }

    /** Redesenha a tabela de serviços a partir da lista em memória. */
    private void atualizarTabelaServicos() {
        DefaultTableModel model = (DefaultTableModel) tblServicos.getModel();
        model.setRowCount(0);
        for (Servico s : servicosTemporarios) {
            model.addRow(new Object[]{s.getNome(), "R$ " + s.getPreco()});
        }
    }

    /** Redesenha a tabela de barbeiros a partir da lista em memória. */
    private void atualizarTabelaBarbeiros() {
        DefaultTableModel model = (DefaultTableModel) tblBarbeiros.getModel();
        model.setRowCount(0);
        for (Barbeiro b : barbeirosTemporarios) {
            model.addRow(new Object[]{b.getNome()});
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        pnlDados = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNomeBarbearia = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCEP = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDataFundacao = UIUtil.criarCampoMascarado("##/##/####");
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtCultura = new javax.swing.JTextArea();
        pnlServicos = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblServicos = new javax.swing.JTable();
        btnAdicionarServico = new javax.swing.JButton();
        btnEditarServico = new javax.swing.JButton();
        btnRemoverServico = new javax.swing.JButton();
        pnlBarbeiros = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblBarbeiros = new javax.swing.JTable();
        btnAdicionarBarbeiro = new javax.swing.JButton();
        btnEditarBarbeiro = new javax.swing.JButton();
        btnRemoverBarbeiro = new javax.swing.JButton();
        pnlUsuario = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtLogin = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtSenha = new javax.swing.JPasswordField();
        btnSalvar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Barbershop - Cadastro Inicial");

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitulo.setText("Configuração Inicial");

        pnlDados.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados da Barbearia"));

        jLabel1.setText("Nome da Barbearia:");

        jLabel2.setText("CEP:");

        jLabel3.setText("Data de Fundação (dd/mm/aaaa):");

        jLabel4.setText("Valores e Cultura:");

        txtCultura.setColumns(20);
        txtCultura.setRows(3);
        jScrollPane1.setViewportView(txtCultura);

        javax.swing.GroupLayout pnlDadosLayout = new javax.swing.GroupLayout(pnlDados);
        pnlDados.setLayout(pnlDadosLayout);
        pnlDadosLayout.setHorizontalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNomeBarbearia)
                    .addGroup(pnlDadosLayout.createSequentialGroup()
                        .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(pnlDadosLayout.createSequentialGroup()
                                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(txtCEP, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)
                                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(txtDataFundacao, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        pnlDadosLayout.setVerticalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNomeBarbearia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCEP, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFundacao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlServicos.setBorder(javax.swing.BorderFactory.createTitledBorder("Serviços Oferecidos"));

        tblServicos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblServicos);

        btnAdicionarServico.setText("Adicionar");
        btnAdicionarServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarServicoActionPerformed(evt);
            }
        });

        btnEditarServico.setText("Editar");
        btnEditarServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarServicoActionPerformed(evt);
            }
        });

        btnRemoverServico.setText("Remover");
        btnRemoverServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverServicoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlServicosLayout = new javax.swing.GroupLayout(pnlServicos);
        pnlServicos.setLayout(pnlServicosLayout);
        pnlServicosLayout.setHorizontalGroup(
            pnlServicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlServicosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlServicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnlServicosLayout.createSequentialGroup()
                        .addComponent(btnAdicionarServico, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditarServico, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverServico, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 32, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlServicosLayout.setVerticalGroup(
            pnlServicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlServicosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlServicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdicionarServico)
                    .addComponent(btnEditarServico)
                    .addComponent(btnRemoverServico))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBarbeiros.setBorder(javax.swing.BorderFactory.createTitledBorder("Barbeiros / Funcionários"));

        tblBarbeiros.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblBarbeiros);

        btnAdicionarBarbeiro.setText("Adicionar");
        btnAdicionarBarbeiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarBarbeiroActionPerformed(evt);
            }
        });

        btnEditarBarbeiro.setText("Editar");
        btnEditarBarbeiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarBarbeiroActionPerformed(evt);
            }
        });

        btnRemoverBarbeiro.setText("Remover");
        btnRemoverBarbeiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverBarbeiroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlBarbeirosLayout = new javax.swing.GroupLayout(pnlBarbeiros);
        pnlBarbeiros.setLayout(pnlBarbeirosLayout);
        pnlBarbeirosLayout.setHorizontalGroup(
            pnlBarbeirosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBarbeirosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBarbeirosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnlBarbeirosLayout.createSequentialGroup()
                        .addComponent(btnAdicionarBarbeiro, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditarBarbeiro, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverBarbeiro, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 32, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlBarbeirosLayout.setVerticalGroup(
            pnlBarbeirosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBarbeirosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBarbeirosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdicionarBarbeiro)
                    .addComponent(btnEditarBarbeiro)
                    .addComponent(btnRemoverBarbeiro))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlUsuario.setBorder(javax.swing.BorderFactory.createTitledBorder("Usuário do Sistema"));

        jLabel5.setText("Login:");

        jLabel6.setText("Senha:");

        javax.swing.GroupLayout pnlUsuarioLayout = new javax.swing.GroupLayout(pnlUsuario);
        pnlUsuario.setLayout(pnlUsuarioLayout);
        pnlUsuarioLayout.setHorizontalGroup(
            pnlUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUsuarioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txtLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(pnlUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlUsuarioLayout.setVerticalGroup(
            pnlUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUsuarioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnSalvar.setBackground(new java.awt.Color(0, 102, 0));
        btnSalvar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSalvar.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvar.setText("Criar Barbearia");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitulo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pnlDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlServicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlBarbeiros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblTitulo)
                .addGap(20, 20, 20)
                .addComponent(pnlDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlServicos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlBarbeiros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /** Abre o diálogo de novo serviço e, se salvo, adiciona à lista temporária. */
    private void btnAdicionarServicoActionPerformed(java.awt.event.ActionEvent evt) {
        DialogServico dialog = new DialogServico(this, true);
        dialog.setVisible(true);
        if (dialog.isSalvo()) {
            servicosTemporarios.add(dialog.getServico());
            atualizarTabelaServicos();
        }
    }

    /** Abre o diálogo de edição para o serviço selecionado na tabela. */
    private void btnEditarServicoActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblServicos.getSelectedRow();
        if (row >= 0) {
            Servico s = servicosTemporarios.get(row);
            DialogServico dialog = new DialogServico(this, true, s);
            dialog.setVisible(true);
            if (dialog.isSalvo()) {
                atualizarTabelaServicos();
            }
        }
    }

    /** Remove o serviço selecionado da lista temporária (nada é gravado no banco ainda). */
    private void btnRemoverServicoActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblServicos.getSelectedRow();
        if (row >= 0) {
            servicosTemporarios.remove(row);
            atualizarTabelaServicos();
        }
    }

    /** Abre o diálogo de novo barbeiro e, se salvo, adiciona à lista temporária. */
    private void btnAdicionarBarbeiroActionPerformed(java.awt.event.ActionEvent evt) {
        DialogBarbeiro dialog = new DialogBarbeiro(this, true);
        dialog.setVisible(true);
        if (dialog.isSalvo()) {
            barbeirosTemporarios.add(dialog.getBarbeiro());
            atualizarTabelaBarbeiros();
        }
    }

    /** Abre o diálogo de edição para o barbeiro selecionado na tabela. */
    private void btnEditarBarbeiroActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblBarbeiros.getSelectedRow();
        if (row >= 0) {
            Barbeiro b = barbeirosTemporarios.get(row);
            DialogBarbeiro dialog = new DialogBarbeiro(this, true, b);
            dialog.setVisible(true);
            if (dialog.isSalvo()) {
                atualizarTabelaBarbeiros();
            }
        }
    }

    /** Remove o barbeiro selecionado da lista temporária (nada é gravado no banco ainda). */
    private void btnRemoverBarbeiroActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblBarbeiros.getSelectedRow();
        if (row >= 0) {
            barbeirosTemporarios.remove(row);
            atualizarTabelaBarbeiros();
        }
    }

    /**
     * Valida os campos obrigatórios (inclusive pelo menos um serviço e um
     * barbeiro já adicionados) e persiste tudo de uma vez via SetupService -
     * barbearia, serviços, barbeiros e usuário administrador nascem juntos
     * na mesma transação lógica de cadastro inicial.
     */
    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {
        String nome = txtNomeBarbearia.getText().trim();
        String cep = txtCEP.getText().trim();
        String dataStr = txtDataFundacao.getText().trim();
        String cultura = txtCultura.getText().trim();
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty() || servicosTemporarios.isEmpty() || barbeirosTemporarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos e adicione pelo menos um serviço e um barbeiro.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (UIUtil.campoMascaradoVazio(txtDataFundacao)) {
            JOptionPane.showMessageDialog(this, "Informe a data de fundação.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate dataFundacao = LocalDate.parse(dataStr, dtf);

            Barbearia b = new Barbearia(nome, cep, dataFundacao, cultura);
            Usuario u = new FabricaDeServicos().criarSetupService()
                    .criarCadastroInicial(b, login, senha, servicosTemporarios, barbeirosTemporarios);

            AppContext.getInstance().setSessaoAtual(new Session(u, b));

            JOptionPane.showMessageDialog(this, "Barbearia criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            new TelaHome().setVisible(true);
            this.dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionarBarbeiro;
    private javax.swing.JButton btnAdicionarServico;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEditarBarbeiro;
    private javax.swing.JButton btnEditarServico;
    private javax.swing.JButton btnRemoverBarbeiro;
    private javax.swing.JButton btnRemoverServico;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlBarbeiros;
    private javax.swing.JPanel pnlDados;
    private javax.swing.JPanel pnlServicos;
    private javax.swing.JPanel pnlUsuario;
    private javax.swing.JTable tblBarbeiros;
    private javax.swing.JTable tblServicos;
    private javax.swing.JTextField txtCEP;
    private javax.swing.JTextArea txtCultura;
    private javax.swing.JFormattedTextField txtDataFundacao;
    private javax.swing.JTextField txtLogin;
    private javax.swing.JTextField txtNomeBarbearia;
    private javax.swing.JPasswordField txtSenha;
    // End of variables declaration//GEN-END:variables
}
