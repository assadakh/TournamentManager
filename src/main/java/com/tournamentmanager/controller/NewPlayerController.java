package com.tournamentmanager.controller;

import com.tournamentmanager.dao.PlayerDAO;
import com.tournamentmanager.model.Player;
import com.tournamentmanager.util.InputValidator;
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

        if (!InputValidator.isValidName(name) || !InputValidator.isValidName(game)) {
            showError("Les champs doivent contenir entre 2 et 50 caractères (lettres, chiffres, espaces, tirets, apostrophes uniquement).");
            return;
        }

        Player player = new Player(name, game);
        new PlayerDAO().create(player);

        Stage stage = (Stage) fieldName.getScene().getWindow();
        stage.close();
    }

    // Méthode pour afficher les messages d'erreur (évite de répéter le code)
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleAnnuler() {
        Stage stage = (Stage) fieldName.getScene().getWindow();
        stage.close();
    }
}
