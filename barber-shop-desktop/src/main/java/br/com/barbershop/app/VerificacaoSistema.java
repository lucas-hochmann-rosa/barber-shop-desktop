package br.com.barbershop.app;

import br.com.barbershop.dao.ConexaoMySQL;
import br.com.barbershop.model.Agendamento;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.model.Barbeiro;
import br.com.barbershop.model.OrigemContato;
import br.com.barbershop.model.Servico;
import br.com.barbershop.model.StatusAgendamento;
import br.com.barbershop.model.Usuario;
import br.com.barbershop.service.AgendaService;
import br.com.barbershop.service.AuthService;
import br.com.barbershop.service.CatalogoService;
import br.com.barbershop.service.RelatorioService;
import br.com.barbershop.service.SetupService;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Utilitário de linha de comando que valida, contra um banco MySQL real
 * (ex.: o do {@code docker-compose.yml} da raiz do projeto), que as peças
 * centrais do sistema continuam funcionando de ponta a ponta: conexão com o
 * banco, schema/migrações, autenticação, criação e cancelamento de
 * agendamento (com detecção de conflito) e geração de relatório.
 * <p>
 * Não é um teste JUnit - os testes em {@code src/test} usam repositórios em
 * memória e rodam a cada {@code mvn test}, sem tocar num banco de verdade.
 * Este é um smoke test operacional, pensado para ser executado manualmente
 * depois de um deploy ou de uma migração de schema, quando "compilou e os
 * testes passaram" não é garantia suficiente de que o sistema funciona
 * contra o banco real.
 * <p>
 * Cria seus próprios dados de verificação - uma barbearia isolada, com nome
 * prefixado e sufixo único - e remove tudo ao final, com sucesso ou falha,
 * para não deixar resíduo na base.
 * <p>
 * Uso: {@code mvn -pl barber-shop-desktop exec:java -Dexec.mainClass=br.com.barbershop.app.VerificacaoSistema}
 * ou, a partir do jar:
 * {@code java -cp barber-shop-desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar br.com.barbershop.app.VerificacaoSistema}.
 * Sai com código 0 se todas as checagens passarem, ou 1 caso alguma falhe.
 */
public class VerificacaoSistema {

    private static final String PREFIXO_BARBEARIA = "Verificação Automática ";
    private static final String SENHA_TESTE = "senhaDeVerificacao123";

    private int totalChecagens = 0;
    private int falhas = 0;

    public static void main(String[] args) {
        // Terminais Windows nem sempre usam UTF-8 como codepage padrão, o que
        // corrompe os acentos do relatório - força UTF-8 na saída, independente
        // de como o processo foi iniciado.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.exit(new VerificacaoSistema().executar());
    }

