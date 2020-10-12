package com.igaltech.goaltime;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TeamsActivity extends AppCompatActivity implements Serializable, CallBack<MatchDay>{

    private HashMap<String,Player> players;
    private ArrayList<Team> teams;
    private Button startGame;
    private Button endMatchDay;
    private ArrayList<CheckBox> checkBoxes;
    private MatchDay matchDay;
    private ListView lv1;
    private ListView lv2;
    private ListView lv3;


    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_teams);
        lv1 = (ListView) findViewById(R.id.lv_first);
        lv2 = (ListView) findViewById(R.id.lv_second);
        lv3 = (ListView) findViewById(R.id.lv_third);
        startGame = (Button) findViewById(R.id.btn_startGame) ;
        endMatchDay = (Button) findViewById(R.id.btn_endMatchDay) ;
        CheckBox checkBoxOne = (CheckBox) findViewById(R.id.checkBox_1);
        CheckBox checkBoxTwo = (CheckBox) findViewById(R.id.checkBox_2);
        CheckBox checkBoxThree = (CheckBox) findViewById(R.id.checkBox_3);
        checkBoxes = new ArrayList<>();
        checkBoxes.add(checkBoxOne);
        checkBoxes.add(checkBoxTwo);
        checkBoxes.add(checkBoxThree);
        players = (HashMap<String,Player>)getIntent().getSerializableExtra("playersHashMap");
        teams = new ArrayList<>();
        if(players != null) {
            createTeams();
            ArrayAdapter<String> teamOneAdapter = new ArrayAdapter<String>(this,R.layout.teams_text_view,teams.get(0).playerNamesGet());
            ArrayAdapter<String> teamTwoAdapter = new ArrayAdapter<String>(this,R.layout.teams_text_view,teams.get(1).playerNamesGet());
            ArrayAdapter<String> teamThreeAdapter = new ArrayAdapter<String>(this,R.layout.teams_text_view,teams.get(2).playerNamesGet());
            lv1.setAdapter(teamOneAdapter);
            lv2.setAdapter(teamTwoAdapter);
            lv3.setAdapter(teamThreeAdapter);
            Thread one = new Thread() {
                public void run() {
                    matchDay =  DBUpdate.getInstance().createNewMatchDay(teams);
                }
            };
            one.start();
        }
        else {
            teams = (ArrayList<Team>)getIntent().getSerializableExtra("teams");
            System.out.println(teams.get(0).playerNamesGet().get(0));
            DBUpdate.getInstance().getLastMatchDay(this);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Team> playingTeams = new ArrayList<>();
                for(int i=0; i<checkBoxes.size();++i){
                    if(checkBoxes.get(i).isChecked())
                        playingTeams.add(teams.get(i));
                }
                if(playingTeams.size()!=2){
                    Toast.makeText(TeamsActivity.this, "צריך בדיוק 2 קבוצות!",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent gameIntent = new Intent(TeamsActivity.this, GameActivity.class);
                    gameIntent.putExtra("teamsArrayList", playingTeams);
                    for(CheckBox check : checkBoxes){
                        check.setChecked(false);
                    }
                    startActivityForResult(gameIntent, 1);
                }

            }
        });

        endMatchDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBUpdate.getInstance().endMatchDay(matchDay);
                finish();
            }
        });
    }




    private void createTeams(){
        ArrayList<Player> playerList = new ArrayList<Player>(players.values());
        Collections.sort(playerList);
        for(int i=0;i<3;++i){
            Team team = createTeamsHeleper(playerList, i);
            this.teams.add(team);
            playerList.removeAll(teams.get(i).getPlayers());
        }
    }

    private Team createTeamsHeleper(ArrayList<Player> playerList, int id){
        ArrayList<Player> teamList = new ArrayList<>();
            int multiplier = playerList.size() / 5;
            for (int i = 0; i < 5; ++i) {
                double rand = (int) (Math.random() * multiplier) + i * multiplier;
                teamList.add(playerList.get((int) rand));
            }
        return new Team(teamList, id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == Activity.RESULT_OK) {
                   Team teamOne = (Team)data.getExtras().getSerializable("teamOne");
                   Team teamTwo = (Team)data.getExtras().getSerializable("teamTwo");
                    for(int i=0;i<teams.size();++i){
                        Team team = teams.get(i);
                        if(team.getTeamId() == teamOne.getTeamId()){
                            teams.set(i,teamOne);
                            matchDay.teamByIdSet(teams.get(i));
                        }
                        else if(team.getTeamId() == teamTwo.getTeamId()){
                            teams.set(i,teamTwo);
                            matchDay.teamByIdSet(teams.get(i));
                        }
                }
                    if(matchDay!=null) {
                        Thread one = new Thread() {
                            public void run() {
                                DBUpdate.getInstance().updateMatchDay(matchDay);
                            }
                        };
                        one.start();
                    }
        }
    }
    @Override
    public void onResume(){
        super.onResume();
    }

    private void updateTeams(){

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void callBack(ArrayList<MatchDay> list) {
        matchDay = list.get(0);
        ArrayAdapter<String> teamOneAdapter = new ArrayAdapter<String>(this,R.layout.teams_text_view,teams.get(0).playerNamesGet());
        ArrayAdapter<String> teamTwoAdapter = new ArrayAdapter<String>(this,R.layout.teams_text_view,teams.get(1).playerNamesGet());
        ArrayAdapter<String> teamThreeAdapter = new ArrayAdapter<String>(this,R.layout.teams_text_view,teams.get(2).playerNamesGet());
        lv1.setAdapter(teamOneAdapter);
        lv2.setAdapter(teamTwoAdapter);
        lv3.setAdapter(teamThreeAdapter);
    }
}