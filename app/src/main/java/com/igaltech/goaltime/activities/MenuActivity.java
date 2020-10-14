package com.igaltech.goaltime.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.igaltech.goaltime.objects.CallBack;
import com.igaltech.goaltime.objects.DBUpdate;
import com.igaltech.goaltime.objects.Player;
import com.igaltech.goaltime.R;
import com.igaltech.goaltime.objects.Team;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity implements CallBack<Team> {
    static final int PICK_REQUEST = 0;
    private FirebaseAuth mAuth;
    private Button startGame;
    private Button addNewPlayer;
    private Button scoreboard;
    private Button addPlayer;
    private EditText playerName;
    private ConstraintLayout layout;
    private TextView nameError;
    private String teamName;
    private FirebaseFirestore db ;
    private ImageButton revert;
    private Button logout;
    private ArrayList<Team> teams;
    private ConstraintLayout layoutCheck;
    private boolean isFinished;
    private String teamId;
    private Boolean isAdmin;
    private Button userSettings;
    private Button userRating;
    private ConstraintLayout layoutSettings;
    private ImageButton revertSettings;
    private TextView teamIdView;
    private TextView teamNameView;
    private long teamsNumber;
    private Button exitTeam;
    private RadioGroup radioGroup;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        teamsNumber = 3;
        mAuth = FirebaseAuth.getInstance();
        startGame = (Button) findViewById(R.id.btn_startMatchDay);
        addNewPlayer = (Button)findViewById(R.id.btn_addNewPlayer);
        scoreboard = (Button)findViewById(R.id.btn_scoreboardAdmin);
        addPlayer = (Button)findViewById(R.id.btn_addPlayer);
        playerName = (EditText)findViewById(R.id.txtBox_playerName);
        layout = (ConstraintLayout)findViewById(R.id.layout_addPlayer);
        nameError = (TextView)findViewById(R.id.textView_nameError);
        nameError.setVisibility(View.INVISIBLE);
        userRating = (Button)findViewById(R.id.btn_raitingUser);
        userSettings = (Button)findViewById(R.id.btn_settingsUser);
        layout.setVisibility(View.INVISIBLE);
        layout.bringToFront();
        layout.setElevation(1000);
        revert = (ImageButton) findViewById(R.id.btn_revert2);
        revertSettings = (ImageButton)findViewById(R.id.btn_revertSettings) ;
        teamIdView = (TextView)findViewById(R.id.textView_teamId);
        teamNameView = (TextView)findViewById(R.id.textView_teamName);
        exitTeam = (Button)findViewById(R.id.btn_exitGroup);
        db = FirebaseFirestore.getInstance();
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup) ;
        radioGroup.check(R.id.radioButton_3);
        logout = (Button) findViewById(R.id.btn_logout);
        layoutCheck = (ConstraintLayout)findViewById(R.id.layout_checkMatch);
        layoutCheck.bringToFront();
        layoutCheck.setElevation(1000);
        layoutSettings = (ConstraintLayout)findViewById(R.id.layout_settings);
        layoutSettings.bringToFront();
        layoutSettings.setElevation(1200);
        layoutSettings.setVisibility(View.INVISIBLE);
        Bundle b = getIntent().getExtras();
        if(b!=null){
            teamId = (String)b.get("teamId");
            isAdmin = (Boolean)b.get("isAdmin");
            teamName = (String)b.get("teamName");
        }
        if(!isAdmin){
            startGame.setVisibility(View.INVISIBLE);
            addNewPlayer.setVisibility(View.INVISIBLE);
            scoreboard.setVisibility(View.INVISIBLE);
            layoutCheck.setVisibility(View.INVISIBLE);
        }
        else{
            DBUpdate.getInstance().createTeams(teamId,this);
            userRating.setVisibility(View.INVISIBLE);
        }
        teamIdView.setText(teamId);
        teamNameView.setText(teamName);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        playerName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        addNewPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLayout();
            }
        });
        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addNewPlayer(playerName.getText().toString());
            }
        });

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                            Intent chooseIntent = new Intent(MenuActivity.this, ChoosePlayersActivity.class);
                            chooseIntent.putExtra("teamId",teamId);
                            chooseIntent.putExtra("numOfTeams", teamsNumber);
                            startActivity(chooseIntent);
                    }
                };
                thread.start();
            }
        });

        scoreboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        Intent ratingIntent = new Intent(MenuActivity.this, RaitingBoardActivity.class);
                        startActivity(ratingIntent);
            }
        });

        revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerName.setText("");
                toggleLayout();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mAuth.signOut();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Intent mainIntent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        userRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        Intent ratingIntent = new Intent(MenuActivity.this, RaitingBoardActivity.class);
                        startActivity(ratingIntent);
            }
        });
        userSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        revertSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSettingLayout();
            }
        });
        userSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSettingLayout();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.radioButton_3:
                        teamsNumber =3;
                        break;
                    case R.id.radioButton_4:
                        teamsNumber =4;
                        break;
                }
            }
        });

        exitTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MenuActivity.this)
                        .setTitle("יציאה מהקבוצה")
                        .setMessage("האם אתה בטוח שברצונך לצאת מהקבוצה?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DBUpdate.getInstance().exitTeam(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                Intent joinCreateTeamIntent = new Intent(MenuActivity.this, JoinCreateTeamActivity.class);
                                startActivity(joinCreateTeamIntent);
                                finish();
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("לא", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onPause() {

        super.onPause();

    }


    private void addNewPlayer(final String name){
        if(name.isEmpty()){
            nameError.setVisibility(View.VISIBLE);
            return;
        }
        db.collection("teams").document("teamId").collection("players")
        .whereEqualTo("name", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult()!=null && task.getResult().isEmpty()){
                        Player newPlayer = new Player(name);
                        db.collection("teams").document(teamId).collection("players").document(name).set(newPlayer);
                        toggleLayout();
                        Toast.makeText(MenuActivity.this, "נוסף שחקן חדש! ",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        nameError.setVisibility(View.VISIBLE);
                    }

                }

            }
        });
    }


    private void toggleLayout(){
        if(layout.getVisibility() == View.INVISIBLE){
            layout.setVisibility(View.VISIBLE);

        }
        else{
            layout.setVisibility(View.INVISIBLE);
        }
    }

    private void toggleSettingLayout(){
        if(layoutSettings.getVisibility() == View.INVISIBLE){
            layoutSettings.setVisibility(View.VISIBLE);
         //   if(!isAdmin){
               // radioGroup.setVisibility(View.INVISIBLE);
           // }
        }
        else{
            layoutSettings.setVisibility(View.INVISIBLE);
        }
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
    public void callBack(ArrayList<Team> teams) {
        this.teams = teams;
        if(teams != null){
            Intent teamsIntent = new Intent(MenuActivity.this, TeamsActivity.class);
            teamsIntent.putExtra("teams", teams);
            teamsIntent.putExtra("teamId",teamId);
            startActivity(teamsIntent);
            isFinished = true;
        }
        else{
            layoutCheck.setVisibility(View.GONE);
            isFinished = true;
        }
    }

}
