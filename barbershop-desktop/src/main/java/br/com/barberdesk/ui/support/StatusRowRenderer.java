package br.com.barberdesk.ui.support;

import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.StatusAgendamento;
import java.awt.Color;
import java.awt.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Colore a linha pelo status do agendamento e, quando ainda está AGENDADO,
 * destaca os que começam em até 30 minutos - RF11 (classificação visual por
 * status/proximidade). Compartilhado entre as tabelas de agendamentos
 * pendentes e de histórico, cada uma com sua própria origem de dados.
 */
public class StatusRowRenderer extends DefaultTableCellRenderer {
    private static final Color COR_CANCELADO = new Color(248, 215, 218);
    private static final Color COR_CONCLUIDO = new Color(212, 237, 218);
    private static final Color COR_EM_ATENDIMENTO = new Color(204, 229, 255);
    private static final Color COR_INICIO_IMINENTE = new Color(255, 243, 205);

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
