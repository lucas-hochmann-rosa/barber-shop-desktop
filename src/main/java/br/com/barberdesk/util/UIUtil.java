package br.com.barberdesk.util;

import java.awt.Desktop;
import java.awt.Image;
import java.io.File;
import java.net.URI;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

public class UIUtil {

    /**
     * Campo de texto com máscara fixa (ex.: "##/##/####" para data). Restringe o
     * que o usuário consegue digitar, evitando erro de formato só detectado ao
     * salvar. getText()/setText() continuam funcionando normalmente.
     */
    public static JFormattedTextField criarCampoMascarado(String mascara) {
        try {
            MaskFormatter formatter = new MaskFormatter(mascara);
            formatter.setPlaceholderCharacter('_');
            return new JFormattedTextField(formatter);
        } catch (ParseException e) {
            // Máscara inválida é erro de programação (string estática), não de usuário.
            throw new IllegalArgumentException("Máscara inválida: " + mascara, e);
        }
    }

    /**
     * "Contato" do agendamento nem sempre é telefone (pode ser @ do Instagram,
     * por exemplo — ver OrigemContato). Considera número válido só quando sobram
     * pelo menos 8 dígitos depois de remover tudo que não é dígito.
     */
    public static boolean pareceNumeroDeTelefone(String contato) {
        return contato != null && contato.replaceAll("\\D", "").length() >= 8;
    }

    /**
     * Abre a conversa do WhatsApp Web/Desktop para o contato informado. Assume
     * DDD+número brasileiro quando não vier com código do país (prefixa 55).
     */
    public static void abrirWhatsApp(java.awt.Component parent, String contato) {
        String digitos = contato != null ? contato.replaceAll("\\D", "") : "";
        if (digitos.length() < 8) {
            showWarning("WhatsApp", "Contato não parece ser um número de telefone válido.");
            return;
        }
        if (!digitos.startsWith("55")) {
            digitos = "55" + digitos;
        }

        try {
            Desktop.getDesktop().browse(new URI("https://wa.me/" + digitos));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Não foi possível abrir o WhatsApp: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

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
