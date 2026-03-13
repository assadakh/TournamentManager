package com.tournamentmanager.util;

import com.tournamentmanager.model.Match;
import com.tournamentmanager.model.Player;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class BracketGenerator {

    public static List<Match> generateFirstRound(List<Player> players, int tournamentId) {
        List<Match> matches = new ArrayList<>();
        Collections.shuffle(players);
        for (int i = 0; i < players.size() - 1; i += 2) {
            Player p1 = players.get(i);
            Player p2 = players.get(i + 1);
            Match newMatch = new Match(tournamentId, 1, p1, p2);
            matches.add(newMatch);
        }
        return matches;
    }

    public static List<Match> generateNextRound(List<Match> previousRound, int tournamentId) {
        List<Match> matches = new ArrayList<>();
        int round = previousRound.getFirst().getRound() + 1;
        for (int i = 0; i < previousRound.size() - 1; i += 2) {
            Player p1 = previousRound.get(i).getWinner();
            Player p2 = previousRound.get(i + 1).getWinner();
            Match newMatch = new Match(tournamentId, round, p1, p2);
            matches.add(newMatch);
        }
        return matches;
    }
}
