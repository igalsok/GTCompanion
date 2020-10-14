package com.igaltech.goaltime.objects;

import java.io.Serializable;

public class Player implements Serializable, Comparable {
    private String name;
    private long assists;
    private long goals;
    private long wins;
    private long ties;
    private long games;


    public Player(){

    }
    public Player(String name){
        this.name = name;
        assists = 0;
        goals = 0;
        wins = 0;
        games = 0;
        ties = 0;
    }

    public long getWins() {
        return wins;
    }

    public void setWins(long wins) {
        this.wins = wins;
    }

    public long getGoals() {
        return goals;
    }

    public void setGoals(long goals) {
        this.goals = goals;
    }

    public long getAssists() {
        return assists;
    }

    public void setAssists(long assists){
        this.assists = assists;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getGames(){
        return games;
    }
    public void setGames(long games){
        this.games = games;
    }

    public void setTies(long ties) {
        this.ties = ties;
    }
    public long getTies(){
        return ties;
    }
    public int calculateRating(){
        float calc = 50;
        if(games!=0) {
            calc = 65 +  ((float)goals / (float)(games * 2)) * 30 + ((float) assists / (float) (games * 2)) * 10 +  ((float)wins / (float)games) * 15 +  ((float)ties / (float)games) * 5;
        }
        return (int)calc;
    }

    public void increaseGoals(){
        goals += 1;
    }
    public void increaseAssists(){
        assists += 1;
    }
    public void increaseWins(){
        wins += 1;
    }
    public void increaseTies(){
        ties += 1;
    }
    public void increaseGames(){
        games += 1;
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Object o) {
        int compareRating = ((Player)o).calculateRating();
        return  compareRating-calculateRating();
    }
}
