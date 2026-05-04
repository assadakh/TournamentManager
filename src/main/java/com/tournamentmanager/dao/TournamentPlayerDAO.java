package com.tournamentmanager.dao;

import com.tournamentmanager.model.Player;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class TournamentPlayerDAO {

    public boolean isPlayerInTournament(int tournamentId, int playerId) {
        String sql = "SELECT COUNT(*) FROM tournament_players WHERE tournament_id = ? AND player_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ps.setInt(2, playerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
            return false;
        }
    }

    public void removePlayer(int tournamentId, int playerId) {
        String sql = "DELETE FROM tournament_players WHERE tournament_id = ? AND player_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ps.setInt(2, playerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void addPlayer(int tournamentId, int playerId) {
        String sql = "INSERT INTO tournament_players (tournament_id, player_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ps.setInt(2, playerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public List<Player> getPlayers(int tournamentId) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.* FROM players p JOIN tournament_players tp ON p.id = tp.player_id WHERE tp.tournament_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Player newPlayer = new Player(rs.getInt("id"), rs.getString("name"), rs.getString("game"));
                players.add(newPlayer);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        return players;
    }
}
