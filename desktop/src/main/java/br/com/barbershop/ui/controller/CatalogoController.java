package br.com.barbershop.ui.controller;

import br.com.barbershop.app.AppContext;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.model.Barbeiro;
import br.com.barbershop.model.Servico;
import br.com.barbershop.service.CatalogoService;
import br.com.barbershop.ui.DialogBarbeiro;
import br.com.barbershop.ui.DialogServico;
import br.com.barbershop.ui.support.UIUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Controla o catálogo da barbearia: o grid de cartões de serviço exibido na
 * Home e as abas "Serviços"/"Barbeiros" de gerenciamento (CRUD) em "Minha
 * Barbearia". Extraído de {@code TelaHome} (SRP - Fase 5).
 */
public class CatalogoController {

    private final Component parent;
    private final JPanel pnlServicosGrid;
    private final JTable tblGerenciarServicos;
    private final JTable tblGerenciarBarbeiros;
    private final CatalogoService catalogoService;
    private final Consumer<Servico> onAgendarServico;
    private final NumberFormat moedaFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private List<Servico> servicosGerenciarLista;
    private List<Barbeiro> barbeirosGerenciarLista;

    /**
     * @param onAgendarServico callback disparado ao clicar em "Agendar" num cartão do
     *                         grid de serviços - quem monta a tela decide como abrir o
     *                         fluxo de novo agendamento (acoplamento com a agenda)
     */
    public CatalogoController(Component parent, JPanel pnlServicosGrid, JTable tblGerenciarServicos,
                               JTable tblGerenciarBarbeiros, CatalogoService catalogoService,
                               Consumer<Servico> onAgendarServico) {
        this.parent = parent;
        this.pnlServicosGrid = pnlServicosGrid;
        this.tblGerenciarServicos = tblGerenciarServicos;
        this.tblGerenciarBarbeiros = tblGerenciarBarbeiros;
        this.catalogoService = catalogoService;
        this.onAgendarServico = onAgendarServico;
    }

    /** Configura o ordenador das tabelas de gerenciamento. */
    public void configurar() {
        tblGerenciarServicos.setRowSorter(new TableRowSorter<>((DefaultTableModel) tblGerenciarServicos.getModel()));
        tblGerenciarBarbeiros.setRowSorter(new TableRowSorter<>((DefaultTableModel) tblGerenciarBarbeiros.getModel()));
    }

