package com.tournamentmanager.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ------------------------------------------------------------------ isValidName

    @Test
    void nomValide() {
        assertTrue(InputValidator.isValidName("Alice"));
    }

    @Test
    void nomAvecAccents() {
        assertTrue(InputValidator.isValidName("Élodie"));
    }

    @Test
    void nomAvecApostrophe() {
        assertTrue(InputValidator.isValidName("O'Brien"));
    }

    @Test
    void nomAvecTiret() {
        assertTrue(InputValidator.isValidName("Jean-Pierre"));
    }

    @Test
    void nomLongueurMax() {
        assertTrue(InputValidator.isValidName("A".repeat(50)));
    }

    @Test
    void nomVide() {
        assertFalse(InputValidator.isValidName(""));
    }

    @Test
    void nomNull() {
        assertFalse(InputValidator.isValidName(null));
    }

    @Test
    void nomTropCourt() {
        assertFalse(InputValidator.isValidName("A"));
    }

    @Test
    void nomTropLong() {
        assertFalse(InputValidator.isValidName("A".repeat(51)));
    }

    @Test
    void nomAvecCaracteresSpeciaux() {
        assertFalse(InputValidator.isValidName("<script>alert(1)</script>"));
    }

    @Test
    void nomAvecInjectionSQL() {
        assertFalse(InputValidator.isValidName("'; DROP TABLE players; --"));
    }

    @Test
    void nomAvecGuillemets() {
        assertFalse(InputValidator.isValidName("nom\"invalide"));
    }

    // ------------------------------------------------------------------ isValidDate

    @Test
    void dateFuture() {
        String demain = LocalDate.now().plusDays(1).format(FMT);
        assertTrue(InputValidator.isValidDate(demain));
    }

    @Test
    void dateAujourdhui() {
        String aujourd_hui = LocalDate.now().format(FMT);
        assertTrue(InputValidator.isValidDate(aujourd_hui));
    }

    @Test
    void datePassee() {
        assertFalse(InputValidator.isValidDate("01/01/2020"));
    }

    @Test
    void dateFormatISO() {
        assertFalse(InputValidator.isValidDate("2025-12-25"));
    }

    @Test
    void dateFormatAmericain() {
        assertFalse(InputValidator.isValidDate("12/25/2025"));
    }

    @Test
    void dateJourInexistant() {
        assertFalse(InputValidator.isValidDate("31/02/2025"));
    }

    @Test
    void dateMoisInvalide() {
        assertFalse(InputValidator.isValidDate("01/13/2025"));
    }

    @Test
    void dateNull() {
        assertFalse(InputValidator.isValidDate(null));
    }

    @Test
    void dateVide() {
        assertFalse(InputValidator.isValidDate(""));
    }
}
