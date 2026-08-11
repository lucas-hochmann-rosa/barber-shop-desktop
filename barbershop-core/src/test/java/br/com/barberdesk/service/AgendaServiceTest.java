package br.com.barberdesk.service;

import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.Barbearia;
import br.com.barberdesk.model.OrigemContato;
import br.com.barberdesk.model.StatusAgendamento;
import br.com.barberdesk.service.fake.FakeAgendamentoRepository;
import br.com.barberdesk.service.fake.FakeClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de {@link AgendaService} com repositórios em memória (sem banco
 * real): transições de status, conflito de horário e restrição por
 * horário de funcionamento da barbearia.
 */
class AgendaServiceTest {

    private FakeAgendamentoRepository agendamentoRepository;
    private FakeClienteRepository clienteRepository;
    private AgendaService service;

    @BeforeEach
    void setUp() {
        agendamentoRepository = new FakeAgendamentoRepository();
        clienteRepository = new FakeClienteRepository();
        service = new AgendaService(agendamentoRepository, clienteRepository);
    }

    private Agendamento novoAgendamento(int barbeiroId, LocalDateTime dataHora, int duracaoMinutos, StatusAgendamento status) {
        Agendamento a = new Agendamento(1, 1, barbeiroId, "Cliente Teste", "11999999999",
                dataHora, OrigemContato.INSTAGRAM, status);
        a.setDuracaoMinutos(duracaoMinutos);
        return a;
    }

    // --- criarAgendamento ---

    @Test
    void criarAgendamentoInsereERegistraCliente() throws Exception {
        Agendamento a = novoAgendamento(5, LocalDateTime.of(2026, 8, 20, 10, 0), 30, StatusAgendamento.AGENDADO);

        int id = service.criarAgendamento(a);

        assertTrue(id > 0);
        assertEquals(1, clienteRepository.listarPorBarbearia(1).size());
        assertEquals("Cliente Teste", clienteRepository.listarPorBarbearia(1).get(0).getNome());
    }

    @Test
    void criarAgendamentoNaoFalhaSeRegistrarClienteFalhar() throws Exception {
        clienteRepository.setFalharAoRegistrar(true);
        Agendamento a = novoAgendamento(5, LocalDateTime.of(2026, 8, 20, 10, 0), 30, StatusAgendamento.AGENDADO);

        int id = service.criarAgendamento(a);

        assertTrue(id > 0, "o agendamento deve ser persistido mesmo que o registro do cliente falhe");
        assertNotNull(agendamentoRepository.buscarPorId(id));
    }

    // --- transições de status ---

    @Test
    void iniciarAtendimentoMudaStatusParaEmAtendimento() throws Exception {
        int id = agendamentoRepository.inserir(novoAgendamento(5, LocalDateTime.now().plusHours(1), 30, StatusAgendamento.AGENDADO));

        service.iniciarAtendimento(id);

        assertEquals(StatusAgendamento.EM_ATENDIMENTO, agendamentoRepository.buscarPorId(id).getStatus());
    }

    @Test
    void concluirAtendimentoMudaStatusParaConcluido() throws Exception {
        int id = agendamentoRepository.inserir(novoAgendamento(5, LocalDateTime.now(), 30, StatusAgendamento.EM_ATENDIMENTO));

        service.concluirAtendimento(id);

        assertEquals(StatusAgendamento.CONCLUIDO, agendamentoRepository.buscarPorId(id).getStatus());
    }

    @Test
    void cancelarAgendamentoMudaStatusEGravaMotivo() throws Exception {
        int id = agendamentoRepository.inserir(novoAgendamento(5, LocalDateTime.now().plusHours(1), 30, StatusAgendamento.AGENDADO));

        service.cancelarAgendamento(id, "Cliente desmarcou");

        Agendamento cancelado = agendamentoRepository.buscarPorId(id);
        assertEquals(StatusAgendamento.CANCELADO, cancelado.getStatus());
        assertEquals("Cliente desmarcou", cancelado.getMotivoCancelamento());
    }

