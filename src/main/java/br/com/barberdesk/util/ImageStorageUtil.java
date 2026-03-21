package br.com.barberdesk.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Copia imagens escolhidas pelo usuário para uma pasta gerenciada pelo próprio
 * app, fora da pasta de instalação — evita que o caminho salvo no banco
 * dependa de onde o arquivo original estava (ex.: Downloads), que pode ser
 * movido ou apagado depois. Não migra imagens já cadastradas antes desta
 * mudança: aquelas continuam apontando pro caminho original enquanto ele
 * existir.
 */
public class ImageStorageUtil {

    private static final Path DIRETORIO_IMAGENS =
            Paths.get(System.getProperty("user.home"), ".barberdesk", "images");

    public static String armazenar(File origem) throws IOException {
        Files.createDirectories(DIRETORIO_IMAGENS);

        String nomeArquivo = UUID.randomUUID() + extrairExtensao(origem.getName());
        Path destino = DIRETORIO_IMAGENS.resolve(nomeArquivo);

        Files.copy(origem.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        return destino.toAbsolutePath().toString();
    }

    private static String extrairExtensao(String nomeArquivo) {
        int idx = nomeArquivo.lastIndexOf('.');
        return idx >= 0 ? nomeArquivo.substring(idx) : "";
    }
}
