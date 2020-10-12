package com.igaltech.goaltime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements Serializable, View.OnClickListener {

    final static long PLAY_TIME = 480000;
    private Team teamOne;
    private Team teamTwo;
    private int teamOneGoals;
    private int teamTwoGoals;
    private Button[] players;
    private ImageButton revert;
    private Button goal;
    private Button assist;
    private ConstraintLayout layout;
    private ImageButton endGame;
    private TextView teamOneScore;
    private TextView teamTwoScore;
    private TextView minutes;
    private TextView seconds;
    private ImageButton pause;
    private ImageButton resume;
    private CountDownTimer timer;
    private long totalTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        revert = (ImageButton)findViewById(R.id.btn_revert);
        goal = (Button)findViewById(R.id.btn_goal);
        assist = (Button)findViewById(R.id.btn_assist);
        layout = (ConstraintLayout)findViewById(R.id.layout_goalOrAssist);
        layout.setVisibility(View.INVISIBLE);
        endGame = (ImageButton)findViewById(R.id.btn_finishGame);
        teamOneScore = (TextView)findViewById(R.id.txtView_scoreFirst);
        teamTwoScore = (TextView)findViewById(R.id.txtView_scoreSecond);
        minutes = (TextView)findViewById(R.id.txtView_hours);
        seconds = (TextView)findViewById(R.id.txtView_min);
        pause = (ImageButton)findViewById(R.id.btn_pause);
        resume = (ImageButton)findViewById(R.id.btn_startTimer);
        pause.setVisibility(View.INVISIBLE);
        totalTime = PLAY_TIME;
        layout.setElevation(1000);
        goal.bringToFront();
        assist.bringToFront();
        revert.bringToFront();
        layout.setElevation(1000);
        goal.bringToFront();
        assist.bringToFront();
        revert.bringToFront();
        teamOneGoals = 0;
        teamTwoGoals = 0;
        teamOneScore.setText(String.valueOf(teamOneGoals));
        teamTwoScore.setText(String.valueOf(teamTwoGoals));
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCountDownTimer();
                toggleTimerButtonVisibility();
            }
        });


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCountDownTimer();
                toggleTimerButtonVisibility();
            }
        });
        ArrayList<Team> teams = (ArrayList<Team>) getIntent().getSerializableExtra("teamsArrayList");
        teamOne = teams.get(0);
        teamTwo = teams.get(1);
        initButtons();
        for(int i=0;i<10;++i){
            if(i<5){
                players[i].setText(teamOne.playerNamesGet().get(i));
                players[i].setOnClickListener(this);
            }
            else{
                players[i].setText(teamTwo.playerNamesGet().get(i-5));
                players[i].setOnClickListener(this);
            }
        }
        endGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(teamOneGoals > teamTwoGoals){
                    teamOne.increaseWins();
                }
                else if(teamTwoGoals > teamOneGoals){
                    teamTwo.increaseWins();
                }
                else
                {
                    teamOne.increaseTies();
                    teamTwo.increaseTies();
                }
                Toast.makeText(GameActivity.this, "המשחק נגמר! ",
                        Toast.LENGTH_SHORT).show();
                teamOne.increaseGames();
                teamTwo.increaseGames();
                DBUpdate updateInstance = DBUpdate.getInstance();
                updateInstance.updatePlayers(teamOne.getPlayers());
                updateInstance.updatePlayers(teamTwo.getPlayers());
                Intent resultIntent = new Intent();
                resultIntent.putExtra("teamOne", teamOne);
                resultIntent.putExtra("teamTwo", teamTwo);
                setResult(Activity.RESULT_OK, resultIntent);
                GameActivity.this.finish();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    private void initButtons(){
        players = new Button[10];
        players[0] =  (Button)findViewById(R.id.btn_1);
        players[1] =  (Button)findViewById(R.id.btn_2);
        players[2] =  (Button)findViewById(R.id.btn_3);
        players[3] =  (Button)findViewById(R.id.btn_4);
        players[4] =  (Button)findViewById(R.id.btn_5);
        players[5] =  (Button)findViewById(R.id.btn_6);
        players[6] =  (Button)findViewById(R.id.btn_7);
        players[7] =  (Button)findViewById(R.id.btn_8);
        players[8] =  (Button)findViewById(R.id.btn_9);
        players[9] =  (Button)findViewById(R.id.btn_10);
    }

    @Override
    public void onClick(View view) {
        layout.setVisibility(View.VISIBLE);
        revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setVisibility(View.INVISIBLE);
            }
        });
        Button playerButton = (Button)findViewById(view.getId());
        final Player player = teamOne.getPlayer(playerButton.getText().toString());
        if(player == null) {
           final Player player2 = teamTwo.getPlayer(playerButton.getText().toString());
            goal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    player2.increaseGoals();
                    teamTwoGoals += 1;
                    updateScoreLine();
                    Toast.makeText(GameActivity.this,  "שער נהדר של " +player2.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            assist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    player2.increaseAssists();
                    Toast.makeText(GameActivity.this, "בישול נהדר של " +player2.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            goal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    player.increaseGoals();
                    teamOneGoals += 1;
                    updateScoreLine();
                    Toast.makeText(GameActivity.this, "שער נהדר של " + player.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            assist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    player.increaseAssists();
                    Toast.makeText(GameActivity.this, "בישול נהדר של " + player.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void updateScoreLine(){
        teamOneScore.setText(String.valueOf(teamOneGoals));
        teamTwoScore.setText(String.valueOf(teamTwoGoals));
    }

    private void startCountDownTimer() {
        timer = new CountDownTimer(totalTime, 1000) {
            public void onTick(long millisUntilFinished) {
                totalTime = millisUntilFinished;
                seconds.setText(String.format("%02d", (millisUntilFinished / 1000)% 60));
                minutes.setText(String.format("%02d", (millisUntilFinished / 60000)));
            }

            public void onFinish() {

            }
        }.start();
    }
    private void stopCountDownTimer(){
        timer.cancel();
    }

    private void toggleTimerButtonVisibility(){
        if(resume.getVisibility() == View.INVISIBLE){
            resume.setVisibility(View.VISIBLE);
            pause.setVisibility(View.INVISIBLE);
        }
        else{
            resume.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();

    }

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



}