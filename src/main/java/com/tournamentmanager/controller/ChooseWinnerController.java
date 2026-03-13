package com.tournamentmanager.controller;

import com.tournamentmanager.dao.MatchDAO;
import com.tournamentmanager.model.Match;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ChooseWinnerController {

    @FXML private Button btnPlayer1;
    @FXML private Button btnPlayer2;
    private Match match;

    public void setMatch(Match m) {
        this.match = m;
        btnPlayer1.setText(m.getPlayer1().getName());
        btnPlayer2.setText(m.getPlayer2().getName());
    }

    @FXML
    public void handlePlayer1() {
        match.setWinner(match.getPlayer1());
        new MatchDAO().updateWinner(match);
        Stage stage = (Stage) btnPlayer1.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handlePlayer2() {
        match.setWinner(match.getPlayer2());
        new MatchDAO().updateWinner(match);
        Stage stage = (Stage) btnPlayer2.getScene().getWindow();
        stage.close();
    }
}