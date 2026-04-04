package br.com.barberdesk.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilTest {

    @Test
    void formatDateTimeRetornaVazioParaNulo() {
        assertEquals("", DateTimeUtil.formatDateTime(null));
    }

    @Test
    void formatDateRetornaVazioParaNulo() {
        assertEquals("", DateTimeUtil.formatDate(null));
    }

    @Test
    void formatDateTimeUsaPadraoBrasileiro() {
        LocalDateTime dataHora = LocalDateTime.of(2026, 3, 8, 14, 30);
        assertEquals("08/03/2026 14:30", DateTimeUtil.formatDateTime(dataHora));
    }

    @Test
    void parseDateTimeComDataEHoraSeparadas() {
        LocalDateTime esperado = LocalDateTime.of(2026, 3, 8, 14, 30);
        assertEquals(esperado, DateTimeUtil.parseDateTime("08/03/2026", "14:30"));
    }

    @Test
    void parseDateTimeComStringUnica() {
        LocalDateTime esperado = LocalDateTime.of(2026, 3, 8, 14, 30);
        assertEquals(esperado, DateTimeUtil.parseDateTime("08/03/2026 14:30"));
    }

    @Test
    void formatEParseSaoRoundTrip() {
        LocalDateTime original = LocalDateTime.of(2026, 12, 25, 9, 5);
        String formatado = DateTimeUtil.formatDateTime(original);
        assertEquals(original, DateTimeUtil.parseDateTime(formatado));
    }

    @Test
    void parseDateUsaPadraoBrasileiro() {
        assertEquals(LocalDate.of(2026, 3, 8), DateTimeUtil.parseDate("08/03/2026"));
    }
}
