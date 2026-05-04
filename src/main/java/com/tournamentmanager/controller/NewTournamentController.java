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

        // Validation 1 : Vérifier que les champs ne sont pas vides
        if (name.isEmpty() || game.isEmpty() || date.isEmpty()) {
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

        // Validation 4 : Vérifier le format de la date (jj/mm/aaaa)
        if (!isValidDate(date)) {
            showError("Format de date invalide ! Utilisez le format jj/mm/aaaa (ex: 15/03/2024)");
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

    // Méthode pour vérifier si la date est au bon format (jj/mm/aaaa)
    private boolean isValidDate(String date) {
        // Vérifier le format avec une expression régulière simple
        // Format attendu : 2 chiffres / 2 chiffres / 4 chiffres
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        // Extraire jour, mois, année
        String[] parts = date.split("/");
        int jour = Integer.parseInt(parts[0]);
        int mois = Integer.parseInt(parts[1]);
        int annee = Integer.parseInt(parts[2]);

        // Vérifier que les valeurs sont cohérentes
        if (mois < 1 || mois > 12) {
            return false; // Mois invalide
        }
        if (jour < 1 || jour > 31) {
            return false; // Jour invalide
        }
        if (annee < 2000 || annee > 2100) {
            return false; // Année hors limite raisonnable
        }

        return true;
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
