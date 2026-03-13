package com.tournamentmanager.dao;

import com.tournamentmanager.model.Match;
import com.tournamentmanager.model.Player;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class MatchDAO {
    public void create(Match m) {
        String sql = "INSERT INTO matches (tournament_id, round, player1_id, player2_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getTournamentId());
            ps.setInt(2, m.getRound());
            ps.setInt(3, m.getPlayer1().getId());
            ps.setInt(4, m.getPlayer2().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public List<Match> findByTournaments(int tournamentId) {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT m.*, \n" +
                "       p1.name as player1_name, \n" +
                "       p2.name as player2_name\n" +
                "FROM matches m\n" +
                "JOIN players p1 ON m.player1_id = p1.id\n" +
                "JOIN players p2 ON m.player2_id = p2.id\n" +
                "WHERE m.tournament_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId); // ← on passe le paramètre ici
            ResultSet rs = ps.executeQuery(); // ← PUIS on exécute
            while (rs.next()) {
                Player player1 = new Player(rs.getInt("player1_id"), rs.getString("player1_name"), "");
                Player player2 = new Player(rs.getInt("player2_id"), rs.getString("player2_name"), "");
                Match newMatch = new Match(rs.getInt("tournament_id"), rs.getInt("round"), player1, player2);
                matches.add(newMatch);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        return matches;
    }

    public void updateWinner(Match m) {
        String sql = "UPDATE matches SET winner_id = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getWinner().getId());
            ps.setInt(2, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM matches WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void deleteByTournament(int tournamentId) {
        String sql = "DELETE FROM matches WHERE tournament_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
