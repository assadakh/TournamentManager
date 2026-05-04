package com.tournamentmanager.controller;

import com.tournamentmanager.dao.MatchDAO;
import com.tournamentmanager.dao.TournamentDAO;
import com.tournamentmanager.model.Match;
import com.tournamentmanager.model.Player;
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
        handleWinner(match.getPlayer1());
    }

    @FXML
    public void handlePlayer2() {
        handleWinner(match.getPlayer2());
    }

    private void handleWinner(Player winner) {
        match.setWinner(winner);
        new MatchDAO().updateWinner(match);

        Stage stage = (Stage) btnPlayer1.getScene().getWindow();
        stage.close();

        List<Match> allMatches = new MatchDAO().findByTournaments(match.getTournamentId());

        int maxRound = allMatches.stream().mapToInt(Match::getRound).max().orElse(0);
        List<Match> lastRound = allMatches.stream()
                .filter(m -> m.getRound() == maxRound).toList();

        if (lastRound.size() == 1 && lastRound.get(0).isPlayed()) {
            tournament.setStatus("Terminé");
            new TournamentDAO().update(tournament);
            return;
        }

        List<Match> currentRound = allMatches.stream()
                .filter(m -> m.getRound() == match.getRound()).toList();

        boolean allPlayed = currentRound.stream().allMatch(Match::isPlayed);
        if (allPlayed && currentRound.size() > 1) {
            List<Match> nextRound = BracketGenerator.generateNextRound(currentRound, match.getTournamentId());
            MatchDAO matchDAO = new MatchDAO();
            for (Match m : nextRound) {
                matchDAO.create(m);
            }
        }
    }
}
