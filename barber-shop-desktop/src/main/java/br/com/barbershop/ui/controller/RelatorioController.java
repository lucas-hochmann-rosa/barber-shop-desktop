package br.com.barbershop.ui.controller;

import br.com.barbershop.app.AppContext;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.model.ItemRelatorio;
import br.com.barbershop.service.RelatorioService;
import br.com.barbershop.ui.support.UIUtil;
import br.com.barbershop.util.DateTimeUtil;
import java.awt.Component;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Controla a aba "Relatórios": faturamento total, serviços mais vendidos e
 * ranking de barbeiros num intervalo de datas. Extraído de {@code TelaHome}
 * (SRP - Fase 5).
 */
public class RelatorioController {

    private final Component parent;
    private final JFormattedTextField txtDe;
    private final JFormattedTextField txtAte;
    private final JLabel lblFaturamentoTotal;
    private final JTable tblServicosVendidos;
    private final JTable tblRankingBarbeiros;
    private final RelatorioService relatorioService;
    private final NumberFormat moedaFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public RelatorioController(Component parent, JFormattedTextField txtDe, JFormattedTextField txtAte,
                                JLabel lblFaturamentoTotal, JTable tblServicosVendidos, JTable tblRankingBarbeiros,
                                RelatorioService relatorioService) {
        this.parent = parent;
        this.txtDe = txtDe;
        this.txtAte = txtAte;
        this.lblFaturamentoTotal = lblFaturamentoTotal;
        this.tblServicosVendidos = tblServicosVendidos;
        this.tblRankingBarbeiros = tblRankingBarbeiros;
        this.relatorioService = relatorioService;
    }

    /**
     * Se os campos de data ainda não foram preenchidos (campo mascarado
     * vazio), assume como período padrão o mês corrente até hoje.
     */
    public void prepararPeriodoPadraoSeVazio() {
        if (UIUtil.campoMascaradoVazio(txtDe)) {
            LocalDate hoje = LocalDate.now();
            txtDe.setText(DateTimeUtil.formatDate(hoje.withDayOfMonth(1)));
            txtAte.setText(DateTimeUtil.formatDate(hoje));
        }
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
     * existe esse histórico - só de nome/duração). Se o preço de um serviço
     * for alterado, relatórios de períodos passados passam a refletir o
     * preço novo retroativamente; é uma limitação conhecida, já documentada
     * no javadoc de {@link RelatorioService}.
     */
    public void gerar() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;

            LocalDate inicio = DateTimeUtil.parseDate(txtDe.getText().trim());
            LocalDate fim = DateTimeUtil.parseDate(txtAte.getText().trim());
            if (inicio.isAfter(fim)) {
                JOptionPane.showMessageDialog(parent, "A data \"De\" deve ser antes da data \"Até\".", "Validação", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(parent, "Data inválida. Use o formato dd/MM/yyyy.", "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent, "Erro ao gerar relatório:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