    /** @return código de saída do processo: 0 se todas as checagens passaram, 1 caso alguma tenha falhado. */
    public int executar() {
        System.out.println("=== Barbershop - Verificação do Sistema ===");
        System.out.println();

        FabricaDeServicos fabrica = new FabricaDeServicos();
        String sufixo = String.valueOf(System.currentTimeMillis());

        Barbearia[] barbeariaRef = new Barbearia[1];
        Usuario[] usuarioRef = new Usuario[1];
        Servico[] servicoRef = new Servico[1];
        Barbeiro[] barbeiroRef = new Barbeiro[1];
        Integer[] agendamentoIdRef = new Integer[1];

        try {
            checar("Conexão com o banco de dados", () -> {
                try (Connection c = ConexaoMySQL.getConexao()) {
                    if (!c.isValid(3)) throw new IllegalStateException("conexão obtida, mas inválida");
                }
            });

            checar("Schema do banco (migrações)", () -> fabrica.criarDatabaseInitService().ensureSchema());

            checar("Cadastro inicial (barbearia + usuário + serviço + barbeiro)", () -> {
                SetupService setupService = fabrica.criarSetupService();
                Barbearia barbearia = new Barbearia(PREFIXO_BARBEARIA + sufixo, "00000-000", LocalDate.now(), "Barbearia temporária de verificação");
                Servico servico = new Servico(0, "Corte Verificação", new BigDecimal("50.00"), null);
                Barbeiro barbeiro = new Barbeiro(0, "Barbeiro Verificação", null);

                Usuario usuario = setupService.criarCadastroInicial(barbearia, "verificacao_" + sufixo, SENHA_TESTE,
                        List.of(servico), List.of(barbeiro));

                barbeariaRef[0] = barbearia;
                usuarioRef[0] = usuario;

                // inserir() não devolve o objeto com o id preenchido - busca de volta via CatalogoService.
                CatalogoService catalogoService = fabrica.criarCatalogoService();
                servicoRef[0] = catalogoService.listarServicos(barbearia.getId()).get(0);
                barbeiroRef[0] = catalogoService.listarBarbeiros(barbearia.getId()).get(0);
            });

            checar("Autenticação com senha correta", () -> {
                AuthService authService = fabrica.criarAuthService();
                if (authService.autenticar(usuarioRef[0].getLogin(), SENHA_TESTE) == null) {
                    throw new IllegalStateException("autenticação com senha correta deveria ter sucesso");
                }
            });

            checar("Autenticação com senha incorreta é rejeitada", () -> {
                AuthService authService = fabrica.criarAuthService();
                if (authService.autenticar(usuarioRef[0].getLogin(), "senhaErrada") != null) {
                    throw new IllegalStateException("autenticação com senha incorreta não deveria ter sucesso");
                }
            });

            checar("Criação de agendamento", () -> {
                AgendaService agendaService = fabrica.criarAgendaService();
                LocalDateTime dataHora = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
                Agendamento agendamento = new Agendamento(barbeariaRef[0].getId(), servicoRef[0].getId(), barbeiroRef[0].getId(),
                        "Cliente Verificação", "49999999999", dataHora, OrigemContato.OUTRO, StatusAgendamento.AGENDADO);
                agendamento.setDuracaoMinutos(30);
                agendamentoIdRef[0] = agendaService.criarAgendamento(agendamento);
            });

            checar("Conflito de horário é detectado corretamente", () -> {
                AgendaService agendaService = fabrica.criarAgendaService();
                Agendamento criado = agendaService.buscarPorId(agendamentoIdRef[0]);
                boolean conflito = agendaService.verificarConflito(barbeiroRef[0].getId(), criado.getDataHora(), 30);
                if (!conflito) throw new IllegalStateException("deveria detectar conflito no mesmo horário/barbeiro");
            });

            checar("Cancelamento de agendamento", () -> {
                AgendaService agendaService = fabrica.criarAgendaService();
                agendaService.cancelarAgendamento(agendamentoIdRef[0], "Verificação automática");
                Agendamento cancelado = agendaService.buscarPorId(agendamentoIdRef[0]);
                if (cancelado.getStatus() != StatusAgendamento.CANCELADO) {
                    throw new IllegalStateException("status deveria ser CANCELADO após o cancelamento");
                }
            });

            checar("Geração de relatório (faturamento/serviços/ranking)", () -> {
                RelatorioService relatorioService = fabrica.criarRelatorioService();
                int barbeariaId = barbeariaRef[0].getId();
                LocalDate inicio = LocalDate.now().minusDays(1);
                LocalDate fim = LocalDate.now().plusDays(1);
                relatorioService.faturamentoTotal(barbeariaId, inicio, fim);
                relatorioService.servicosMaisVendidos(barbeariaId, inicio, fim);
                relatorioService.rankingBarbeiros(barbeariaId, inicio, fim);
            });

        } finally {
            limpar(barbeariaRef[0], agendamentoIdRef[0]);
        }

        System.out.println();
        System.out.println(totalChecagens + " checagem(ns), " + falhas + " falha(s).");
        return falhas == 0 ? 0 : 1;
    }

    private void checar(String nome, Checagem checagem) {
        totalChecagens++;
        try {
            checagem.executar();
            System.out.println("[OK]    " + nome);
        } catch (Exception e) {
            falhas++;
            System.out.println("[FALHA] " + nome + " - " + e.getMessage());
        }
    }

    /**
     * Remove todos os dados criados pela verificação, na ordem que respeita
     * as chaves estrangeiras (agendamentos/clientes/serviços/barbeiros antes
     * da barbearia, usuário por último). Roda mesmo se alguma checagem
     * falhou no meio do caminho, para nunca deixar resíduo na base - por
     * isso usa SQL direto: nem todo repositório tem um método de exclusão
     * (barbearia e usuário nunca são removidos pelo fluxo normal do app).
     */
    private void limpar(Barbearia barbearia, Integer agendamentoId) {
        if (barbearia == null || barbearia.getId() <= 0) return;
        int barbeariaId = barbearia.getId();

        try (Connection conn = ConexaoMySQL.getConexao()) {
            executar(conn, "DELETE FROM agendamentos WHERE barbearia_id = ?", barbeariaId);
            executar(conn, "DELETE FROM clientes WHERE barbearia_id = ?", barbeariaId);
            executar(conn, "DELETE FROM servicos WHERE barbearia_id = ?", barbeariaId);
            executar(conn, "DELETE FROM barbeiros WHERE barbearia_id = ?", barbeariaId);
            executar(conn, "DELETE FROM usuarios WHERE barbearia_id = ?", barbeariaId);
            executar(conn, "DELETE FROM barbearias WHERE id = ?", barbeariaId);
        } catch (Exception e) {
            System.out.println("[AVISO] Falha ao limpar dados de verificação (barbearia id=" + barbeariaId + "): " + e.getMessage());
        }
    }

    private void executar(Connection conn, String sql, int id) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @FunctionalInterface
    private interface Checagem {
        void executar() throws Exception;
    }
}
