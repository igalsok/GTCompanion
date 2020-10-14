package com.igaltech.goaltime.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.igaltech.goaltime.objects.CallBack;
import com.igaltech.goaltime.objects.DBUpdate;
import com.igaltech.goaltime.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CallBack<String> {
    private static final String USERS = "users";
    private static final String PLAYERS = "players";
    private Button loginBtn;
    private Button register;
    private ImageButton revertRegister;
    private ConstraintLayout registerLayout;
    private EditText username;
    private EditText password;
    private EditText username2;
    private EditText password2;
    private Button register2;
    private ImageView smallLogo;
    private ImageView v;
    private ImageView x;
    private CountDownTimer timer;
    private ProgressBar progressBar;
    private ProgressBar progressBarRegister;
    private FirebaseAuth mAuth;
    private EditText email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            Intent LoggedInIntent = new Intent(MainActivity.this, LoggedInActivity.class);
            startActivity(LoggedInIntent);
            finish();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        loginBtn = (Button) findViewById(R.id.btn_Login);
        register = (Button) findViewById(R.id.btn_register);
        username = (EditText) findViewById(R.id.txtBox_username);
        password = (EditText) findViewById(R.id.txtBox_password);
        smallLogo = (ImageView) findViewById(R.id.imgView_smallLogo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_login);
        progressBarRegister = (ProgressBar) findViewById(R.id.progressBar_register);
        registerLayout = (ConstraintLayout)findViewById(R.id.layout_register);
        revertRegister = (ImageButton)findViewById(R.id.btn_revertRegister);
        username2 = (EditText)findViewById(R.id.txtBox_username2);
        password2 = (EditText)findViewById(R.id.txtBox_password2);
        register2 = (Button)findViewById(R.id.btn_register2);
        email = (EditText)findViewById(R.id.txtBox_email) ;
        v = (ImageView)findViewById(R.id.imgView_v);
        x = (ImageView)findViewById(R.id.imgView_x);
        x.setVisibility(View.INVISIBLE);
        v.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onStart() {
        super.onStart();
        registerLayout.setVisibility(View.INVISIBLE);
        registerLayout.setElevation(1000);
        smallLogo.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.VISIBLE);
        register.setVisibility(View.VISIBLE);
        username.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLayout();
            }
        });
        revertRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLayout();
            }
        });
        register2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                progressBarRegister.setVisibility(View.VISIBLE);
                v.setVisibility(View.INVISIBLE);
                x.setVisibility(View.INVISIBLE);
                final String emailTxt = email.getText().toString().toLowerCase();
                final String passwordTxt = password2.getText().toString();
                final String usernameTxt = username2.getText().toString();

                if (email.getText().toString().isEmpty() || passwordTxt.isEmpty() || usernameTxt.isEmpty()) {
                    progressBarRegister.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "אנא הכנס את כל הפרטים!",
                            Toast.LENGTH_SHORT).show();
                }
                else if(usernameTxt.length() > 10){
                    progressBarRegister.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "שם המשתמש ארוך מדי!",
                            Toast.LENGTH_SHORT).show();
                }
                else if(!emailTxt.contains("@") || !emailTxt.contains(".")){
                    progressBarRegister.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "אימייל לא תקני",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBarRegister.setVisibility(View.VISIBLE);
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                DBUpdate.getInstance().register(emailTxt,usernameTxt);
                                                Toast.makeText(MainActivity.this, "נרשמת בהצלחה!",
                                                        Toast.LENGTH_SHORT).show();
                                                toggleLayout();
                                                progressBarRegister.setVisibility(View.INVISIBLE);
                                            }
                                            else {
                                                Toast.makeText(MainActivity.this, "שם המשתמש תפוס!",
                                                        Toast.LENGTH_SHORT).show();
                                                progressBarRegister.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                        }
                    };
                    thread.start();
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                v.setVisibility(View.INVISIBLE);
                x.setVisibility(View.INVISIBLE);
                final String usernameTxt = username.getText().toString();
                final String passwordTxt = password.getText().toString();
                if (username.getText().toString().isEmpty() || passwordTxt.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if(!usernameTxt.contains("@") || !usernameTxt.contains(".")){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "אימייל לא תקני",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            mAuth.signInWithEmailAndPassword(usernameTxt, passwordTxt)
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                DBUpdate.getInstance().login(user.getEmail(),MainActivity.this);
                                            } else {
                                                progressBar.setVisibility(View.INVISIBLE);
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
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

    public void toggleLayout(){
        if(registerLayout.getVisibility() == View.VISIBLE)
            registerLayout.setVisibility(View.INVISIBLE);
        else
            registerLayout.setVisibility(View.VISIBLE);
        progressBarRegister.setVisibility(View.INVISIBLE);
    }


    @Override
    public void callBack(ArrayList<String> list) {
        if(list == null){
            progressBar.setVisibility(View.INVISIBLE);
            v.setVisibility(View.VISIBLE);
            Intent joinCreateTeamIntent = new Intent(MainActivity.this, JoinCreateTeamActivity.class);
            startActivity(joinCreateTeamIntent);
            finish();
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            v.setVisibility(View.VISIBLE);
            Intent menuIntent = new Intent(MainActivity.this, MenuActivity.class);
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