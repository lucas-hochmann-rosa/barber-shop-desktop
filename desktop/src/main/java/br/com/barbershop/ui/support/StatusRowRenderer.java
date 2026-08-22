package br.com.barbershop.ui.support;

import br.com.barbershop.model.Agendamento;
import br.com.barbershop.model.ClassificacaoAgenda;
import br.com.barbershop.service.ClassificadorAgenda;
import java.awt.Color;
import java.awt.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Colore a linha pela classificação visual do agendamento (RF11 -
 * classificação por status/proximidade), calculada por
 * {@link ClassificadorAgenda}. Esta classe só mapeia classificação para
 * cor - o cálculo em si (status, atraso, proximidade do horário) vive
 * inteiramente no core, testável sem Swing. Compartilhado entre as tabelas
 * de agendamentos pendentes e de histórico, cada uma com sua própria
 * origem de dados.
 */
public class StatusRowRenderer extends DefaultTableCellRenderer {
    private static final Color COR_EM_ANDAMENTO = new Color(225, 245, 240); // Verde suave (ativo)
    private static final Color COR_ATRASADO = new Color(254, 235, 233);      // Oxblood suave (alerta)
    private static final Color COR_IMINENTE = new Color(254, 243, 226);      // Latão suave (atenção)
    private static final Color COR_PROXIMO = new Color(255, 251, 235);       // Ouro suave (próximo)
    private static final Color COR_DISTANTE = new Color(255, 255, 255);      // Branco limpo (distante)
    private static final Color COR_CONCLUIDO = new Color(240, 243, 242);     // Porcelana (concluído)
    private static final Color COR_CANCELADO = new Color(245, 245, 245);     // Cinza neutro (cancelado)

    private final Supplier<List<Agendamento>> origemSupplier;

    public StatusRowRenderer(Supplier<List<Agendamento>> origemSupplier) {
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
        ClassificacaoAgenda classificacao = ClassificadorAgenda.classificar(a, LocalDateTime.now());
        return corDaClassificacao(classificacao);
    }

    private Color corDaClassificacao(ClassificacaoAgenda classificacao) {
        return switch (classificacao) {
            case EM_ANDAMENTO -> COR_EM_ANDAMENTO;
            case ATRASADO -> COR_ATRASADO;
            case IMINENTE -> COR_IMINENTE;
            case PROXIMO -> COR_PROXIMO;
            case DISTANTE -> COR_DISTANTE;
            case CONCLUIDO -> COR_CONCLUIDO;
            case CANCELADO -> COR_CANCELADO;
        };
    }
}
