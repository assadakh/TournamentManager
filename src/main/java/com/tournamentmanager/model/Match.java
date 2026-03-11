package com.tournamentmanager.model;

public class Match {

    // Attributes
    private int id;
    private int idTournament;
    private int round;
    private Player player1;
    private Player player2;
    private Player winner;

    // Constructors
    public Match() {}

    public Match(int idTournament, int round, Player player1, Player player2) {
        this.idTournament = idTournament;
        this.round = round;
        this.player1 = player1;
        this.player2 = player2;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getTournamentId() {
        return idTournament;
    }

    public int getRound() {
        return round;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getWinner() {
        return winner;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTournamentId(int tournamentId) {
        this.idTournament = tournamentId;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public boolean isPlayed() {
        return winner != null;
    }
}
