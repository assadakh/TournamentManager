package com.tournamentmanager.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TournamentStatusTest {

    private static final Player P1 = new Player(1, "Alice", "FIFA");
    private static final Player P2 = new Player(2, "Bob",   "FIFA");
    private static final Player P3 = new Player(3, "Carol", "FIFA");
    private static final Player P4 = new Player(4, "Dave",  "FIFA");

    // ------------------------------------------------------------------ statut initial

    @Test
    void nouveauTournoiEstEnAttente() {
        Tournament t = new Tournament("Open", "FIFA", "10/06/2026");
        assertEquals("En attente", t.getStatus());
    }

    // ------------------------------------------------------------------ détection fin de tournoi

    /**
     * Reproduit la logique de ChooseWinnerController.handleWinner() :
     * maxRound = 1 match avec gagnant → tournoi terminé.
     */
    private boolean estTermine(List<Match> allMatches) {
        int maxRound = allMatches.stream().mapToInt(Match::getRound).max().orElse(0);
        List<Match> lastRound = allMatches.stream()
                .filter(m -> m.getRound() == maxRound).toList();
        return lastRound.size() == 1 && lastRound.get(0).isPlayed();
    }

    @Test
    void finaleJoueeTermineLetournoi() {
        Match finale = new Match(1, 1, P1, P2);
        finale.setWinner(P1);
        assertTrue(estTermine(List.of(finale)));
    }

    @Test
    void finaleSansGagnantNeterminePas() {
        Match finale = new Match(1, 1, P1, P2);
        assertFalse(estTermine(List.of(finale)));
    }

    @Test
    void roundIntermediaireNeterminePas() {
        // Round 1 : 2 matchs joués, Round 2 : 1 match non joué
        Match m1 = new Match(1, 1, P1, P2); m1.setWinner(P1);
        Match m2 = new Match(1, 1, P3, P4); m2.setWinner(P3);
        Match finale = new Match(1, 2, P1, P3);

        assertFalse(estTermine(List.of(m1, m2, finale)));
    }

    @Test
    void tournoiQuatreJoueursFinaleJouee() {
        // Round 1 : 2 matchs joués
        Match m1 = new Match(1, 1, P1, P2); m1.setWinner(P1);
        Match m2 = new Match(1, 1, P3, P4); m2.setWinner(P3);
        // Round 2 : finale jouée
        Match finale = new Match(1, 2, P1, P3); finale.setWinner(P1);

        assertTrue(estTermine(List.of(m1, m2, finale)));
    }

    @Test
    void tournoiQuatreJoueursFinaleNonJouee() {
        Match m1 = new Match(1, 1, P1, P2); m1.setWinner(P1);
        Match m2 = new Match(1, 1, P3, P4); m2.setWinner(P3);
        Match finale = new Match(1, 2, P1, P3);

        assertFalse(estTermine(List.of(m1, m2, finale)));
    }

    // ------------------------------------------------------------------ détection round complet

    /**
     * Reproduit la vérification : tous les matchs du round actuel sont joués.
     */
    private boolean roundComplet(List<Match> allMatches, int round) {
        List<Match> currentRound = allMatches.stream()
                .filter(m -> m.getRound() == round).toList();
        return currentRound.stream().allMatch(Match::isPlayed);
    }

    @Test
    void roundCompletQuandTousJoues() {
        Match m1 = new Match(1, 1, P1, P2); m1.setWinner(P1);
        Match m2 = new Match(1, 1, P3, P4); m2.setWinner(P3);
        assertTrue(roundComplet(List.of(m1, m2), 1));
    }

    @Test
    void roundIncompletSiUnMatchNonJoue() {
        Match m1 = new Match(1, 1, P1, P2); m1.setWinner(P1);
        Match m2 = new Match(1, 1, P3, P4);
        assertFalse(roundComplet(List.of(m1, m2), 1));
    }

    @Test
    void matchJoueQuandGagnantPresent() {
        Match m = new Match(1, 1, P1, P2);
        m.setWinner(P1);
        assertTrue(m.isPlayed());
    }

    @Test
    void matchNonJoueSansGagnant() {
        Match m = new Match(1, 1, P1, P2);
        assertFalse(m.isPlayed());
    }
}
