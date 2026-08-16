package br.com.barbershop.app;

import br.com.barbershop.dao.ConexaoMySQL;
import br.com.barbershop.model.*;
import br.com.barbershop.service.*;
import br.com.barbershop.ui.TelaLogin;
import br.com.barbershop.ui.TelaCadastroInicial;
import com.formdev.flatlaf.FlatLightLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.sql.SQLException;

/**
 * Ponto de entrada da aplicação Barbershop.
 *
 * Responsável por configurar o Look & Feel (FlatLaf), validar a conexão com o
 * banco de dados e garantir que o schema exista, e então decidir qual tela
 * inicial exibir: se ainda não existe nenhuma barbearia cadastrada, abre a
 * tela de cadastro inicial; caso já exista, abre a tela de login.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Inicializa a aplicação na Event Dispatch Thread do Swing.
     *
     * Fluxo: aplica o Look & Feel FlatLaf, testa a conexão com o MySQL e
     * garante o schema do banco, verifica se já existe uma barbearia
     * cadastrada e abre a tela correspondente (login ou cadastro inicial).
     * Se a conexão com o banco falhar, exibe um diálogo de erro ao usuário,
     * registra o erro no log e encerra a aplicação.
     *
     * @param args argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();

                FabricaDeServicos fabrica = new FabricaDeServicos();

                // Testar conexão + garantir schema
                ConexaoMySQL.getConexao().close();
                fabrica.criarDatabaseInitService().ensureSchema();

                // Verificar se existe barbearia
                SetupService setupService = fabrica.criarSetupService();
                if (setupService.existeBarbearia()) {
                    // Abrir login
                    TelaLogin telaLogin = new TelaLogin();
                    telaLogin.setVisible(true);
                } else {
                    // Abrir cadastro inicial
                    TelaCadastroInicial telaCadastro = new TelaCadastroInicial();
                    telaCadastro.setVisible(true);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Erro ao conectar ao banco de dados:\n" + e.getMessage(),
                        "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                logger.error("Erro ao inicializar o Barbershop", e);
                System.exit(1);
            }
        });
    }
}
