package com.igaltech.goaltime;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DBUpdate {

    final static private DBUpdate endGameUpdate = new DBUpdate();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private DBUpdate(){
    }

    public void updatePlayers(ArrayList<Player> players){
        for(Player player : players){
            db.collection("players").document(player.getName())
                    .update("goals",player.getGoals(),"assists",player.getAssists(),
                            "games",player.getGames(),
                            "wins",player.getWins(),
                            "ties",player.getTies()
                    ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   e.printStackTrace();
                }
            });
        }
    }

    public void getAllPlayers(final CallBack callback){
       final ArrayList<Player> players = new ArrayList<>();
      db.collection("players").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
              if (task.isSuccessful()) {
                  List<DocumentSnapshot> listOfDocuments = task.getResult().getDocuments();
                  for(DocumentSnapshot document : listOfDocuments){
                      Player player = document.toObject(Player.class);
                      players.add(player);
                  }
                  Collections.sort(players);
                  callback.callBack(players);
              }
          }
      });
    }

    public void createTeams(final CallBack<Team> callBack){
        Team one = new Team();
        Team two = new Team();
        Team three = new Team();
        Query query = db.collection("match_days")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        boolean finished = (boolean)document.get("finished");
                        if(!finished){
                          ArrayList<Team> teams = new ArrayList<>();
                            teams.add( mapToTeam((HashMap<String,Object>)document.get("teamOne")));
                            teams.add( mapToTeam((HashMap<String,Object>)document.get("teamTwo")));
                            teams.add( mapToTeam((HashMap<String,Object>)document.get("teamThree")));
                          callBack.callBack(teams);
                        }
                        else{
                            callBack.callBack(null);
                        }
                    }

                }
            }
        });

    }

    private Team mapToTeam(HashMap<String,Object> map){
        ArrayList<Player> players = new ArrayList<>();
        Team team = new Team();
      ArrayList<HashMap<String,Object>> array = (  ArrayList<HashMap<String,Object>>)map.get("players");
       HashMap<String,Object> map3 = new HashMap<String,Object>();
       for(int i=0;i<5;++i){
           map3 = array.get(i);
           Player player = new Player();
           player.setTies((long)map3.get("ties"));
           player.setWins((long)map3.get("wins"));
           player.setGames((long)map3.get("games"));
           player.setGoals((long)map3.get("goals"));
           player.setAssists((long)map3.get("assists"));
           player.setName((String)map3.get("name"));
           players.add(player);
       }
       team.setTeamId((long)map.get("teamId"));
       team.setWins((long)map.get("wins"));
        team.setGames((long)map.get("games"));
        team.setTies((long)map.get("ties"));
        team.setPlayers(players);
        return team;
    }


    public MatchDay createNewMatchDay(ArrayList<Team> teams){
        MatchDay matchDay = new MatchDay();
        matchDay.setTeamOne(teams.get(0));
        matchDay.setTeamTwo(teams.get(1));
        matchDay.setTeamThree(teams.get(2));
        Date date = new Date();
        matchDay.setDate(date);
        matchDay.setFinished(false);
        String docId =matchDay.getDate().toString();
        db.collection("match_days").document(docId).set(matchDay);
        return matchDay;
    }

    public void updateMatchDay(final MatchDay matchDay){
      db.collection("match_days").document(matchDay.getDate().toString()).update("teamOne",generateTeamMap(matchDay.getTeamOne()),"teamTwo",generateTeamMap(matchDay.getTeamTwo()),"teamThree",generateTeamMap(matchDay.getTeamThree()));
    }
    public void endMatchDay(final MatchDay matchDay){
        db.collection("match_days").document(matchDay.getDate().toString()).update("finished",true);
    }

    public HashMap<String,Object> generateTeamMap(Team team){
        HashMap<String,Object> map = new HashMap<>();
        ArrayList<Player> playerArray = team.getPlayers();
        ArrayList<HashMap<String,Object> > playerIntegerArray = new ArrayList<>();
        for(int i=0;i<playerArray.size();++i){
            playerIntegerArray.add(playerToMap(playerArray.get(i)));
        }
        map.put("players",playerIntegerArray);
        map.put("games",team.getGames());
        map.put("teamId",team.getTeamId());
        map.put("wins",team.getWins());
        map.put("ties",team.getTies());
        return map;
    }

    public HashMap<String,Object> playerToMap(Player player){
        HashMap<String,Object> playerMap = new HashMap<>();
        playerMap.put("assists",player.getAssists());
        playerMap.put("games",player.getGames());
        playerMap.put("wins",player.getWins());
        playerMap.put("ties",player.getTies());
        playerMap.put("goals",player.getGoals());
        playerMap.put("name",player.getName());
        return playerMap;
    }



    public void getLastMatchDay(final CallBack<MatchDay> callBack){
        Query query = db.collection("match_days")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        MatchDay matchDay = (MatchDay)document.toObject(MatchDay.class);
                        ArrayList<MatchDay> array = new ArrayList<>();
                        array.add(matchDay);
                            callBack.callBack(array);
                    }

                }
            }
        });
    }

    public void test(){
        Team team = new Team();
        Player player1 = new Player("igal");
        Player player2 = new Player("igal");
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        team.setPlayers(players);
        db.collection("test").add(team);
    }


    static public DBUpdate getInstance(){
        return endGameUpdate;
    }





}
