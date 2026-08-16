package br.com.barbershop.ui.support;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * FlowLayout com cálculo de preferred size "wrap", evitando scroll horizontal
 * em painéis dentro de JScrollPane.
 */
public class WrapLayout extends FlowLayout {

    /** Cria o layout com alinhamento e espaçamentos padrão do {@link FlowLayout}. */
    public WrapLayout() {
        super();
    }

    /** Cria o layout com o alinhamento informado (ver constantes de {@link FlowLayout}). */
    public WrapLayout(int align) {
        super(align);
    }

    /** Cria o layout com alinhamento e espaçamentos horizontal/vertical customizados. */
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /**
     * Calcula o tamanho preferido do container somando a altura de todas as
     * linhas necessárias para acomodar os componentes na largura disponível
     * (em vez de assumir uma única linha infinita, como o {@link FlowLayout}
     * padrão faz), o que evita o scroll horizontal quando o painel está
     * dentro de um {@link javax.swing.JScrollPane}.
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    /**
     * Calcula o tamanho mínimo do container usando a mesma lógica de quebra
     * de linha do tamanho preferido, com um pequeno ajuste de largura
     * (subtrai {@code hgap + 1}) para manter compatibilidade com o
     * comportamento esperado de {@link FlowLayout#minimumLayoutSize}.
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    /**
     * Simula a distribuição dos componentes em linhas dentro da largura
     * disponível do container e retorna a dimensão total resultante (soma
     * das alturas de cada linha, largura igual à linha mais larga). Usado
     * tanto para o cálculo do tamanho preferido quanto do mínimo, alternando
     * entre {@code getPreferredSize()} e {@code getMinimumSize()} de cada
     * componente conforme o parâmetro {@code preferred}.
     */
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getWidth();
            if (targetWidth <= 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (getHgap() * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();
            for (int i = 0; i < nmembers; i++) {
                java.awt.Component m = target.getComponent(i);
                if (!m.isVisible()) continue;

                Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                if (rowWidth + d.width > maxWidth) {
                    addRow(dim, rowWidth, rowHeight);
                    rowWidth = 0;
                    rowHeight = 0;
                }

                if (rowWidth != 0) {
                    rowWidth += getHgap();
                }
                rowWidth += d.width;
                rowHeight = Math.max(rowHeight, d.height);
            }

            addRow(dim, rowWidth, rowHeight);

            dim.width += horizontalInsetsAndGap;
            dim.height += insets.top + insets.bottom + getVgap() * 2;

            return dim;
        }
    }

    /**
     * Acumula na dimensão total ({@code dim}) a largura e a altura de mais
     * uma linha já fechada: a largura final é a maior largura entre as
     * linhas, e a altura é a soma das alturas de todas as linhas (mais o
     * espaçamento vertical entre elas).
     */
    private void addRow(Dimension dim, int rowWidth, int rowHeight) {
        dim.width = Math.max(dim.width, rowWidth);
        if (dim.height > 0) {
            dim.height += getVgap();
        }
        dim.height += rowHeight;
    }
}
