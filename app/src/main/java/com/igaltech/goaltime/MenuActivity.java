package com.igaltech.goaltime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    private FirebaseFirestore db ;
    private ImageButton revert;
    private Button logout;
    private ArrayList<Team> teams;
    private ConstraintLayout layoutCheck;
    private boolean isFinished;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();
        startGame = (Button) findViewById(R.id.btn_startMatchDay);
        addNewPlayer = (Button)findViewById(R.id.btn_addNewPlayer);
        scoreboard = (Button)findViewById(R.id.btn_scoreboardAdmin);
        addPlayer = (Button)findViewById(R.id.btn_addPlayer);
        playerName = (EditText)findViewById(R.id.txtBox_playerName);
        layout = (ConstraintLayout)findViewById(R.id.layout_addPlayer);
        nameError = (TextView)findViewById(R.id.textView_nameError);
        nameError.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.INVISIBLE);
        revert = (ImageButton) findViewById(R.id.btn_revert2);
        db = FirebaseFirestore.getInstance();
        logout = (Button) findViewById(R.id.btn_logout);
        layoutCheck = (ConstraintLayout)findViewById(R.id.layout_checkMatch);
        layoutCheck.bringToFront();
        layoutCheck.setElevation(1000);
        DBUpdate.getInstance().createTeams(this);
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        System.out.println(currentUser.getEmail());

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
                            startActivityForResult(chooseIntent, PICK_REQUEST);
                    }
                };
                thread.start();
            }
        });

        scoreboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Intent ratingIntent = new Intent(MenuActivity.this, RaitingBoardActivity.class);
                        startActivity(ratingIntent);
                    }
                };
                thread.start();
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
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
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
        db.collection("players")
        .whereEqualTo("name", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult()!=null && task.getResult().isEmpty()){
                        Player newPlayer = new Player(name);
                        db.collection("players").document(name).set(newPlayer);
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
            startGame.setVisibility(View.INVISIBLE);
            addNewPlayer.setVisibility(View.INVISIBLE);
            scoreboard .setVisibility(View.INVISIBLE);
        }
        else{
            layout.setVisibility(View.INVISIBLE);
            nameError.setVisibility(View.INVISIBLE);
            startGame.setVisibility(View.VISIBLE);
            addNewPlayer.setVisibility(View.VISIBLE);
            scoreboard .setVisibility(View.VISIBLE);
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
            startActivity(teamsIntent);
            isFinished = true;
            layoutCheck.setVisibility(View.GONE);
        }
        else{
            layoutCheck.setVisibility(View.GONE);
            isFinished = true;
        }
    }

}
