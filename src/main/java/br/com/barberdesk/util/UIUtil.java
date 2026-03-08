package br.com.barberdesk.util;

import java.awt.Image;
import java.io.File;
import javax.swing.*;

public class UIUtil {
    public static void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static int showConfirm(String title, String message) {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean validateRequired(String value, String fieldName) {
        if (isNullOrEmpty(value)) {
            showError("Validação", fieldName + " é obrigatório!");
            return false;
        }
        return true;
    }

    public static void exibirMiniatura(JLabel label, String path, int width, int height) {
        int w = width;
        int h = height;

        // Se não vier tamanho, tenta usar o tamanho do próprio label
        if (w <= 0 || h <= 0) {
            if (label.getWidth() > 0 && label.getHeight() > 0) {
                w = label.getWidth();
                h = label.getHeight();
            } else if (label.getPreferredSize() != null && label.getPreferredSize().width > 0 && label.getPreferredSize().height > 0) {
                w = label.getPreferredSize().width;
                h = label.getPreferredSize().height;
            } else {
                w = 140;
                h = 140;
            }
        }

        // Garante que o label tenha espaço para o ícone (evita "corte")
        label.setPreferredSize(new java.awt.Dimension(w, h));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        if (path == null || path.trim().isEmpty()) {
            label.setIcon(null);
            label.setText("Sem imagem");
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            label.setIcon(null);
            label.setText("Sem imagem");
            return;
        }

        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage();

            // Redimensiona "fit" preservando proporção, sem cortar
            int imgW = icon.getIconWidth();
            int imgH = icon.getIconHeight();
            double scale = Math.min((double) w / Math.max(1, imgW), (double) h / Math.max(1, imgH));
            int newW = Math.max(1, (int) (imgW * scale));
            int newH = Math.max(1, (int) (imgH * scale));

            Image newImg = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(newImg));
            label.setText("");
        } catch (Exception e) {
            label.setIcon(null);
            label.setText("Sem imagem");
        }
    }

    /**
     * Overload de conveniência para manter compatibilidade com telas que
     * chamam exibirMiniatura(label, path) sem largura/altura.
     */
    public static void exibirMiniatura(JLabel label, String path) {
        // Usa o tamanho do próprio label (ou preferred size) para evitar corte
        exibirMiniatura(label, path, 0, 0);
    }
}
