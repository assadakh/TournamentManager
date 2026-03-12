package com.tournamentmanager.controller;

import com.tournamentmanager.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // s'exécute au chargement
    }

    @FXML
    public void handleVoirTournois() throws IOException {
        App.setRoot("fxml/tournaments");
    }

    @FXML
    public void handleGererJoueurs() throws IOException {
        App.setRoot("fxml/players");
    }
}
