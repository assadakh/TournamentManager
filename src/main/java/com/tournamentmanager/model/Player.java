package com.tournamentmanager.model;

public class Player {

    // Attributes
    private int id;
    private String name;
    private String game;

    // Constructors
    public Player() {}

    public Player(int id, String name, String game) {
        this.id = id;
        this.name = name;
        this.game = game;
    }

    public Player(String name, String game) {
        this.name = name;
        this.game = game;
    }

    // Getters
    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getGame() {
        return this.game;
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

    @Override
    public String toString() {
        return name;
    }
}
