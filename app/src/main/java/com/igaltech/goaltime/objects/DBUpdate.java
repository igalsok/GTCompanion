package com.igaltech.goaltime.objects;

import android.service.autofill.FieldClassification;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void createTeams(String teamId, final CallBack<Team> callBack){
        Query query = db.collection("teams").document(teamId).collection("match_days")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1);
                   query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean isSucceful = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        boolean finished = (boolean)document.get("finished");
                        if(!finished){
                          ArrayList<Team> teams = new ArrayList<>();
                            teams.add( mapToTeam((HashMap<String,Object>)document.get("teamOne")));
                            teams.add( mapToTeam((HashMap<String,Object>)document.get("teamTwo")));
                            teams.add( mapToTeam((HashMap<String,Object>)document.get("teamThree")));
                            if(document.get("teamFour")!=null)
                                teams.add( mapToTeam((HashMap<String,Object>)document.get("teamFour")));
                          callBack.callBack(teams);
                            isSucceful = true;
                        }
                        else{
                            callBack.callBack(null);
                            isSucceful = true;
                        }
                    }
                    if(!isSucceful){
                        callBack.callBack(null);
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


    public MatchDay createNewMatchDay(ArrayList<Team> teams, String teamId){
        MatchDay matchDay = new MatchDay();
        matchDay.setTeamOne(teams.get(0));
        matchDay.setTeamTwo(teams.get(1));
        matchDay.setTeamThree(teams.get(2));
        if(teams.size()> 3)
            matchDay.setTeamFour(teams.get(3));
        Date date = new Date();
        matchDay.setDate(date);
        matchDay.setFinished(false);
        String docId =matchDay.getDate().toString();
        db.collection("teams").document(teamId).collection("match_days").document(docId).set(matchDay);
        return matchDay;
    }

    public void updateMatchDay(final MatchDay matchDay){
      db.collection("match_days").document(matchDay.getDate().toString()).update("teamOne",generateTeamMap(matchDay.getTeamOne()),"teamTwo",generateTeamMap(matchDay.getTeamTwo()),"teamThree",generateTeamMap(matchDay.getTeamThree()));
    }
    public void endMatchDay(final MatchDay matchDay, String teamId){
        db.collection("teams").document(teamId).collection("match_days").document(matchDay.getDate().toString()).update("finished",true);
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



    public void getLastMatchDay(String teamId, final CallBack<MatchDay> callBack){
        Query query = db.collection("teams").document(teamId).collection("match_days")
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

    public void register(String email,String username){
        Map<String, Object> data = new HashMap<>();
        data.put("teamId", null);
        data.put("isAdmin", false);
        data.put("username", username);
        db.collection("users").document(email).set(data);
    }

    public void login(String username,final CallBack<String> call){
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final String teamId = (String)document.get("teamId");
                        final Boolean isAdmin = (Boolean)document.get("isAdmin");
                        if(teamId!=null) {
                            db.collection("teams").document(teamId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()) {
                                            String teamName = (String) document.get("teamName");
                                            ArrayList<String> strings = new ArrayList<>();
                                            strings.add(teamId);
                                            strings.add(teamName);
                                            if (isAdmin) {
                                                strings.add("admin");
                                            }
                                            call.callBack(strings);
                                        }
                                    }
                                }
                            });

                        }
                        else
                        {
                            call.callBack(null);
                        }
                    }
                }
            }
        });
    }

    public void getUserTeam(String username, final CallBack call){
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        if(document.get("teamId")!=null) {
                            String teamId = (String) document.get("teamId");
                            ArrayList<String> strings = new ArrayList<>();
                            strings.add(teamId);
                            call.callBack(strings);
                        }
                        else{
                            call.callBack(null);
                        }

                    }

                    }
                }
        });
    }

    public void joinTeam(final String teamId, final String username, final CallBack<String> call){
        db.collection("teams").document(teamId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String teamName = (String)document.get("teamName");
                        db.collection("users").document(username).update("teamId",teamId);
                        ArrayList<String> strings = new ArrayList<>();
                        strings.add(teamId);
                        strings.add(teamName);
                        call.callBack(strings);
                    } else {
                        call.callBack(null);
                    }
                }
            }
        });

    }

    public void createTeam(final String teamName, final String username, final CallBack<String> call){
        final String teamId = teamIdGenerator();
        HashMap<String,Object> data = new HashMap<>();
        data.put("teamName",teamName);
        db.collection("teams").document(teamId).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                        db.collection("teams").document(teamId).update("teamName",teamName).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                MatchDay matchDay = new MatchDay();
                                matchDay.setFinished(true);
                                db.collection("users").document(username).update("isAdmin", true);
                                db.collection("users").document(username).update("teamId",teamId);
                                ArrayList<String> strings = new ArrayList<>();
                                strings.add(teamId);
                                strings.add(teamName);
                                strings.add("admin");
                                call.callBack(strings);
                            }
                        });
                }
            }
        });

    }

    private String teamIdGenerator() {
        char[] corpus = "ABCDEFGHIJKLMNPQRSTUVWXYZ0123456789".toCharArray();
        int generated = 0;
        int desired = 6;
        char[] result = new char[desired];

        while (generated < desired) {
            byte[] ran = SecureRandom.getSeed(desired);
            for (byte b : ran) {
                if (b >= 0 && b < corpus.length) {
                    result[generated] = corpus[b];
                    generated += 1;
                    if (generated == desired) break;
                }
            }
        }
        return String.valueOf(result);
    }

    public void exitTeam(String username){
        db.collection("users").document(username).update("teamId",null, "isAdmin",false);

    }


    static public DBUpdate getInstance(){
        return endGameUpdate;
    }





}
