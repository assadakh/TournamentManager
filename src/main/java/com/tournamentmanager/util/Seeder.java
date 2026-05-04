package com.tournamentmanager.util;

import com.tournamentmanager.dao.DatabaseManager;
import java.sql.*;

public class Seeder {

    public static void seedIfEmpty() {
        if (!isEmpty()) return;
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            int[] playerIds = insertPlayers(conn);
            int[] tournamentIds = insertTournaments(conn);
            linkPlayers(conn, playerIds, tournamentIds);
            conn.commit();
        } catch (SQLException e) {
        }
    }

    private static boolean isEmpty() {
        String sql = "SELECT COUNT(*) FROM players";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private static int[] insertPlayers(Connection conn) throws SQLException {
        String[][] data = {
            {"Lucas Martin",  "FIFA 25"},
            {"Theo Bernard",  "FIFA 25"},
            {"Emma Dupont",   "FIFA 25"},
            {"Noah Simon",    "FIFA 25"},
            {"Jade Moreau",   "League of Legends"},
            {"Tom Petit",     "League of Legends"},
            {"Lea Garnier",   "League of Legends"},
            {"Hugo Leroy",    "League of Legends"},
            {"Camille Roy",   "Valorant"},
            {"Antoine Blond", "Valorant"},
        };
        return insert(conn, "INSERT INTO players (name, game) VALUES (?, ?)", data);
    }

    private static int[] insertTournaments(Connection conn) throws SQLException {
        String[][] data = {
            {"Open FIFA 2026",      "FIFA 25",           "15/06/2026", "En attente"},
            {"Championnat LoL",     "League of Legends", "20/06/2026", "En attente"},
            {"Tournoi Valorant",    "Valorant",          "25/06/2026", "En attente"},
        };
        return insert(conn, "INSERT INTO tournaments (name, game, date, status) VALUES (?, ?, ?, ?)", data);
    }

    private static void linkPlayers(Connection conn, int[] pIds, int[] tIds) throws SQLException {
        String sql = "INSERT INTO tournament_players (tournament_id, player_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Tournoi FIFA -> 4 joueurs FIFA (ids 0-3)
            for (int i = 0; i < 4; i++) addLink(ps, tIds[0], pIds[i]);
            // Tournoi LoL  -> 4 joueurs LoL  (ids 4-7)
            for (int i = 4; i < 8; i++) addLink(ps, tIds[1], pIds[i]);
            // Tournoi Valorant -> 2 joueurs Valorant (ids 8-9)
            for (int i = 8; i < 10; i++) addLink(ps, tIds[2], pIds[i]);
            ps.executeBatch();
        }
    }

    private static void addLink(PreparedStatement ps, int tournamentId, int playerId) throws SQLException {
        ps.setInt(1, tournamentId);
        ps.setInt(2, playerId);
        ps.addBatch();
    }

    private static int[] insert(Connection conn, String sql, String[][] rows) throws SQLException {
        int[] ids = new int[rows.length];
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < rows.length; i++) {
                for (int j = 0; j < rows[i].length; j++) ps.setString(j + 1, rows[i][j]);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) ids[i] = rs.getInt(1);
            }
        }
        return ids;
    }
}
