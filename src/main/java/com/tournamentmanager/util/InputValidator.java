package com.tournamentmanager.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InputValidator {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 50;
    private static final String ALLOWED_CHARS = "[a-zA-ZÀ-ÿ0-9 '\\-]+";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static boolean isValidName(String value) {
        if (value == null || value.isBlank()) return false;
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) return false;
        return value.matches(ALLOWED_CHARS);
    }

    public static boolean isValidDate(String date) {
        if (date == null || !date.matches("\\d{2}/\\d{2}/\\d{4}")) return false;
        try {
            LocalDate parsed = LocalDate.parse(date, DATE_FORMATTER);
            return !parsed.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
