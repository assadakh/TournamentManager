package com.tournamentmanager.controller;

import com.tournamentmanager.App;
import com.tournamentmanager.dao.MatchDAO;
import com.tournamentmanager.model.Match;
import com.tournamentmanager.model.Player;
import com.tournamentmanager.model.Tournament;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class BracketController {

    @FXML private TableView<Match> tableBracket;
    @FXML private TableColumn<Match, Integer> colRound;
    @FXML private TableColumn<Match, String> colPlayer1;
    @FXML private TableColumn<Match, String> colPlayer2;
    @FXML private TableColumn<Match, String> colWinner;
    @FXML private TableColumn<Match, Void> colAction;
    private Tournament tournament;

    public void initialize() {
        colRound.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRound()).asObject());
        colPlayer1.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPlayer1().getName()));
        colPlayer2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPlayer2().getName()));
        colWinner.setCellValueFactory(data -> {
            Player winner = data.getValue().getWinner();
            return new SimpleStringProperty(winner != null ? winner.getName() : "À jouer");
        });

        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnDefWinner = new Button("Définir gagnant");

            {
                btnDefWinner.setOnAction(e -> {
                    Match m = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tournamentmanager/fxml/choose_winner.fxml"));
                        Parent root = loader.load();
                        ChooseWinnerController controller = loader.getController();
                        controller.setMatch(m);

                        Stage stage = new Stage();
                        stage.setTitle("Choisir le gagnant");
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(new Scene(root));
                        stage.showAndWait();

                        loadMatches(); // rafraîchit le bracket
                    } catch (IOException ex) {
                        System.out.println("Erreur : " + ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDefWinner);
            }
        });
    }

    public void setTournament(Tournament t) {
        this.tournament = t;
        loadMatches();
    }

    public void loadMatches() {
        List<Match> matches = new MatchDAO().findByTournaments(tournament.getId());
        tableBracket.getItems().setAll(matches);
    }

    @FXML
    public void handleRetour() throws IOException {
        Stage stage = (Stage) tableBracket.getScene().getWindow();
        stage.close();
    }
}
