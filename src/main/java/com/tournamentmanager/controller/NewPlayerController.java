package com.tournamentmanager.controller;

import com.tournamentmanager.dao.PlayerDAO;
import com.tournamentmanager.model.Player;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewPlayerController {

    @FXML private TextField fieldName;
    @FXML private TextField fieldGame;

    @FXML
    public void handleCreer() {
        String name = fieldName.getText();
        String game = fieldGame.getText();

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
