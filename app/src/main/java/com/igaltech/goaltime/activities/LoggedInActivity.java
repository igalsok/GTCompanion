package com.igaltech.goaltime.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.igaltech.goaltime.R;
import com.igaltech.goaltime.objects.CallBack;
import com.igaltech.goaltime.objects.DBUpdate;

import java.util.ArrayList;

public class LoggedInActivity extends AppCompatActivity implements CallBack<String> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email;
        if(user!=null) {
            email = user.getEmail();
            DBUpdate.getInstance().login(email,this);
        }




    }

    @Override
    public void callBack(ArrayList<String> list) {
        if(list == null){
            Intent joinCreateTeamIntent = new Intent(LoggedInActivity.this, JoinCreateTeamActivity.class);
            startActivity(joinCreateTeamIntent);
            finish();
        }
        else{
            Intent menuIntent = new Intent(LoggedInActivity.this, MenuActivity.class);
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