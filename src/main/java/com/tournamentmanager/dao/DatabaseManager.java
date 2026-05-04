package com.tournamentmanager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:tournament.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initDatabase() {
        String createPlayers = """
            CREATE TABLE IF NOT EXISTS players (
                id   INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT    NOT NULL CHECK(length(name) BETWEEN 2 AND 50),
                game TEXT    NOT NULL CHECK(length(game) BETWEEN 2 AND 50)
            )
        """;

        String createTournaments = """
            CREATE TABLE IF NOT EXISTS tournaments (
                id     INTEGER PRIMARY KEY AUTOINCREMENT,
                name   TEXT    NOT NULL CHECK(length(name) BETWEEN 2 AND 50),
                game   TEXT    NOT NULL CHECK(length(game) BETWEEN 2 AND 50),
                date   TEXT    NOT NULL CHECK(length(date) = 10),
                status TEXT    NOT NULL CHECK(status IN ('En attente', 'En cours', 'Terminé'))
            )
        """;

        String createMatches = """
            CREATE TABLE IF NOT EXISTS matches (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                tournament_id INTEGER NOT NULL,
                round         INTEGER NOT NULL CHECK(round >= 1),
                player1_id    INTEGER NOT NULL,
                player2_id    INTEGER NOT NULL,
                winner_id     INTEGER,
                CHECK(player1_id != player2_id),
                FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
                FOREIGN KEY (player1_id)    REFERENCES players(id),
                FOREIGN KEY (player2_id)    REFERENCES players(id),
                FOREIGN KEY (winner_id)     REFERENCES players(id)
            )
        """;

        String createTournamentPlayers = """
            CREATE TABLE IF NOT EXISTS tournament_players (
                tournament_id INTEGER NOT NULL,
                player_id INTEGER NOT NULL,
                PRIMARY KEY (tournament_id, player_id),
                FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
                FOREIGN KEY (player_id) REFERENCES players(id)
            )
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createPlayers);
            stmt.execute(createTournaments);
            stmt.execute(createMatches);
            stmt.execute(createTournamentPlayers);
        } catch (SQLException e) {
        }
    }
}