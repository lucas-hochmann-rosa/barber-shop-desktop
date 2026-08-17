package br.com.barbershop.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de {@link DateTimeUtil}: formatação/parse no padrão brasileiro
 * (dd/MM/yyyy HH:mm), round-trip formatar→parsear e tratamento de null.
 */
class DateTimeUtilTest {

    /** LocalDateTime nulo formata pra string vazia em vez de lançar exceção. */
    @Test
    void formatDateTimeRetornaVazioParaNulo() {
        assertEquals("", DateTimeUtil.formatDateTime(null));
    }

    /** LocalDate nulo formata pra string vazia em vez de lançar exceção. */
    @Test
    void formatDateRetornaVazioParaNulo() {
        assertEquals("", DateTimeUtil.formatDate(null));
    }

    /** formatDateTime usa o padrão dd/MM/yyyy HH:mm. */
    @Test
    void formatDateTimeUsaPadraoBrasileiro() {
        LocalDateTime dataHora = LocalDateTime.of(2026, 3, 8, 14, 30);
        assertEquals("08/03/2026 14:30", DateTimeUtil.formatDateTime(dataHora));
    }

    /** Overload que recebe data e hora como strings separadas (campos distintos na UI). */
    @Test
    void parseDateTimeComDataEHoraSeparadas() {
        LocalDateTime esperado = LocalDateTime.of(2026, 3, 8, 14, 30);
        assertEquals(esperado, DateTimeUtil.parseDateTime("08/03/2026", "14:30"));
    }

    /** Overload que recebe data e hora já combinadas numa única string. */
    @Test
    void parseDateTimeComStringUnica() {
        LocalDateTime esperado = LocalDateTime.of(2026, 3, 8, 14, 30);
        assertEquals(esperado, DateTimeUtil.parseDateTime("08/03/2026 14:30"));
    }

    /** Formatar e depois parsear de volta deve devolver o valor original, sem perda. */
    @Test
    void formatEParseSaoRoundTrip() {
        LocalDateTime original = LocalDateTime.of(2026, 12, 25, 9, 5);
        String formatado = DateTimeUtil.formatDateTime(original);
        assertEquals(original, DateTimeUtil.parseDateTime(formatado));
    }

    /** parseDate (só data, sem hora) também usa o padrão dd/MM/yyyy. */
    @Test
    void parseDateUsaPadraoBrasileiro() {
        assertEquals(LocalDate.of(2026, 3, 8), DateTimeUtil.parseDate("08/03/2026"));
    }
}
