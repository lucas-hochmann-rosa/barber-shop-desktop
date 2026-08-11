package br.com.barberdesk.ui.support;

import java.awt.Desktop;
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

public class UIUtil {

    private static final int[] TAMANHOS_ICONE = {16, 24, 32, 48, 64, 128, 256};

    private static List<Image> iconesApp;
    private static boolean iconeCarregado = false;

    /**
     * Ícone do app (src/main/resources/icon.ico), aplicado em toda janela.
     * Carregado uma única vez e reaproveitado. Gera várias resoluções
     * pré-renderizadas (setIconImages) em vez de entregar uma imagem grande
     * só - o Windows escala uma imagem única com baixa qualidade ao desenhar
     * o ícone pequeno da barra de título, o que deixava tudo pixelado.
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

    /**
     * setIconImages no JFrame já cobre a barra de título/Alt-Tab; a barra de
     * tarefas do Windows usa java.awt.Taskbar à parte. Chamada direta, sem
     * reflection - o projeto compila com --release 17, e java.awt.Taskbar
     * existe desde o Java 9.
     */
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
     * Um campo mascarado mostra os caracteres de preenchimento (ex.:
     * "__/__/____") mesmo sem o usuário ter digitado nada - getText() não
     * fica "" nesse caso, só cheio de placeholder. Considera vazio quando não
     * sobra nenhum dígito de verdade.
     */
    public static boolean campoMascaradoVazio(JFormattedTextField campo) {
        String texto = campo.getText();
        return texto == null || texto.replaceAll("[^0-9]", "").isEmpty();
    }

    /**
     * "Contato" do agendamento nem sempre é telefone (pode ser @ do Instagram,
     * por exemplo - ver OrigemContato). Considera número válido só quando sobram
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

    /**
     * Mostra a imagem de um barbeiro/serviço a partir do Base64 gravado no
     * banco (ver ImageStorageUtil) - não depende de um arquivo existir no
     * disco, então sobrevive a troca de máquina/pasta.
     */
    public static void exibirMiniatura(JLabel label, String base64, int width, int height) {
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

        if (base64 == null || base64.trim().isEmpty()) {
            label.setIcon(null);
            label.setText("Sem imagem");
            return;
        }

        try {
            byte[] dados = Base64.getDecoder().decode(base64);
            ImageIcon icon = new ImageIcon(dados);
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
     * chamam exibirMiniatura(label, base64) sem largura/altura.
     */
    public static void exibirMiniatura(JLabel label, String base64) {
        // Usa o tamanho do próprio label (ou preferred size) para evitar corte
        exibirMiniatura(label, base64, 0, 0);
    }
}
