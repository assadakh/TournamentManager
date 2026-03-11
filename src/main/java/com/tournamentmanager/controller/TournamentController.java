package com.tournamentmanager.controller;

import com.tournamentmanager.dao.TournamentDAO;
import com.tournamentmanager.model.Tournament;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TournamentController {

    @FXML private TableView<Tournament> tableTournaments;
    @FXML private TableColumn<Tournament, String> colName;
    @FXML private TableColumn<Tournament, String> colGame;
    @FXML private TableColumn<Tournament, String> colDate;
    @FXML private TableColumn<Tournament, String> colStatus;

    public void initialize(URL url, ResourceBundle rb) {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGame.setCellValueFactory(new PropertyValueFactory<>("game"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadTournaments();
    }

    public void loadTournaments() {
        List<Tournament> dao = new TournamentDAO().findAll();
        tableTournaments.getItems().setAll(dao);
    }

    @FXML
    public void handleNouveauTournoi() {
        System.out.println("Nouveau tournoi !");
    }
}
