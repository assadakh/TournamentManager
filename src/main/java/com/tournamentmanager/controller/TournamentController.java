package com.tournamentmanager.controller;

import com.tournamentmanager.App;
import com.tournamentmanager.dao.TournamentDAO;
import com.tournamentmanager.model.Tournament;
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
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TournamentController {

    @FXML private TableView<Tournament> tableTournaments;
    @FXML private TableColumn<Tournament, String> colName;
    @FXML private TableColumn<Tournament, String> colGame;
    @FXML private TableColumn<Tournament, String> colDate;
    @FXML private TableColumn<Tournament, String> colStatus;
    @FXML private TableColumn<Tournament, Void> colAction;
    @FXML private TableColumn<Tournament, Void> colGerer;

    @FXML
    public void initialize() {
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        colGame.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getGame()));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnSupprimer.setOnAction(e -> {
                    Tournament t = getTableView().getItems().get(getIndex());
                    new TournamentDAO().delete(t.getId());
                    loadTournaments();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnSupprimer);
            }
        });

        colGerer.setCellFactory(col -> new TableCell<>() {
            private final Button btnGerer = new Button("Gérer");

            {
                btnGerer.setOnAction(e -> {
                    Tournament t = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tournamentmanager/fxml/manage_tournament.fxml"));
                        Parent root = loader.load();
                        ManageTournamentController controller = loader.getController();
                        controller.setTournament(t);

                        Stage stage = new Stage();
                        stage.setTitle("Gérer le tournoi");
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(new Scene(root));
                        stage.showAndWait();

                        loadTournaments();
                    } catch (IOException ex) {
                        System.out.println("Erreur : " + ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnGerer);
            }
        });

        loadTournaments();
    }

    public void loadTournaments() {
        List<Tournament> dao = new TournamentDAO().findAll();
        tableTournaments.getItems().setAll(dao);
    }

    @FXML
    public void handleNouveauTournoi() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tournamentmanager/fxml/new_tournament.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Nouveau tournoi");
        stage.initModality(Modality.APPLICATION_MODAL); // bloque la fenêtre principale
        stage.setScene(new Scene(root));
        stage.showAndWait(); // attend que la popup soit fermée

        loadTournaments(); // rafraîchit la liste après fermeture
    }

    @FXML
    public void handleRetour() throws IOException {
        App.setRoot("fxml/home");
    }
}
