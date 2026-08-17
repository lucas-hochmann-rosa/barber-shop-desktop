package br.com.barbershop.ui.controller;

import br.com.barbershop.app.AppContext;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.model.Cliente;
import br.com.barbershop.service.ClienteService;
import java.awt.Component;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Controla a aba "Clientes": listagem e filtro textual. Extraído de
 * {@code TelaHome} (SRP - Fase 5).
 */
public class ClienteController {

    private final Component parent;
    private final JTable tblClientes;
    private final JTextField txtBusca;
    private final ClienteService clienteService;

    public ClienteController(Component parent, JTable tblClientes, JTextField txtBusca, ClienteService clienteService) {
        this.parent = parent;
        this.tblClientes = tblClientes;
        this.txtBusca = txtBusca;
        this.clienteService = clienteService;
    }

    /** Configura o ordenador da tabela. */
    public void configurar() {
        tblClientes.setRowSorter(new TableRowSorter<>((DefaultTableModel) tblClientes.getModel()));
    }

    /** Recarrega {@code tblClientes} com os clientes da barbearia atual. */
    public void carregarClientes() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;

            DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
            model.setRowCount(0);
            for (Cliente c : clienteService.listarPorBarbearia(b.getId())) {
                model.addRow(new Object[]{ c.getNome(), c.getContato() });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao carregar clientes:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aplica em {@code tblClientes} o filtro de texto digitado em
     * {@code txtBusca}, usando um {@link RowFilter} de regex
     * case-insensitive sobre todas as colunas visíveis. Texto vazio remove
     * o filtro.
     */
    public void aplicarFiltro() {
        TableRowSorter<?> sorter = (TableRowSorter<?>) tblClientes.getRowSorter();
        String texto = txtBusca.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
        }
    }
}
