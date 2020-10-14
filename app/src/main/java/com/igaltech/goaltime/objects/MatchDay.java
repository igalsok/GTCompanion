package com.igaltech.goaltime.objects;

import java.math.BigDecimal;
import java.util.Date;


public class MatchDay{
    private Team teamOne;
    private Team teamTwo;
    private Team teamThree;
    private Team teamFour;
    private Date date;
    private boolean finished;


    public MatchDay() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Team getTeamThree() {
        return teamThree;
    }

    public void setTeamThree(Team teamThree) {
        this.teamThree = teamThree;
    }

    public Team getTeamTwo() {
        return teamTwo;
    }

    public void setTeamTwo(Team teamTwo) {
        this.teamTwo = teamTwo;
    }

    public Team getTeamOne() {
        return teamOne;
    }

    public void setTeamOne(Team teamOne) {
        this.teamOne = teamOne;
    }

    public Team getTeamFour() { return teamFour; }
    public void setTeamFour(Team teamFour) {this.teamFour = teamFour; }

    public void teamByIdSet(Team team){
        switch(new BigDecimal(team.getTeamId()).intValueExact()) {
            case 0:
                 setTeamOne(team);
                break;
            case 1:
                setTeamTwo(team);
                break;
            case 2:
                setTeamThree(team);
                break;

        }


    }


}
