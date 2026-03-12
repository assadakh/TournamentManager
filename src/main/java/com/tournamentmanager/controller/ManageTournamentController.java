package com.tournamentmanager.controller;

import com.tournamentmanager.dao.PlayerDAO;
import com.tournamentmanager.dao.TournamentDAO;
import com.tournamentmanager.dao.TournamentPlayerDAO;
import com.tournamentmanager.model.Player;
import com.tournamentmanager.model.Tournament;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class ManageTournamentController {

    @FXML private ListView<Player> listPlayers;
    @FXML private ComboBox<Player> comboPlayers;
    @FXML private Label labelTournament;
    private Tournament tournament;

    @FXML
    public void initialize() {
        // rien pour l'instant, on attend setTournament()
    }

    public void setTournament(Tournament t) {
        this.tournament = t;
        labelTournament.setText("Gestion : " + t.getName());
        loadJoueurs();
    }

    public void loadJoueurs() {
        List<Player> dao = new PlayerDAO().findAll();
        listPlayers.getItems().setAll(dao);
        comboPlayers.getItems().setAll(dao);
    }

    @FXML
    public void handleAjouterJoueur() {
        Player joueur = comboPlayers.getValue();
        if (joueur != null) {
            new TournamentPlayerDAO().addPlayer(tournament.getId(), joueur.getId());
        }
        loadJoueurs();
    }

    @FXML
    public void handleGenererBracket() {
        System.out.println("Génération du bracket !");
    }
}
