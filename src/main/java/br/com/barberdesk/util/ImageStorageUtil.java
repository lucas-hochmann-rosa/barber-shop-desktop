package br.com.barberdesk.util;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Converte a imagem escolhida pelo usuário (foto de barbeiro/serviço) para
 * Base64, gravado direto no banco — evita depender de um caminho de arquivo
 * que pode não existir mais depois (pasta movida, imagem apagada, banco
 * restaurado em outra máquina). Redimensiona antes de codificar para não
 * inflar o banco com fotos de câmera em resolução alta.
 */
public class ImageStorageUtil {

    private static final int DIMENSAO_MAXIMA = 480;

    /**
     * Lê a imagem do arquivo informado, redimensiona (se necessário) para
     * caber em {@value #DIMENSAO_MAXIMA}px de largura/altura e retorna o
     * conteúdo codificado em Base64, pronto para ser gravado num campo do
     * banco de dados.
     *
     * @param origem arquivo de imagem escolhido pelo usuário
     * @return a imagem redimensionada, codificada em PNG/Base64
     * @throws IOException se o arquivo não puder ser lido ou não for uma imagem reconhecida
     */
    public static String paraBase64(File origem) throws IOException {
        BufferedImage original = ImageIO.read(origem);
        if (original == null) {
            throw new IOException("Arquivo não é uma imagem reconhecida: " + origem.getName());
        }

        BufferedImage redimensionada = redimensionar(original, DIMENSAO_MAXIMA);

        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        ImageIO.write(redimensionada, "png", saida);
        return Base64.getEncoder().encodeToString(saida.toByteArray());
    }

    /**
     * Redimensiona a imagem mantendo a proporção original, de forma que nem
     * a largura nem a altura ultrapassem {@code dimensaoMaxima}. Se a imagem
     * já for menor ou igual ao limite nas duas dimensões, retorna a imagem
     * original sem nenhuma cópia/processamento.
     *
     * @param original      imagem de origem
     * @param dimensaoMaxima limite, em pixels, para largura e altura
     * @return a imagem redimensionada, ou a original se já estiver dentro do limite
     */
    private static BufferedImage redimensionar(BufferedImage original, int dimensaoMaxima) {
        int largura = original.getWidth();
        int altura = original.getHeight();
        if (largura <= dimensaoMaxima && altura <= dimensaoMaxima) {
            return original;
        }

        double escala = Math.min((double) dimensaoMaxima / largura, (double) dimensaoMaxima / altura);
        int novaLargura = Math.max(1, (int) (largura * escala));
        int novaAltura = Math.max(1, (int) (altura * escala));

        Image escalada = original.getScaledInstance(novaLargura, novaAltura, Image.SCALE_SMOOTH);
        BufferedImage resultado = new BufferedImage(novaLargura, novaAltura, BufferedImage.TYPE_INT_ARGB);
        resultado.getGraphics().drawImage(escalada, 0, 0, null);
        return resultado;
    }
}
