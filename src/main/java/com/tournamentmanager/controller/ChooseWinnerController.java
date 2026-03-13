package com.tournamentmanager.controller;

import com.tournamentmanager.dao.MatchDAO;
import com.tournamentmanager.model.Match;
import com.tournamentmanager.model.Tournament;
import com.tournamentmanager.util.BracketGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.List;

public class ChooseWinnerController {

    @FXML private Button btnPlayer1;
    @FXML private Button btnPlayer2;
    private Match match;
    private Tournament tournament;

    public void setMatch(Match m) {
        this.match = m;
        btnPlayer1.setText(m.getPlayer1().getName());
        btnPlayer2.setText(m.getPlayer2().getName());
    }

    public void setTournament(Tournament t) {
        this.tournament = t;
    }

    @FXML
    public void handlePlayer1() {
        match.setWinner(match.getPlayer1());
        new MatchDAO().updateWinner(match);
        Stage stage = (Stage) btnPlayer1.getScene().getWindow();
        stage.close();

        // Récupère tous les matchs du round actuel
        List<Match> allMatches = new MatchDAO().findByTournaments(match.getTournamentId());
        List<Match> currentRound = allMatches.stream()
                .filter(m -> m.getRound() == match.getRound()).toList();

        // Vérifie si TOUS les matchs du round sont joués
        boolean allPlayed = currentRound.stream().allMatch(Match::isPlayed);

        if (allPlayed && currentRound.size() > 1) {
            // Génère le round suivant
            List<Match> nextRound = BracketGenerator.generateNextRound(currentRound, match.getTournamentId());
            MatchDAO matchDAO = new MatchDAO();
            for (Match m : nextRound) {
                matchDAO.create(m);
            }
        }
    }

    @FXML
    public void handlePlayer2() {
        match.setWinner(match.getPlayer2());
        new MatchDAO().updateWinner(match);
        Stage stage = (Stage) btnPlayer2.getScene().getWindow();
        stage.close();

        // Récupère tous les matchs du round actuel
        List<Match> allMatches = new MatchDAO().findByTournaments(match.getTournamentId());
        List<Match> currentRound = allMatches.stream()
                .filter(m -> m.getRound() == match.getRound()).toList();

        // Vérifie si TOUS les matchs du round sont joués
        boolean allPlayed = currentRound.stream().allMatch(Match::isPlayed);

        if (allPlayed && currentRound.size() > 1) {
            // Génère le round suivant
            List<Match> nextRound = BracketGenerator.generateNextRound(currentRound, match.getTournamentId());
            MatchDAO matchDAO = new MatchDAO();
            for (Match m : nextRound) {
                matchDAO.create(m);
            }
        }
    }
}