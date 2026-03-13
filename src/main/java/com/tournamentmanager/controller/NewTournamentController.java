package com.tournamentmanager.controller;

import com.tournamentmanager.dao.TournamentDAO;
import com.tournamentmanager.model.Tournament;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewTournamentController {

    @FXML private TextField fieldName;
    @FXML private TextField fieldGame;
    @FXML private TextField fieldDate;

    @FXML
    public void handleCreer() {
        String name = fieldName.getText().trim();
        String game = fieldGame.getText().trim();
        String date = fieldDate.getText().trim();

        if (name.isEmpty() || game.isEmpty() || date.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs !");
            alert.showAndWait();
            return;
        }

        // Test
        System.out.println("Name: " + name);
        System.out.println("Game: " + game);
        System.out.println("Date: " + date);

        Tournament tournament = new Tournament(name, game, date);
        new TournamentDAO().create(tournament);

        Stage stage = (Stage) fieldName.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleAnnuler() {
        Stage stage = (Stage) fieldName.getScene().getWindow();
        stage.close();
    }
}