    /**
     * Reconstrói o grid de cartões de serviço (um {@link JPanel} por
     * serviço, com miniatura, nome, preço e botão "Agendar") a partir dos
     * serviços cadastrados na barbearia atual. Remove todos os cartões
     * existentes antes de recriá-los.
     */
    public void carregarGridServicos() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;
            List<Servico> servicos = catalogoService.listarServicos(b.getId());
            pnlServicosGrid.removeAll();
            for (Servico s : servicos) {
                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setPreferredSize(new Dimension(130, 175));
                card.setBackground(UIUtil.COLOR_BRANCO);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIUtil.COLOR_NEBLINA, 1),
                        BorderFactory.createEmptyBorder(6, 6, 8, 6)));

                JLabel lblImg = new JLabel();
                lblImg.setAlignmentX(Component.CENTER_ALIGNMENT);
                UIUtil.exibirMiniatura(lblImg, s.getFotoCaminho(), 110, 80);

                JLabel lblNome = new JLabel(s.getNome());
                lblNome.setAlignmentX(Component.CENTER_ALIGNMENT);
                lblNome.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblNome.setForeground(UIUtil.COLOR_TINTA);

                JLabel lblPreco = new JLabel(moedaFormat.format(s.getPreco()));
                lblPreco.setAlignmentX(Component.CENTER_ALIGNMENT);
                lblPreco.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblPreco.setForeground(UIUtil.COLOR_VERDE_CADEIRA);

                JButton btn = new JButton("Agendar");
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                UIUtil.estilizarBotaoPrimario(btn);
                btn.addActionListener(e -> onAgendarServico.accept(s));

                card.add(Box.createVerticalStrut(4));
                card.add(lblImg);
                card.add(Box.createVerticalStrut(4));
                card.add(lblNome);
                card.add(Box.createVerticalStrut(2));
                card.add(lblPreco);
                card.add(Box.createVerticalStrut(6));
                card.add(btn);

                pnlServicosGrid.add(card);
            }
            pnlServicosGrid.revalidate();
            pnlServicosGrid.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao carregar grid de serviços:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Recarrega {@code tblGerenciarServicos} com os serviços da barbearia
     * atual e guarda a lista em {@code servicosGerenciarLista}, que espelha
     * a ordem das linhas do model e é usada pelos métodos de editar/excluir
     * para resolver a linha selecionada na tabela de volta para o
     * {@link Servico} correspondente.
     */
    public void carregarTabelaServicos() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;

            servicosGerenciarLista = catalogoService.listarServicos(b.getId());
            DefaultTableModel model = (DefaultTableModel) tblGerenciarServicos.getModel();
            model.setRowCount(0);
            for (Servico s : servicosGerenciarLista) {
                model.addRow(new Object[]{
                    s.getNome(),
                    s.getPreco()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao carregar serviços:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Recarrega {@code tblGerenciarBarbeiros} com os barbeiros da barbearia
     * atual e guarda a lista em {@code barbeirosGerenciarLista}, que
     * espelha a ordem das linhas do model e é usada pelos métodos de
     * editar/excluir para resolver a linha selecionada na tabela de volta
     * para o {@link Barbeiro} correspondente.
     */
    public void carregarTabelaBarbeiros() {
        try {
            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) return;

            barbeirosGerenciarLista = catalogoService.listarBarbeiros(b.getId());
            DefaultTableModel model = (DefaultTableModel) tblGerenciarBarbeiros.getModel();
            model.setRowCount(0);
            for (Barbeiro barb : barbeirosGerenciarLista) {
                model.addRow(new Object[]{
                    barb.getNome()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao carregar barbeiros:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre o diálogo de cadastro de serviço; se o usuário salvar, valida que
     * não existe outro serviço com o mesmo nome na barbearia, insere o novo
     * serviço no banco e recarrega a tabela de gerenciamento e o grid de
     * serviços da Home.
     */
    public void novoServico() {
        try {
            DialogServico dlg = new DialogServico((java.awt.Frame) parent, true);
            dlg.setVisible(true);

            if (!dlg.isSalvo()) return;
            Servico s = dlg.getServico();

            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) throw new IllegalStateException("Sessão inválida: barbearia não definida.");

            if (catalogoService.existeServicoComNome(b.getId(), s.getNome(), 0)) {
                JOptionPane.showMessageDialog(parent, "Já existe um serviço com esse nome.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            s.setBarbeariaId(b.getId());
            catalogoService.salvarNovoServico(s);

            carregarTabelaServicos();
            carregarGridServicos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao salvar serviço:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Edita o serviço selecionado em {@code tblGerenciarServicos},
     * resolvendo a linha selecionada para o {@link Servico} correspondente
     * via {@code servicosGerenciarLista}. Abre o diálogo com uma cópia do
     * serviço original (para não refletir alterações na tabela caso o
     * usuário cancele), valida nome duplicado e, se salvo, persiste e
     * recarrega a tabela de gerenciamento e o grid de serviços da Home.
     */
    public void editarServico() {
        int row = tblGerenciarServicos.getSelectedRow();
        if (row < 0 || servicosGerenciarLista == null || row >= servicosGerenciarLista.size()) {
            JOptionPane.showMessageDialog(parent, "Selecione um serviço para editar.");
            return;
        }

        try {
            Servico original = servicosGerenciarLista.get(row);
            // Trabalhar com uma cópia para evitar alterar a tabela caso cancele
            Servico copia = new Servico(original.getId(), original.getBarbeariaId(), original.getNome(), original.getPreco(), original.getImagemBase64(), original.getDuracaoMinutos());

            DialogServico dlg = new DialogServico((java.awt.Frame) parent, true, copia);
            dlg.setVisible(true);
            if (!dlg.isSalvo()) return;

            Servico editado = dlg.getServico();
            if (catalogoService.existeServicoComNome(editado.getBarbeariaId(), editado.getNome(), editado.getId())) {
                JOptionPane.showMessageDialog(parent, "Já existe um serviço com esse nome.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            catalogoService.atualizarServico(editado);
            carregarTabelaServicos();
            carregarGridServicos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao editar serviço:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exclui o serviço selecionado em {@code tblGerenciarServicos}, após
     * confirmação do usuário (avisando que agendamentos existentes podem
     * ser afetados), e recarrega a tabela de gerenciamento e o grid de
     * serviços da Home.
     */
    public void excluirServico() {
        int row = tblGerenciarServicos.getSelectedRow();
        if (row < 0 || servicosGerenciarLista == null || row >= servicosGerenciarLista.size()) {
            JOptionPane.showMessageDialog(parent, "Selecione um serviço para excluir.");
            return;
        }

        Servico s = servicosGerenciarLista.get(row);
        int ok = JOptionPane.showConfirmDialog(parent,
                "Excluir o serviço '" + s.getNome() + "'?\nIsso pode afetar agendamentos existentes.",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            catalogoService.excluirServico(s.getId());
            carregarTabelaServicos();
            carregarGridServicos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao excluir serviço:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre o diálogo de cadastro de barbeiro; se o usuário salvar, valida
     * que não existe outro barbeiro com o mesmo nome na barbearia, insere o
     * novo barbeiro no banco e recarrega a tabela de gerenciamento de
     * barbeiros.
     */
    public void novoBarbeiro() {
        try {
            DialogBarbeiro dlg = new DialogBarbeiro((java.awt.Frame) parent, true);
            dlg.setVisible(true);
            if (!dlg.isSalvo()) return;

            Barbeiro barb = dlg.getBarbeiro();

            Barbearia b = AppContext.getInstance().getBarbeariaAtual();
            if (b == null) throw new IllegalStateException("Sessão inválida: barbearia não definida.");

            if (catalogoService.existeBarbeiroComNome(b.getId(), barb.getNome(), 0)) {
                JOptionPane.showMessageDialog(parent, "Já existe um barbeiro com esse nome.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            barb.setBarbeariaId(b.getId());
            catalogoService.salvarNovoBarbeiro(barb);

            carregarTabelaBarbeiros();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao salvar barbeiro:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Edita o barbeiro selecionado em {@code tblGerenciarBarbeiros},
     * resolvendo a linha selecionada para o {@link Barbeiro} correspondente
     * via {@code barbeirosGerenciarLista}. Abre o diálogo com uma cópia do
     * barbeiro original (para não refletir alterações na tabela caso o
     * usuário cancele), valida nome duplicado e, se salvo, persiste e
     * recarrega a tabela de gerenciamento de barbeiros.
     */
    public void editarBarbeiro() {
        int row = tblGerenciarBarbeiros.getSelectedRow();
        if (row < 0 || barbeirosGerenciarLista == null || row >= barbeirosGerenciarLista.size()) {
            JOptionPane.showMessageDialog(parent, "Selecione um barbeiro para editar.");
            return;
        }

        try {
            Barbeiro original = barbeirosGerenciarLista.get(row);
            Barbeiro copia = new Barbeiro(original.getId(), original.getBarbeariaId(), original.getNome(), original.getImagemBase64());

            DialogBarbeiro dlg = new DialogBarbeiro((java.awt.Frame) parent, true, copia);
            dlg.setVisible(true);
            if (!dlg.isSalvo()) return;

            Barbeiro editado = dlg.getBarbeiro();
            if (catalogoService.existeBarbeiroComNome(editado.getBarbeariaId(), editado.getNome(), editado.getId())) {
                JOptionPane.showMessageDialog(parent, "Já existe um barbeiro com esse nome.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            catalogoService.atualizarBarbeiro(editado);
            carregarTabelaBarbeiros();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao editar barbeiro:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exclui o barbeiro selecionado em {@code tblGerenciarBarbeiros}, após
     * confirmação do usuário (avisando que agendamentos existentes podem
     * ser afetados), e recarrega a tabela de gerenciamento de barbeiros.
     */
    public void excluirBarbeiro() {
        int row = tblGerenciarBarbeiros.getSelectedRow();
        if (row < 0 || barbeirosGerenciarLista == null || row >= barbeirosGerenciarLista.size()) {
            JOptionPane.showMessageDialog(parent, "Selecione um barbeiro para excluir.");
            return;
        }

        Barbeiro barb = barbeirosGerenciarLista.get(row);
        int ok = JOptionPane.showConfirmDialog(parent,
                "Excluir o barbeiro '" + barb.getNome() + "'?\nIsso pode afetar agendamentos existentes.",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            catalogoService.excluirBarbeiro(barb.getId());
            carregarTabelaBarbeiros();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Erro ao excluir barbeiro:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
