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
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                game TEXT NOT NULL
            )
        """;

        String createTournaments = """
            CREATE TABLE IF NOT EXISTS tournaments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                game TEXT NOT NULL,
                date TEXT NOT NULL,
                status TEXT NOT NULL
            )
        """;

        String createMatches = """
            CREATE TABLE IF NOT EXISTS matches (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tournament_id INTEGER NOT NULL,
                round INTEGER NOT NULL,
                player1_id INTEGER NOT NULL,
                player2_id INTEGER NOT NULL,
                winner_id INTEGER,
                FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
                FOREIGN KEY (player1_id) REFERENCES players(id),
                FOREIGN KEY (player2_id) REFERENCES players(id),
                FOREIGN KEY (winner_id) REFERENCES players(id)
            )
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createPlayers);
            stmt.execute(createTournaments);
            stmt.execute(createMatches);
            System.out.println("Base de données initialisée.");
        } catch (SQLException e) {
            System.out.println("Erreur init BDD : " + e.getMessage());
        }
    }
}