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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ManageTournamentController {

    @FXML private ListView<Player> listDisponibles;
    @FXML private ListView<Player> listInscrits;
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
        listDisponibles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listInscrits.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        loadJoueurs();
    }

    public void loadJoueurs() {
        List<Player> inscrits = new TournamentPlayerDAO().getPlayers(tournament.getId());
        List<Player> tous = new PlayerDAO().findAll();
        List<Player> disponibles = tous.stream()
                .filter(p -> inscrits.stream().noneMatch(i -> i.getId() == p.getId()))
                .collect(Collectors.toList());

        listInscrits.getItems().setAll(inscrits);
        listDisponibles.getItems().setAll(disponibles);
    }

    @FXML
    public void handleAjouterJoueur() {
        List<Player> selection = List.copyOf(listDisponibles.getSelectionModel().getSelectedItems());
        labelError.setText("");
        if (selection.isEmpty()) return;
        TournamentPlayerDAO dao = new TournamentPlayerDAO();
        for (Player joueur : selection) {
            dao.addPlayer(tournament.getId(), joueur.getId());
        }
        loadJoueurs();
    }

    @FXML
    public void handleRetirerJoueur() {
        List<Player> selection = List.copyOf(listInscrits.getSelectionModel().getSelectedItems());
        labelError.setText("");
        if (selection.isEmpty()) return;
        TournamentPlayerDAO dao = new TournamentPlayerDAO();
        for (Player joueur : selection) {
            dao.removePlayer(tournament.getId(), joueur.getId());
        }
        loadJoueurs();
    }

    @FXML
    public void handleGenererBracket() throws IOException {
        List<Match> existingMatches = new MatchDAO().findByTournaments(tournament.getId());

        if (existingMatches.isEmpty()) {
            List<Player> players = new TournamentPlayerDAO().getPlayers(tournament.getId());
            if (players.size() < 2) {
                labelError.setText("Il faut au moins 2 joueurs pour générer un bracket.");
                return;
            }
            List<Match> matches = BracketGenerator.generateFirstRound(players, tournament.getId());
            MatchDAO matchDAO = new MatchDAO();
            for (Match m : matches) {
                matchDAO.create(m);
            }
        }

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
