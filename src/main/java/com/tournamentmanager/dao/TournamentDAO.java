package com.tournamentmanager.dao;

import com.tournamentmanager.model.Tournament;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class TournamentDAO {

    public void create(Tournament t) {
        String sql = "INSERT INTO tournaments (name, game, date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getGame());
            ps.setString(3, t.getDate());
            ps.setString(4, t.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public List<Tournament> findAll() {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tournament newTournament = new Tournament(rs.getInt("id"), rs.getString("name"), rs.getString("game"), rs.getString("date"), rs.getString("status"));
                tournaments.add(newTournament);
            }
        } catch (SQLException e) {
        }

        return tournaments;
    }

    public void update(Tournament t) {
        String sql = "UPDATE tournaments SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getStatus());
            ps.setInt(2, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM tournaments WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }
}
