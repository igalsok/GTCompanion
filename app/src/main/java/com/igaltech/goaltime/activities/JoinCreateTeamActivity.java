package com.igaltech.goaltime.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.igaltech.goaltime.objects.CallBack;
import com.igaltech.goaltime.objects.DBUpdate;
import com.igaltech.goaltime.R;

import java.util.ArrayList;

public class JoinCreateTeamActivity extends AppCompatActivity implements CallBack<String> {

    private Button join;
    private Button create;
    private Button logout;
    private ImageButton revertJoin;
    private ConstraintLayout layoutJoin;
    private EditText teamId;
    private Button joinTeam;
    private ImageButton revertCreate;
    private EditText teamName;
    private Button createTeam;
    private ConstraintLayout layoutCreate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_join_create_team);
        join = (Button)findViewById(R.id.btn_joinTeam);
        create = (Button)findViewById(R.id.btn_createTeam);
        logout = (Button)findViewById(R.id.btn_logout2);
        revertJoin = (ImageButton)findViewById(R.id.btn_revertJoin);
        layoutJoin = (ConstraintLayout)findViewById(R.id.layout_joinTeam);
        teamId = (EditText)findViewById(R.id.txtBox_teamId);
        joinTeam = (Button)findViewById(R.id.btn_joinTeam2);
        createTeam = (Button)findViewById(R.id.btn_createTeam2);
        teamName = (EditText)findViewById(R.id.txtBox_teamName);
        revertCreate = (ImageButton)findViewById(R.id.btn_revertCreate);
        layoutJoin.setElevation(1000);
        layoutJoin.setVisibility(View.INVISIBLE);
        layoutCreate = (ConstraintLayout)findViewById(R.id.layout_createTeam);
        layoutCreate.setElevation(1000);
        layoutCreate.setVisibility(View.INVISIBLE);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleJoinLayout();
            }
        });
        revertJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleJoinLayout();
            }
        });
        joinTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamIdTxt = teamId.getText().toString();
                if(!teamIdTxt.isEmpty()) {
                    String username = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    System.out.println("TeamId "+ teamIdTxt);
                    DBUpdate.getInstance().joinTeam(teamIdTxt, username, JoinCreateTeamActivity.this);
                }
            }
        });
        revertCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCreateLayout();
            }
        });
        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamNameTxt = teamName.getText().toString();
                if(!teamNameTxt.isEmpty()) {
                    String username = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    DBUpdate.getInstance().createTeam(teamNameTxt,username,JoinCreateTeamActivity.this);
                }
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCreateLayout();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseAuth.getInstance().signOut();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Intent mainIntent = new Intent(JoinCreateTeamActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    public void toggleJoinLayout(){
        if(layoutJoin.getVisibility() == View.INVISIBLE){
            layoutJoin.setVisibility(View.VISIBLE);
        }
        else{
            layoutJoin.setVisibility(View.INVISIBLE);
        }

    }
    public void toggleCreateLayout(){
        if(layoutCreate.getVisibility() == View.INVISIBLE){
            layoutCreate.setVisibility(View.VISIBLE);
        }
        else{
            layoutCreate.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void callBack(ArrayList<String> list) {
        if(list == null){
            Toast.makeText(JoinCreateTeamActivity.this, "מספר קבוצה אינו קיים!",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(JoinCreateTeamActivity.this, "ברכות! הצטרפת לקבוצה חדשה!",
                    Toast.LENGTH_SHORT).show();
            Intent menuIntent = new Intent(JoinCreateTeamActivity.this, MenuActivity.class);
            menuIntent.putExtra("teamId",list.get(0));
            menuIntent.putExtra("teamName",list.get(1));
            if(list.size()>2){
                menuIntent.putExtra("isAdmin",true);
            }
            else{
                menuIntent.putExtra("isAdmin",false);
            }
            startActivity(menuIntent);
            finish();
        }
    }
}