package com.igaltech.goaltime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String USERS = "users";
    private static final String PLAYERS = "players";
    private Button loginBtn;
    private Button scoreboard;
    private EditText username;
    private EditText passowrd;
    private ImageView smallLogo;
    private ImageView v;
    private ImageView x;
    private CountDownTimer timer;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        loginBtn = (Button) findViewById(R.id.btn_Login);
        scoreboard = (Button) findViewById(R.id.btn_scoreboard);
        username = (EditText) findViewById(R.id.txtBox_username);
        passowrd = (EditText) findViewById(R.id.txtBox_password);
        smallLogo = (ImageView) findViewById(R.id.imgView_smallLogo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_login);
        v = (ImageView)findViewById(R.id.imgView_v);
        x = (ImageView)findViewById(R.id.imgView_x);
        smallLogo.setVisibility(View.INVISIBLE);
        loginBtn.setVisibility(View.INVISIBLE);
        scoreboard.setVisibility(View.INVISIBLE);
        username.setVisibility(View.INVISIBLE);
        passowrd.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        v.setVisibility(View.INVISIBLE);
        x.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        smallLogo.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.VISIBLE);
        scoreboard.setVisibility(View.VISIBLE);
        username.setVisibility(View.VISIBLE);
        passowrd.setVisibility(View.VISIBLE);
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        passowrd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        scoreboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scoreIntent = new Intent(MainActivity.this, RaitingBoardActivity.class);
                startActivity(scoreIntent);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                v.setVisibility(View.INVISIBLE);
                x.setVisibility(View.INVISIBLE);
                final String usernameTxt = username.getText().toString() + "@mygoaltimeapp.com";
                final String passwordTxt = passowrd.getText().toString();
                if (username.getText().toString().isEmpty() || passwordTxt.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {

                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            mAuth.signInWithEmailAndPassword(usernameTxt, passwordTxt)
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                v.setVisibility(View.VISIBLE);
                                                timer = new CountDownTimer(500, 500) {
                                                    @Override
                                                    public void onTick(long l) {

                                                    }
                                                    @Override
                                                    public void onFinish() {
                                                        Intent menuIntent = new Intent(MainActivity.this, MenuActivity.class);
                                                        startActivity(menuIntent);
                                                        finish();
                                                    }
                                                };
                                                timer.start();
                                            } else {
                                                x.setVisibility(View.VISIBLE);
                                            }
                                        }

                                    });
                        }
                    };
                    thread.start();
                }
                }
        });


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
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}