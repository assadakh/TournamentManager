package com.tournamentmanager.model;

import java.util.List;
import java.util.ArrayList;

public class Tournament {

    // Attributes
    private int id;
    private String name;
    private String game;
    private String date;
    private String status;
    private List<Player> players;

    //Constructors
    public Tournament() {
        this.players = new ArrayList<>();
    }

    public Tournament(int id, String name, String game, String date, String status) {
        this.id = id;
        this.name = name;
        this.game = game;
        this.date = date;
        this.status = status;
        this.players = new ArrayList<>();
    }

    public Tournament(String name, String game, String date) {
        this.name = name;
        this.game = game;
        this.date = date;
        this.status = "En cours";
        this.players = new ArrayList<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGame() {
        return game;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public List<Player> getPlayers() {
        return players;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return name + " (" + game + ")";
    }
}
