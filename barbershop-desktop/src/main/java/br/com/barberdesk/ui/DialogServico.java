package br.com.barberdesk.ui;

import br.com.barberdesk.model.Servico;
import br.com.barberdesk.util.ImageStorageUtil;
import br.com.barberdesk.ui.support.UIUtil;
import java.io.IOException;
import java.math.BigDecimal;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Diálogo modal de cadastro/edição de serviço, com preview de foto.
 *
 * <p>Permite informar nome, preço e duração (em minutos) do serviço, além
 * de escolher uma imagem ilustrativa. A imagem selecionada é convertida e
 * armazenada como Base64 (ver {@link ImageStorageUtil#paraBase64}), não
 * como caminho de arquivo, para que fique persistida junto com o restante
 * dos dados do serviço.</p>
 *
 * <p>A tela que abre este diálogo deve, após ele ser fechado, consultar
 * {@link #isSalvo()} para saber se o usuário confirmou o cadastro e, em
 * caso positivo, obter o resultado com {@link #getServico()}.</p>
 */
public class DialogServico extends javax.swing.JDialog {

    /** Serviço sendo criado ou editado; é o objeto devolvido por {@link #getServico()}. */
    private Servico servico;
    /** Indica se o usuário confirmou o formulário clicando em "Salvar". */
    private boolean salvo = false;
    /** Imagem do serviço já convertida para Base64 (vazia quando não há imagem). */
    private String imagemBase64 = "";

    /**
     * Cria o diálogo para cadastro de um novo serviço, com um objeto
     * {@link Servico} vazio a ser preenchido pelo usuário.
     *
     * @param parent janela pai do diálogo
     * @param modal  se {@code true}, bloqueia a janela pai enquanto o diálogo estiver aberto
     */
    public DialogServico(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        UIUtil.aplicarIcone(this);
        this.servico = new Servico();
    }

    /**
     * Cria o diálogo para edição de um serviço já existente, pré-preenchendo
     * os campos da tela com os dados recebidos.
     *
     * @param parent  janela pai do diálogo
     * @param modal   se {@code true}, bloqueia a janela pai enquanto o diálogo estiver aberto
     * @param servico serviço a ser editado
     */
    public DialogServico(java.awt.Frame parent, boolean modal, Servico servico) {
        super(parent, modal);
        initComponents();
        UIUtil.aplicarIcone(this);
        this.servico = servico;
        preencherCampos();
    }

    /**
     * Preenche os campos da tela (nome, preço, duração e miniatura de
     * imagem) com os dados do serviço recebido no construtor de edição.
     * Quando a duração cadastrada não é válida (zero ou negativa), usa
     * 30 minutos como valor padrão exibido no spinner.
     */
    private void preencherCampos() {
        if (servico != null) {
            txtNome.setText(servico.getNome());
            txtPreco.setText(servico.getPreco() != null ? servico.getPreco().toString() : "");
            spnDuracao.setValue(servico.getDuracaoMinutos() > 0 ? servico.getDuracaoMinutos() : 30);
            this.imagemBase64 = servico.getImagemBase64() != null ? servico.getImagemBase64() : "";
            UIUtil.exibirMiniatura(lblPreview, imagemBase64);
        }
    }

    /**
     * Indica se o usuário confirmou o cadastro/edição clicando em "Salvar".
     * Deve ser consultado pela tela chamadora antes de usar {@link #getServico()}.
     *
     * @return {@code true} se o formulário foi salvo; {@code false} se foi cancelado/fechado
     */
    public boolean isSalvo() {
        return salvo;
    }

    /**
     * Obtém o serviço criado ou editado neste diálogo.
     *
     * @return o serviço com os dados preenchidos na tela
     */
    public Servico getServico() {
        return servico;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPreco = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        spnDuracao = new javax.swing.JSpinner();
        lblPreview = new javax.swing.JLabel();
        btnEscolherImagem = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gerenciar Serviço");
        setModal(true);
        setResizable(false);

        jLabel1.setText("Nome do Serviço:");

        jLabel2.setText("Preço (R$):");

        jLabel3.setText("Duração (min):");
        spnDuracao.setModel(new javax.swing.SpinnerNumberModel(30, 5, 480, 5));

        lblPreview.setBackground(new java.awt.Color(204, 204, 204));
        lblPreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPreview.setText("Sem Imagem");
        lblPreview.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPreview.setOpaque(true);

        btnEscolherImagem.setText("Escolher...");
        btnEscolherImagem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEscolherImagemActionPerformed(evt);
            }
        });

        btnSalvar.setText("Salvar");
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(spnDuracao, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEscolherImagem, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnDuracao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEscolherImagem)))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Abre um seletor de arquivos para o usuário escolher uma imagem
     * (jpg/png/jpeg), converte o arquivo escolhido para Base64 e atualiza
     * a miniatura de preview exibida na tela.
     */
    private void btnEscolherImagemActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagens", "jpg", "png", "jpeg");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                this.imagemBase64 = ImageStorageUtil.paraBase64(chooser.getSelectedFile());
                UIUtil.exibirMiniatura(lblPreview, imagemBase64);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar a imagem: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Valida o formulário (nome preenchido, preço numérico e maior que
     * zero) e, se tudo estiver correto, grava os dados no objeto
     * {@link #servico}, marca {@link #salvo} como {@code true} e fecha o
     * diálogo. A vírgula digitada no campo de preço é convertida para
     * ponto antes da conversão para {@link BigDecimal}, para aceitar o
     * formato numérico brasileiro.
     */
    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {
        String nome = txtNome.getText().trim();
        String precoStr = txtPreco.getText().trim().replace(",", ".");

        if (nome.isEmpty() || precoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha o nome e o preço.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal preco = new BigDecimal(precoStr);
            if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "O preço deve ser maior que zero.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            servico.setNome(nome);
            servico.setPreco(preco);
            servico.setImagemBase64(imagemBase64);
            servico.setDuracaoMinutos((Integer) spnDuracao.getValue());
            this.salvo = true;
            this.dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cancela o cadastro/edição e fecha o diálogo sem marcar {@link #salvo}
     * como {@code true} (ou seja, sem gravar as alterações feitas na tela).
     */
    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEscolherImagem;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblPreview;
    private javax.swing.JSpinner spnDuracao;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtPreco;
    // End of variables declaration//GEN-END:variables
}
