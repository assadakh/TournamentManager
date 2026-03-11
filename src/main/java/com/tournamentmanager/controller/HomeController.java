package com.tournamentmanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // s'exécute au chargement
    }

    @FXML
    public void handleVoirTournois() {
        System.out.println("Voir les tournois");
    }

    @FXML
    public void handleGererJoueurs() {
        System.out.println("Gérer les joueurs");
    }
}
