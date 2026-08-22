package br.com.barbershop.ui.support;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Taskbar;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

/**
 * Utilitários de interface gráfica para o aplicativo desktop Java Swing.
 * Centraliza ícones, paleta de cores institucional unificada com a versão web,
 * renderização de miniaturas e formatação de campos.
 */
public class UIUtil {

    // Paleta de design institucional (paridade com a versão Web)
    public static final Color COLOR_PORCELANA = new Color(0xF2, 0xF5, 0xF3); // #F2F5F3 - Fundo geral
    public static final Color COLOR_BRANCO = new Color(0xFF, 0xFF, 0xFF);    // #FFFFFF - Cartões e tabelas
    public static final Color COLOR_VERDE_CADEIRA = new Color(0x14, 0x48, 0x3F); // #14483F - Marca / Cabeçalho
    public static final Color COLOR_VERDE_CLARO = new Color(0x2E, 0x7D, 0x6B); // #2E7D6B - Destaques e ativos
    public static final Color COLOR_OXBLOOD = new Color(0x8A, 0x33, 0x24);     // #8A3324 - Ações destrutivas
    public static final Color COLOR_LATAO = new Color(0xC8, 0x91, 0x2F);       // #C8912F - Destaque dourado
    public static final Color COLOR_TINTA = new Color(0x14, 0x20, 0x1E);       // #14201E - Texto principal
    public static final Color COLOR_FUMACA = new Color(0x6B, 0x7A, 0x76);      // #6B7A76 - Texto secundário
    public static final Color COLOR_NEBLINA = new Color(0xDC, 0xE3, 0xE0);     // #DCE3E0 - Bordas e divisores

    private static final int[] TAMANHOS_ICONE = {16, 24, 32, 48, 64, 128, 256};

    private static List<Image> iconesApp;
    private static boolean iconeCarregado = false;

    /**
     * Configura o Look & Feel FlatLaf com a paleta de cores e tipografia
     * unificada entre a versão desktop e web.
     */
    public static void configurarTema() {
        FlatLightLaf.setup();

        UIManager.put("Panel.background", COLOR_PORCELANA);
        UIManager.put("RootPane.background", COLOR_PORCELANA);
        UIManager.put("Viewport.background", COLOR_BRANCO);
        UIManager.put("ScrollPane.background", COLOR_BRANCO);

        UIManager.put("Component.accentColor", COLOR_VERDE_CADEIRA);
        UIManager.put("Component.focusColor", new Color(0x2E, 0x7D, 0x6B, 0x60));
        UIManager.put("Component.arc", 6);
        UIManager.put("Button.arc", 6);
        UIManager.put("TextComponent.arc", 6);
        UIManager.put("ProgressBar.arc", 6);

        UIManager.put("TableHeader.background", COLOR_VERDE_CADEIRA);
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("TableHeader.separatorColor", COLOR_VERDE_CLARO);

        UIManager.put("Table.background", COLOR_BRANCO);
        UIManager.put("Table.foreground", COLOR_TINTA);
        UIManager.put("Table.gridColor", COLOR_NEBLINA);
        UIManager.put("Table.selectionBackground", new Color(0x2E, 0x7D, 0x6B, 0x33));
        UIManager.put("Table.selectionForeground", COLOR_TINTA);
        UIManager.put("Table.rowHeight", 28);

        UIManager.put("TabbedPane.selectedBackground", COLOR_VERDE_CADEIRA);
        UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
        UIManager.put("TabbedPane.underlineColor", COLOR_LATAO);
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("TabbedPane.tabSeparatorsFullHeight", true);
    }

    /**
     * Aplica o estilo visual primário da marca a um botão (verde-cadeira, texto branco).
     */
    public static void estilizarBotaoPrimario(JButton btn) {
        if (btn == null) return;
        btn.setBackground(COLOR_VERDE_CADEIRA);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }

    /**
     * Aplica o estilo visual secundário (outline) a um botão.
     */
    public static void estilizarBotaoSecundario(JButton btn) {
        if (btn == null) return;
        btn.setBackground(COLOR_BRANCO);
        btn.setForeground(COLOR_VERDE_CADEIRA);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_NEBLINA, 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
    }

    /**
     * Aplica o estilo de perigo / cancelamento a um botão (oxblood).
     */
    public static void estilizarBotaoPerigo(JButton btn) {
        if (btn == null) return;
        btn.setBackground(COLOR_OXBLOOD);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }

    /**
     * Ícone do app (src/main/resources/icon.ico), aplicado em toda janela.
     */
    public static void aplicarIcone(Window janela) {
        if (!iconeCarregado) {
            try (InputStream in = UIUtil.class.getClassLoader().getResourceAsStream("icon.ico")) {
                BufferedImage original = in != null ? IcoDecoder.lerMaiorFrame(in) : null;
                iconesApp = original != null ? gerarTamanhosDeIcone(original) : null;
            } catch (IOException ignored) {
                iconesApp = null;
            }
            iconeCarregado = true;
            aplicarIconeNaTaskbar(iconesApp == null || iconesApp.isEmpty() ? null : iconesApp.get(iconesApp.size() - 1));
        }
        if (iconesApp != null && !iconesApp.isEmpty()) {
            janela.setIconImages(iconesApp);
        }
    }

    private static List<Image> gerarTamanhosDeIcone(BufferedImage original) {
        List<Image> resultado = new ArrayList<>();
        for (int tamanho : TAMANHOS_ICONE) {
            BufferedImage escalado = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = escalado.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(original, 0, 0, tamanho, tamanho, null);
            g2.dispose();
            resultado.add(escalado);
        }
        return resultado;
    }

    private static void aplicarIconeNaTaskbar(Image icone) {
        if (icone == null || !Taskbar.isTaskbarSupported()) {
            return;
        }
        Taskbar taskbar = Taskbar.getTaskbar();
        if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
            taskbar.setIconImage(icone);
        }
    }

    /**
     * Campo de texto com máscara fixa (ex.: "##/##/####" para data).
     */
    public static JFormattedTextField criarCampoMascarado(String mascara) {
        try {
            MaskFormatter formatter = new MaskFormatter(mascara);
            formatter.setPlaceholderCharacter('_');
            return new JFormattedTextField(formatter);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Máscara inválida: " + mascara, e);
        }
    }

    public static boolean campoMascaradoVazio(JFormattedTextField campo) {
        String texto = campo.getText();
        return texto == null || texto.replaceAll("[^0-9]", "").isEmpty();
    }

    public static boolean pareceNumeroDeTelefone(String contato) {
        return contato != null && contato.replaceAll("\\D", "").length() >= 8;
    }

    public static void abrirWhatsApp(Component parent, String contato) {
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

    /**
     * Mostra a imagem de um barbeiro/serviço a partir do Base64 gravado no banco.
     */
    public static void exibirMiniatura(JLabel label, String base64, int width, int height) {
        int w = width;
        int h = height;

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

        label.setPreferredSize(new Dimension(w, h));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        if (base64 == null || base64.trim().isEmpty()) {
            label.setIcon(null);
            label.setText("Sem imagem");
            label.setForeground(COLOR_FUMACA);
            return;
        }

        try {
            byte[] dados = Base64.getDecoder().decode(base64);
            ImageIcon icon = new ImageIcon(dados);
            Image img = icon.getImage();

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
            label.setForeground(COLOR_FUMACA);
        }
    }

    public static void exibirMiniatura(JLabel label, String base64) {
        exibirMiniatura(label, base64, 0, 0);
    }
}
