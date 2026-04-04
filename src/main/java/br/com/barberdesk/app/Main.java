package br.com.barberdesk.app;

import br.com.barberdesk.dao.ConexaoMySQL;
import br.com.barberdesk.model.*;
import br.com.barberdesk.service.*;
import br.com.barberdesk.ui.TelaLogin;
import br.com.barberdesk.ui.TelaCadastroInicial;
import com.formdev.flatlaf.FlatLightLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.sql.SQLException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();

                // Testar conexão + garantir schema
                ConexaoMySQL.getConexao().close();
                new DatabaseInitService().ensureSchema();

                // Verificar se existe barbearia
                SetupService setupService = new SetupService();
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
                logger.error("Erro ao inicializar o BarberDesk", e);
                System.exit(1);
            }
        });
    }
}
