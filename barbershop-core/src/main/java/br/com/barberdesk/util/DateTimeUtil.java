package br.com.barberdesk.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitário central de formatação e parse de datas/horas da aplicação.
 *
 * Padroniza os formatos usados em toda a interface: datas no padrão
 * brasileiro "dd/MM/yyyy", horas "HH:mm" e data+hora "dd/MM/yyyy HH:mm".
 * Concentrar os formatters aqui evita que cada tela reimplemente seu próprio
 * padrão de exibição/parse.
 */
public class DateTimeUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Formata uma data no padrão "dd/MM/yyyy".
     *
     * @param date data a formatar, pode ser {@code null}
     * @return a data formatada, ou string vazia se {@code date} for {@code null}
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    /**
     * Formata uma data/hora no padrão "dd/MM/yyyy HH:mm".
     *
     * @param dateTime data/hora a formatar, pode ser {@code null}
     * @return a data/hora formatada, ou string vazia se {@code dateTime} for {@code null}
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }

    /**
     * Formata apenas a parte de hora de uma data/hora, no padrão "HH:mm".
     *
     * @param dateTime data/hora de origem, pode ser {@code null}
     * @return a hora formatada, ou string vazia se {@code dateTime} for {@code null}
     */
    public static String formatTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(TIME_FORMATTER) : "";
    }

    /**
     * Converte uma string no formato "dd/MM/yyyy" para {@link LocalDate}.
     *
     * @param dateStr texto da data no formato esperado
     * @return a data convertida
     * @throws java.time.format.DateTimeParseException se {@code dateStr} não estiver no formato esperado
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * Combina uma string de data ("dd/MM/yyyy") e uma de hora ("HH:mm"),
     * separadas por espaço, e converte o resultado para {@link LocalDateTime}.
     *
     * @param dateStr texto da data no formato "dd/MM/yyyy"
     * @param timeStr texto da hora no formato "HH:mm"
     * @return a data/hora combinada
     * @throws java.time.format.DateTimeParseException se a combinação não estiver no formato esperado
     */
    public static LocalDateTime parseDateTime(String dateStr, String timeStr) {
        String combined = dateStr + " " + timeStr;
        return LocalDateTime.parse(combined, DATETIME_FORMATTER);
    }

    /**
     * Converte uma string no formato "dd/MM/yyyy HH:mm" para {@link LocalDateTime}.
     *
     * @param dateTimeStr texto de data/hora já combinado
     * @return a data/hora convertida
     * @throws java.time.format.DateTimeParseException se {@code dateTimeStr} não estiver no formato esperado
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    /**
     * Formata um {@link LocalTime} no padrão "HH:mm".
     *
     * @param time horário a formatar, pode ser {@code null}
     * @return o horário formatado, ou string vazia se {@code time} for {@code null}
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "";
    }

    /**
     * Converte uma string no formato "HH:mm" para {@link LocalTime}.
     *
     * @param timeStr texto do horário no formato esperado
     * @return o horário convertido
     * @throws java.time.format.DateTimeParseException se {@code timeStr} não estiver no formato esperado
     */
    public static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }
}
