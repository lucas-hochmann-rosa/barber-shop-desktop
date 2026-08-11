package br.com.barberdesk.ui.controller;

import br.com.barberdesk.app.AppContext;
import br.com.barberdesk.model.Barbearia;
import br.com.barberdesk.model.Session;
import br.com.barberdesk.service.BarbeariaService;
import br.com.barberdesk.util.DateTimeUtil;
import java.awt.Component;
import java.sql.SQLException;
import java.time.LocalTime;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controla a aba "Dados Gerais" de "Minha Barbearia": exibição e edição do
 * nome, CEP, cultura/valores e horário de funcionamento. Extraído de
 * {@code TelaHome} (SRP — Fase 5).
 */
public class BarbeariaController {

    private static final Logger logger = LoggerFactory.getLogger(BarbeariaController.class);

    private final Component parent;
    private final JTextField txtNome;
    private final JTextField txtCep;
    private final JTextArea txtCultura;
    private final JFormattedTextField txtHorarioAbertura;
    private final JFormattedTextField txtHorarioFechamento;
    private final BarbeariaService barbeariaService;

    public BarbeariaController(Component parent, JTextField txtNome, JTextField txtCep, JTextArea txtCultura,
                                JFormattedTextField txtHorarioAbertura, JFormattedTextField txtHorarioFechamento,
                                BarbeariaService barbeariaService) {
        this.parent = parent;
        this.txtNome = txtNome;
        this.txtCep = txtCep;
        this.txtCultura = txtCultura;
        this.txtHorarioAbertura = txtHorarioAbertura;
        this.txtHorarioFechamento = txtHorarioFechamento;
        this.barbeariaService = barbeariaService;
    }

    /**
     * Preenche os campos com os dados atuais da barbearia. Antes de exibir,
     * recarrega a barbearia diretamente do banco em vez de confiar apenas no
     * {@link AppContext}, para evitar mostrar dados desatualizados caso o
     * contexto em memória esteja obsoleto, e já atualiza o {@link AppContext}
     * com o resultado.
     */
    public void carregarDados() {
        try {
            Barbearia ctx = AppContext.getInstance().getBarbeariaAtual();
            if (ctx == null) return;

            // Recarrega do banco para evitar inconsistências caso o AppContext esteja desatualizado
            Barbearia b = barbeariaService.buscarPorId(ctx.getId());
            if (b == null) b = ctx;
            // Reconstrói a sessão inteira (Session é imutável) mantendo o mesmo usuário logado
            AppContext.getInstance().setSessaoAtual(new Session(AppContext.getInstance().getUsuarioLogado(), b));

            txtNome.setText(b.getNome() != null ? b.getNome() : "");
            txtCep.setText(b.getCep() != null ? b.getCep() : "");
            txtCultura.setText(b.getCulturaValores() != null ? b.getCulturaValores() : "");
            txtHorarioAbertura.setText(DateTimeUtil.formatTime(b.getHorarioAbertura()));
            txtHorarioFechamento.setText(DateTimeUtil.formatTime(b.getHorarioFechamento()));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao carregar dados da barbearia:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Salva as alterações da aba "Dados Gerais" na barbearia atual. Valida
     * que, quando ambos os horários são informados, a abertura seja antes do
     * fechamento; campos de horário em branco (ou só com placeholder) são
     * tratados como "sem restrição de horário" via {@link #parseHorarioOuNulo(String)}.
     */
    public void salvar() {
        try {
            LocalTime abertura = parseHorarioOuNulo(txtHorarioAbertura.getText());
            LocalTime fechamento = parseHorarioOuNulo(txtHorarioFechamento.getText());
            if (abertura != null && fechamento != null && !abertura.isBefore(fechamento)) {
                JOptionPane.showMessageDialog(parent, "O horário de abertura deve ser antes do fechamento.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            b.setNome(txtNome.getText());
            b.setCep(txtCep.getText());
            b.setCulturaValores(txtCultura.getText());
            b.setHorarioAbertura(abertura);
            b.setHorarioFechamento(fechamento);
            barbeariaService.atualizar(b);
            JOptionPane.showMessageDialog(parent, "Dados atualizados!");
        } catch (SQLException e) {
            logger.error("Erro ao salvar dados da barbearia", e);
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(parent, "Horário inválido. Use o formato HH:mm.", "Validação", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Texto do campo mascarado ainda com placeholders ("__:__") ou vazio conta como "sem restrição". */
    private LocalTime parseHorarioOuNulo(String texto) {
        if (texto == null || texto.replaceAll("[^0-9]", "").isEmpty()) return null;
        return DateTimeUtil.parseTime(texto.trim());
    }
}
