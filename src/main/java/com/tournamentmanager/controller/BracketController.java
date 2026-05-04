package com.tournamentmanager.controller;

import com.tournamentmanager.App;
import com.tournamentmanager.dao.MatchDAO;
import com.tournamentmanager.model.Match;
import com.tournamentmanager.model.Player;
import com.tournamentmanager.model.Tournament;
import com.tournamentmanager.util.PdfExporter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BracketController {

    @FXML private ScrollPane scrollPane;
    @FXML private Pane bracketPane;

    private Tournament tournament;
    private List<Match> currentMatches;

    private static final double CARD_W  = 200;
    private static final double CARD_H  = 100;
    private static final double V_GAP   = 30;
    private static final double H_GAP   = 80;
    private static final double LEFT_PAD = 30;
    private static final double TOP_PAD  = 50;

    public void setTournament(Tournament t) {
        this.tournament = t;
        loadMatches();
    }

    public void loadMatches() {
        currentMatches = new MatchDAO().findByTournaments(tournament.getId());
        buildBracketView(currentMatches);
    }

    private void buildBracketView(List<Match> matches) {
        bracketPane.getChildren().clear();

        TreeMap<Integer, List<Match>> byRound = matches.stream()
                .sorted(Comparator.comparingInt(Match::getId))
                .collect(Collectors.groupingBy(Match::getRound, TreeMap::new, Collectors.toList()));

        if (byRound.isEmpty()) {
            Label empty = new Label("Aucun match généré.");
            empty.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            empty.setLayoutX(LEFT_PAD);
            empty.setLayoutY(TOP_PAD);
            bracketPane.getChildren().add(empty);
            return;
        }

        int round1Count = byRound.get(1).size();
        // Total rounds = log2(round1Count) + 1  (e.g. 4 matches R1 → 3 rounds)
        int totalRounds = (int)(Math.log(round1Count) / Math.log(2)) + 1;

        // Compute Y center of each card per round (all rounds, even future ones)
        Map<Integer, double[]> centerYByRound = new TreeMap<>();

        double[] r1Centers = new double[round1Count];
        for (int i = 0; i < round1Count; i++) {
            r1Centers[i] = TOP_PAD + i * (CARD_H + V_GAP) + CARD_H / 2.0;
        }
        centerYByRound.put(1, r1Centers);

        for (int r = 2; r <= totalRounds; r++) {
            double[] prev = centerYByRound.get(r - 1);
            int count     = prev.length / 2;
            double[] curr = new double[count];
            for (int i = 0; i < count; i++) {
                curr[i] = (prev[2 * i] + prev[2 * i + 1]) / 2.0;
            }
            centerYByRound.put(r, curr);
        }

        // Draw connector lines (behind cards) for all rounds
        for (int r = 1; r < totalRounds; r++) {
            double[] curr = centerYByRound.get(r);
            double[] next = centerYByRound.get(r + 1);

            double xRight = LEFT_PAD + (r - 1) * (CARD_W + H_GAP) + CARD_W;
            double xLeft  = LEFT_PAD + r * (CARD_W + H_GAP);
            double midX   = xRight + H_GAP / 2.0;

            for (int i = 0; i < next.length; i++) {
                double topY    = curr[2 * i];
                double bottomY = curr[2 * i + 1];
                double parentY = next[i];

                bracketPane.getChildren().addAll(
                        line(xRight, topY,    midX,  topY),
                        line(xRight, bottomY, midX,  bottomY),
                        line(midX,   topY,    midX,  bottomY),
                        line(midX,   parentY, xLeft, parentY)
                );
            }
        }

        // Draw round labels and match cards (real + placeholder for future rounds)
        for (int round = 1; round <= totalRounds; round++) {
            List<Match> rMatches = byRound.getOrDefault(round, Collections.emptyList());
            double[] centers     = centerYByRound.get(round);
            double cardX         = LEFT_PAD + (round - 1) * (CARD_W + H_GAP);
            int expectedCount    = round1Count / (int) Math.pow(2, round - 1);

            boolean isFinal   = (round == totalRounds);
            String roundLabel = isFinal ? "Finale" : "Round " + round;

            Label lbl = new Label(roundLabel);
            lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
            lbl.setLayoutX(cardX + CARD_W / 2.0 - 25);
            lbl.setLayoutY(Math.max(8, centers[0] - CARD_H / 2.0 - 22));
            bracketPane.getChildren().add(lbl);

            for (int i = 0; i < expectedCount; i++) {
                VBox card = (i < rMatches.size())
                        ? buildMatchCard(rMatches.get(i))
                        : buildPlaceholderCard();
                card.setLayoutX(cardX);
                card.setLayoutY(centers[i] - CARD_H / 2.0);
                bracketPane.getChildren().add(card);
            }
        }

        double totalW = LEFT_PAD * 2 + totalRounds * (CARD_W + H_GAP);
        double totalH = TOP_PAD * 2 + round1Count * (CARD_H + V_GAP);
        bracketPane.setPrefSize(totalW, totalH);
    }

    private VBox buildMatchCard(Match match) {
        VBox card = new VBox();
        card.setPrefWidth(CARD_W);
        card.setPrefHeight(CARD_H);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #dde1e7;" +
                "-fx-border-radius: 8;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);"
        );

        Player winner = match.getWinner();
        boolean p1Wins = winner != null && match.getPlayer1().getId() == winner.getId();
        boolean p2Wins = winner != null && match.getPlayer2().getId() == winner.getId();

        card.getChildren().addAll(
                playerRow(match.getPlayer1(), p1Wins),
                hDivider(),
                playerRow(match.getPlayer2(), p2Wins),
                hDivider(),
                actionRow(match)
        );

        return card;
    }

    private VBox buildPlaceholderCard() {
        VBox card = new VBox();
        card.setPrefWidth(CARD_W);
        card.setPrefHeight(CARD_H);
        card.setStyle(
                "-fx-background-color: #f8f9fa;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #dde1e7;" +
                "-fx-border-radius: 8;"
        );

        Label top = new Label("À déterminer");
        top.setStyle("-fx-font-size: 11px; -fx-text-fill: #bdc3c7; -fx-padding: 0 8;");
        VBox topRow = new VBox(top);
        topRow.setPrefHeight(34);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Rectangle div = new Rectangle(CARD_W, 1);
        div.setFill(Color.web("#eaecef"));

        Label bottom = new Label("À déterminer");
        bottom.setStyle("-fx-font-size: 11px; -fx-text-fill: #bdc3c7; -fx-padding: 0 8;");
        VBox bottomRow = new VBox(bottom);
        bottomRow.setPrefHeight(34);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Rectangle div2 = new Rectangle(CARD_W, 1);
        div2.setFill(Color.web("#eaecef"));

        HBox action = new HBox();
        action.setPrefHeight(29);

        card.getChildren().addAll(topRow, div, bottomRow, div2, action);
        return card;
    }

    private VBox playerRow(Player player, boolean isWinner) {
        Label name = new Label((isWinner ? "🏆 " : "     ") + player.getName());
        name.setMaxWidth(CARD_W - 16);
        name.setStyle("-fx-font-size: 12px;" +
                (isWinner
                        ? "-fx-font-weight: bold; -fx-text-fill: #27ae60;"
                        : "-fx-text-fill: #2c3e50;"));
        VBox row = new VBox(name);
        row.setPrefHeight(34);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 0 8 0 8;" +
                (isWinner ? "-fx-background-color: #eafaf1;" : ""));
        return row;
    }

    private Rectangle hDivider() {
        Rectangle r = new Rectangle(CARD_W, 1);
        r.setFill(Color.web("#eaecef"));
        return r;
    }

    private HBox actionRow(Match match) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        row.setPrefHeight(29);

        if (!match.isPlayed()) {
            Button btn = new Button("Définir gagnant");
            btn.setStyle("-fx-font-size: 10px; -fx-padding: 3 10; -fx-background-radius: 4;");
            btn.setOnAction(e -> openChooseWinner(match));
            row.getChildren().add(btn);
        }

        return row;
    }

    private Line line(double x1, double y1, double x2, double y2) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(Color.web("#bdc3c7"));
        l.setStrokeWidth(2);
        return l;
    }

    private void openChooseWinner(Match match) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/tournamentmanager/fxml/choose_winner.fxml"));
            Parent root = loader.load();

            ChooseWinnerController controller = loader.getController();
            controller.setMatch(match);
            controller.setTournament(tournament);

            Stage stage = new Stage();
            stage.setTitle("Choisir le gagnant");
            stage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(root);
            App.applyCSS(scene);
            stage.setScene(scene);
            stage.showAndWait();

            loadMatches();
        } catch (IOException ex) {
            showError("Une erreur est survenue. Veuillez réessayer.");
        }
    }

    @FXML
    public void handleExportPdf() {
        PdfExporter.export(currentMatches, tournament);
    }

    @FXML
    public void handleRetour() {
        Stage stage = (Stage) bracketPane.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
