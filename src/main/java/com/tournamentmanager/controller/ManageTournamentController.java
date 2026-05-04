package com.tournamentmanager.controller;

import com.tournamentmanager.App;
import com.tournamentmanager.dao.MatchDAO;
import com.tournamentmanager.dao.PlayerDAO;
import com.tournamentmanager.dao.TournamentPlayerDAO;
import com.tournamentmanager.model.Match;
import com.tournamentmanager.model.Player;
import com.tournamentmanager.model.Tournament;
import com.tournamentmanager.util.BracketGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ManageTournamentController {

    @FXML private ListView<Player> listPlayers;
    @FXML private ComboBox<Player> comboPlayers;
    @FXML private Label labelTournament;
    @FXML private Label labelError;
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
        // Liste des joueurs inscrits au tournoi
        List<Player> inscrits = new TournamentPlayerDAO().getPlayers(tournament.getId());
        listPlayers.getItems().setAll(inscrits);

        // Tous les joueurs disponibles dans le ComboBox
        List<Player> tous = new PlayerDAO().findAll();
        comboPlayers.getItems().setAll(tous);
    }

    @FXML
    public void handleAjouterJoueur() {
        Player joueur = comboPlayers.getValue();
        labelError.setText("");
        if (joueur == null) return;

        TournamentPlayerDAO dao = new TournamentPlayerDAO();
        if (dao.isPlayerInTournament(tournament.getId(), joueur.getId())) {
            labelError.setText("Ce joueur est déjà inscrit dans ce tournoi.");
            return;
        }
        dao.addPlayer(tournament.getId(), joueur.getId());
        loadJoueurs();
    }

    @FXML
    public void handleGenererBracket() throws IOException {
        List<Match> existingMatches = new MatchDAO().findByTournaments(tournament.getId());

        if (existingMatches.isEmpty()) {
            // Pas de bracket existant -> on en génère un nouveau
            List<Player> players = new TournamentPlayerDAO().getPlayers(tournament.getId());
            if (players.size() < 2) {
                System.out.println("Pas assez de joueurs !");
                return;
            }
            List<Match> matches = BracketGenerator.generateFirstRound(players, tournament.getId());
            MatchDAO matchDAO = new MatchDAO();
            for (Match m : matches) {
                matchDAO.create(m);
            }
        }

        // Ouvre le bracket dans tous les cas
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tournamentmanager/fxml/bracket.fxml"));
        Parent root = loader.load();
        BracketController controller = loader.getController();
        controller.setTournament(tournament);

        Stage stage = new Stage();
        stage.setTitle("Bracket - " + tournament.getName());
        stage.initModality(Modality.APPLICATION_MODAL);

        Scene scene = new Scene(root);
        App.applyCSS(scene);
        stage.setScene(scene);

        stage.show();
    }
}
