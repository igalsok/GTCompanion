package com.igaltech.goaltime;

import java.io.Serializable;
import java.util.ArrayList;

public class Team implements Serializable {
    private long teamId;
    private ArrayList<Player> players;
    private long wins;
    private long ties;
    private long games;

    public Team(){

    }

    public Team(ArrayList<Player> players, int teamId){
        this.players = players;
        this.wins = 0;
        this.ties = 0;
        this.games = 0;
        this.teamId = teamId;
    }
    public Team(ArrayList<Player> players, int teamId, int wins, int ties,int games){
        this.players = players;
        this.wins = wins;
        this.ties = ties;
        this.games = games;
        this.teamId = teamId;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public long getWins() {
        return wins;
    }

    public void setWins(long wins) {
        this.wins = wins;
    }

    public long getTies() {
        return ties;
    }

    public void setTies(long ties) {
        this.ties = ties;
    }

    public long getGames() {
        return games;
    }

    public void setGames(long games) {
        this.games = games;
    }

    public ArrayList<String> playerNamesGet(){
        ArrayList<String> names = new ArrayList<>();
        for(Player player : players){
            names.add(player.getName());
        }
        return names;
    }

    public Player getPlayer(String name){
        for(Player player : players){
            if(player.getName().equals(name)){
                return player;
            }
        }
        return null;
    }

    public void increaseWins(){
        wins += 1;
        updatePlayerWins();
    }
    private void updatePlayerWins(){
        for(Player player : players){
            player.increaseWins();
        }
    }

    public void increaseTies(){
        ties += 1;
        updatePlayerTies();
    }
    private void updatePlayerTies(){
        for(Player player : players){
            player.increaseTies();
        }
    }

    public void increaseGames(){
        games += 1;
        updatePlayerGames();
    }
    private void updatePlayerGames(){
        for(Player player : players){
            player.increaseGames();
        }
    }


    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }
}
