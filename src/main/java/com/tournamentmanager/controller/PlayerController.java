package com.tournamentmanager.controller;

import com.tournamentmanager.App;
import com.tournamentmanager.dao.PlayerDAO;
import com.tournamentmanager.model.Player;
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

public class PlayerController {

    @FXML private TableView<Player> tablePlayers;
    @FXML private TableColumn<Player, String> colName;
    @FXML private TableColumn<Player, String> colGame;
    @FXML private TableColumn<Player, Void> colAction;

    @FXML
    public void initialize() {
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colGame.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGame()));
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnSupprimer.setOnAction(e -> {
                    Player p = getTableView().getItems().get(getIndex());
                    new PlayerDAO().delete(p.getId());
                    loadPlayers();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnSupprimer);
            }
        });

        loadPlayers();
    }

    public void loadPlayers() {
        List<Player> dao = new PlayerDAO().findAll();
        tablePlayers.getItems().setAll(dao);
    }

    @FXML
    public void handleNouveauJoueur() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tournamentmanager/fxml/new_player.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Nouveau joueur");
        stage.initModality(Modality.APPLICATION_MODAL); // bloque la fenêtre principale
        stage.setScene(new Scene(root));
        stage.showAndWait(); // attend que la popup soit fermée

        loadPlayers(); // rafraîchit la liste après fermeture
    }

    @FXML
    public void handleRetour() throws IOException {
        App.setRoot("fxml/home");
    }
}