    @Test
    void alterarStatusDeAgendamentoInexistenteNaoLancaExcecao() {
        assertDoesNotThrow(() -> service.iniciarAtendimento(9999));
    }

    // --- conflito de horário ---

    @Test
    void verificarConflitoDetectaSobreposicaoDeHorarioParaMesmoBarbeiro() {
        agendamentoRepository.inserir(novoAgendamento(5, LocalDateTime.of(2026, 8, 20, 10, 0), 30, StatusAgendamento.AGENDADO));

        // Novo agendamento das 10:15 às 10:45 sobrepõe o das 10:00 às 10:30.
        boolean conflito = agendamentoRepository.verificarConflito(5, LocalDateTime.of(2026, 8, 20, 10, 15), 30);

        assertTrue(conflito);
    }

    @Test
    void verificarConflitoNaoDetectaHorariosConsecutivos() {
        agendamentoRepository.inserir(novoAgendamento(5, LocalDateTime.of(2026, 8, 20, 10, 0), 30, StatusAgendamento.AGENDADO));

        // Novo agendamento começa exatamente quando o anterior termina — sem sobreposição.
        boolean conflito = agendamentoRepository.verificarConflito(5, LocalDateTime.of(2026, 8, 20, 10, 30), 30);

        assertFalse(conflito);
    }

    @Test
    void verificarConflitoIgnoraAgendamentosDeOutroBarbeiro() {
        agendamentoRepository.inserir(novoAgendamento(5, LocalDateTime.of(2026, 8, 20, 10, 0), 30, StatusAgendamento.AGENDADO));

        boolean conflito = agendamentoRepository.verificarConflito(7, LocalDateTime.of(2026, 8, 20, 10, 0), 30);

        assertFalse(conflito);
    }

    @Test
    void verificarConflitoIgnoraAgendamentosCanceladosOuConcluidos() {
        agendamentoRepository.inserir(novoAgendamento(5, LocalDateTime.of(2026, 8, 20, 10, 0), 30, StatusAgendamento.CANCELADO));
        agendamentoRepository.inserir(novoAgendamento(5, LocalDateTime.of(2026, 8, 20, 10, 0), 30, StatusAgendamento.CONCLUIDO));

        boolean conflito = agendamentoRepository.verificarConflito(5, LocalDateTime.of(2026, 8, 20, 10, 0), 30);

        assertFalse(conflito);
    }

    // --- horário de funcionamento ---

    @Test
    void dentroDoHorarioFuncionamentoSemRestricaoQuandoBarbeariaNaoDefineHorario() {
        Barbearia b = new Barbearia("Barbearia Teste", "00000-000", LocalDate.now(), "");
        assertTrue(service.dentroDoHorarioFuncionamento(b, LocalDateTime.of(2026, 8, 20, 3, 0), 30));
    }

    @Test
    void dentroDoHorarioFuncionamentoAceitaHorarioDentroDoIntervalo() {
        Barbearia b = new Barbearia("Barbearia Teste", "00000-000", LocalDate.now(), "");
        b.setHorarioAbertura(LocalTime.of(9, 0));
        b.setHorarioFechamento(LocalTime.of(18, 0));

        assertTrue(service.dentroDoHorarioFuncionamento(b, LocalDateTime.of(2026, 8, 20, 9, 0), 30));
        assertTrue(service.dentroDoHorarioFuncionamento(b, LocalDateTime.of(2026, 8, 20, 17, 30), 30));
    }

    @Test
    void dentroDoHorarioFuncionamentoRejeitaAntesDaAberturaOuAposOFechamento() {
        Barbearia b = new Barbearia("Barbearia Teste", "00000-000", LocalDate.now(), "");
        b.setHorarioAbertura(LocalTime.of(9, 0));
        b.setHorarioFechamento(LocalTime.of(18, 0));

        assertFalse(service.dentroDoHorarioFuncionamento(b, LocalDateTime.of(2026, 8, 20, 8, 30), 30));
        assertFalse(service.dentroDoHorarioFuncionamento(b, LocalDateTime.of(2026, 8, 20, 17, 45), 30), "termina depois do fechamento");
    }
}
