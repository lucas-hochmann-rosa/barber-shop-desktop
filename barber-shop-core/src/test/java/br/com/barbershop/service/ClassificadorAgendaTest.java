package br.com.barbershop.service;

import br.com.barbershop.model.Agendamento;
import br.com.barbershop.model.ClassificacaoAgenda;
import br.com.barbershop.model.OrigemContato;
import br.com.barbershop.model.StatusAgendamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes de {@link ClassificadorAgenda}. Usa uma data de referência fixa
 * para os resultados serem determinísticos, sem depender do relógio do
 * sistema no momento em que o teste roda.
 */
class ClassificadorAgendaTest {

    private static final LocalDateTime REFERENCIA = LocalDateTime.of(2026, 3, 10, 14, 0);

    private Agendamento agendamento(LocalDateTime dataHora, StatusAgendamento status) {
        return new Agendamento(1, 1, 1, "Cliente Teste", "11999999999",
                dataHora, OrigemContato.INSTAGRAM, status);
    }

    @Test
    @DisplayName("agendamento cancelado retorna CANCELADO")
    void cancelado() {
        Agendamento a = agendamento(REFERENCIA.plusHours(1), StatusAgendamento.CANCELADO);
        assertEquals(ClassificacaoAgenda.CANCELADO, ClassificadorAgenda.classificar(a, REFERENCIA));
    }

    @Test
    @DisplayName("agendamento concluído retorna CONCLUIDO")
    void concluido() {
        Agendamento a = agendamento(REFERENCIA.minusHours(1), StatusAgendamento.CONCLUIDO);
        assertEquals(ClassificacaoAgenda.CONCLUIDO, ClassificadorAgenda.classificar(a, REFERENCIA));
    }

    @Test
    @DisplayName("agendamento em atendimento retorna EM_ANDAMENTO")
    void emAtendimento() {
        Agendamento a = agendamento(REFERENCIA.minusMinutes(10), StatusAgendamento.EM_ATENDIMENTO);
        assertEquals(ClassificacaoAgenda.EM_ANDAMENTO, ClassificadorAgenda.classificar(a, REFERENCIA));
    }

    @Test
    @DisplayName("agendado no passado retorna ATRASADO")
    void agendadoNoPassado() {
        Agendamento a = agendamento(REFERENCIA.minusMinutes(1), StatusAgendamento.AGENDADO);
        assertEquals(ClassificacaoAgenda.ATRASADO, ClassificadorAgenda.classificar(a, REFERENCIA));
    }

    @Test
    @DisplayName("exatamente na hora de referência não é considerado atrasado")
    void exatamenteNaHoraDeReferencia() {
        Agendamento a = agendamento(REFERENCIA, StatusAgendamento.AGENDADO);
        assertEquals(ClassificacaoAgenda.IMINENTE, ClassificadorAgenda.classificar(a, REFERENCIA));
    }

    @ParameterizedTest(name = "faltando {0} minuto(s) deve classificar como {1}")
    @DisplayName("classificação por proximidade do horário, incluindo as fronteiras de 60 e 120 minutos")
    @CsvSource({
            "30, IMINENTE",
            "60, IMINENTE",
            "61, PROXIMO",
            "120, PROXIMO",
            "121, DISTANTE"
    })
    void classificacaoPorProximidade(long minutosParaComecar, ClassificacaoAgenda esperado) {
        Agendamento a = agendamento(REFERENCIA.plusMinutes(minutosParaComecar), StatusAgendamento.AGENDADO);
        assertEquals(esperado, ClassificadorAgenda.classificar(a, REFERENCIA));
    }

    @Test
    @DisplayName("agendamento nulo lança IllegalArgumentException")
    void agendamentoNulo() {
        assertThrows(IllegalArgumentException.class, () -> ClassificadorAgenda.classificar(null, REFERENCIA));
    }

    @Test
    @DisplayName("data/hora nula lança IllegalArgumentException")
    void dataHoraNula() {
        Agendamento a = agendamento(null, StatusAgendamento.AGENDADO);
        assertThrows(IllegalArgumentException.class, () -> ClassificadorAgenda.classificar(a, REFERENCIA));
    }
}
