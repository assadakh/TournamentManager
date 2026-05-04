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

        // Validation 1 : Vérifier que les champs ne sont pas vides
        if (name.isEmpty() || game.isEmpty()) {
            showError("Veuillez remplir tous les champs !");
            return;
        }

        // Validation 2 : Vérifier la longueur minimale (au moins 2 caractères)
        if (name.length() < 2 || game.length() < 2) {
            showError("Le nom et le jeu doivent contenir au moins 2 caractères !");
            return;
        }

        // Validation 3 : Vérifier la longueur maximale (max 50 caractères)
        if (name.length() > 50 || game.length() > 50) {
            showError("Le nom et le jeu ne peuvent pas dépasser 50 caractères !");
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
