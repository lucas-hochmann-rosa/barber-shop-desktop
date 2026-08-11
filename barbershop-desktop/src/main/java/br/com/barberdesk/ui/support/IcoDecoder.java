package br.com.barberdesk.ui.support;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Leitor mínimo do formato .ico do Windows.
 *
 * O javax.imageio padrão do Java não tem suporte nativo a esse formato
 * (nenhum ImageReader registrado pra "ico") - em vez de puxar uma
 * dependência externa só pra isso, este decodificador entende o
 * suficiente do formato pra extrair os frames.
 *
 * Cada frame dentro de um .ico é ou (a) um PNG completo embutido (comum
 * em frames grandes, 256x256, gerados por ferramentas modernas), ou
 * (b) um bitmap DIB cru, de baixo pra cima, com 32 bits por pixel (BGRA)
 * seguido de uma máscara AND - só o caso (b) com 32bpp é suportado aqui,
 * que cobre o que as ferramentas de conversão de ícone mais comuns geram.
 */
public final class IcoDecoder {

    private IcoDecoder() {
    }

    /** Lê todos os frames de um .ico. Frames em formato não suportado são simplesmente ignorados. */
    public static List<BufferedImage> ler(InputStream in) throws IOException {
        byte[] dados = lerTodosOsBytes(in);
        List<BufferedImage> frames = new ArrayList<>();
        if (dados.length < 6 || lerUInt16LE(dados, 2) != 1) {
            return frames; // não é um .ico (tipo 1) válido
        }
        int quantidade = lerUInt16LE(dados, 4);
        for (int i = 0; i < quantidade; i++) {
            int entrada = 6 + i * 16;
            int tamanho = lerInt32LE(dados, entrada + 8);
            int offset = lerInt32LE(dados, entrada + 12);
            BufferedImage frame = decodificarFrame(dados, offset, tamanho);
            if (frame != null) {
                frames.add(frame);
            }
        }
        return frames;
    }

    /** Devolve o maior frame do .ico (a melhor fonte pra gerar outras resoluções por escala). */
    public static BufferedImage lerMaiorFrame(InputStream in) throws IOException {
        BufferedImage maior = null;
        for (BufferedImage frame : ler(in)) {
            if (maior == null || frame.getWidth() > maior.getWidth()) {
                maior = frame;
            }
        }
        return maior;
    }

    private static BufferedImage decodificarFrame(byte[] dados, int offset, int tamanho) throws IOException {
        if (ehPng(dados, offset)) {
            return ImageIO.read(new ByteArrayInputStream(dados, offset, tamanho));
        }
        return decodificarDib(dados, offset);
    }

    private static boolean ehPng(byte[] dados, int offset) {
        return dados[offset] == (byte) 0x89 && dados[offset + 1] == 0x50
                && dados[offset + 2] == 0x4E && dados[offset + 3] == 0x47;
    }

    /** Bitmap DIB cru (BITMAPINFOHEADER + pixels 32bpp BGRA de baixo pra cima + máscara AND). */
    private static BufferedImage decodificarDib(byte[] dados, int offset) {
        int tamanhoHeader = lerInt32LE(dados, offset);
        int largura = lerInt32LE(dados, offset + 4);
        int alturaTotal = lerInt32LE(dados, offset + 8); // inclui a máscara AND (2x a altura real do ícone)
        int altura = alturaTotal / 2;
        int bpp = lerUInt16LE(dados, offset + 14);
        if (bpp != 32 || largura <= 0 || altura <= 0) {
            return null; // só o caso comum (32bpp) é suportado
        }

        int inicioPixels = offset + tamanhoHeader;
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < altura; y++) {
            int linhaOrigem = inicioPixels + (altura - 1 - y) * largura * 4; // DIB é de baixo pra cima
            for (int x = 0; x < largura; x++) {
                int p = linhaOrigem + x * 4;
                int b = dados[p] & 0xFF;
                int g = dados[p + 1] & 0xFF;
                int r = dados[p + 2] & 0xFF;
                int a = dados[p + 3] & 0xFF;
                imagem.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return imagem;
    }

    private static int lerUInt16LE(byte[] dados, int offset) {
        return (dados[offset] & 0xFF) | ((dados[offset + 1] & 0xFF) << 8);
    }

    private static int lerInt32LE(byte[] dados, int offset) {
        return (dados[offset] & 0xFF) | ((dados[offset + 1] & 0xFF) << 8)
                | ((dados[offset + 2] & 0xFF) << 16) | ((dados[offset + 3] & 0xFF) << 24);
    }

    private static byte[] lerTodosOsBytes(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int lidos;
        while ((lidos = in.read(chunk)) != -1) {
            buffer.write(chunk, 0, lidos);
        }
        return buffer.toByteArray();
    }
}
