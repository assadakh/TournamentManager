package com.tournamentmanager.controller;

import com.tournamentmanager.dao.PlayerDAO;
import com.tournamentmanager.model.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewPlayerController {

    @FXML private TextField fieldName;
    @FXML private TextField fieldGame;

    @FXML
    public void handleCreer() {
        String name = fieldName.getText().trim();
        String game = fieldGame.getText().trim();

        if (name.isEmpty() || game.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs !");
            alert.showAndWait();
            return;
        }

        //Test
        System.out.println("Name: " + name);
        System.out.println("Game: " + game);

        Player player = new Player(name, game);
        new PlayerDAO().create(player);

        Stage stage = (Stage) fieldName.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleAnnuler() {
        Stage stage = (Stage) fieldName.getScene().getWindow();
        stage.close();
    }
}
