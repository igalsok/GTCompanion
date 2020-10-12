package com.igaltech.goaltime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChoosePlayersActivity  extends AppCompatActivity implements Serializable {

    private ListView listView;
    private Button continueBtn;
    private HashMap<String,Player> players;
    FirebaseFirestore db ;
    private ProgressBar progressBar;
    private TextView count;
    private int countPlayers = 0;
    private static final String COUNT_STRING = " / 15";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_choose_players);
        listView = (ListView) findViewById(R.id.listview);
        continueBtn = (Button) findViewById(R.id.btn_continue);
        count = (TextView)findViewById(R.id.txtView_count);
    }
    public void onStart() {
        super.onStart();
        String newString = String.valueOf(countPlayers) + COUNT_STRING;
        count.setText(newString);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_choose);
        players = new HashMap<>();
        final List<UserModel> users = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int playersCount = 0;
                for(UserModel model : users){
                    if(model.isSelected)
                        playersCount++;
                }
                if(playersCount==15) {
                    for (UserModel model : users) {
                        if (!model.isSelected) {
                            players.remove(model.getUserName());
                        }
                    }
                    Intent teamsIntent = new Intent(ChoosePlayersActivity.this, TeamsActivity.class);
                    teamsIntent.putExtra("playersHashMap", players);
                    startActivity(teamsIntent);
                    finish();
                }
                else{
                    Toast.makeText(ChoosePlayersActivity.this, "צריך בדיוק 15 שחקנים!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        db.collection("players").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        users.add(new UserModel(false, (String)document.get("name")));
                        Player newPlayer = document.toObject(Player.class);
                        players.put(newPlayer.getName(),newPlayer);
                    }
                    final CustomAdapter adapter = new CustomAdapter(ChoosePlayersActivity.this, users);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            UserModel model = users.get(i);
                            if (model.isSelected()) {
                                countPlayers--;
                                String newString = (String.valueOf(countPlayers) + COUNT_STRING);
                                count.setText(newString);
                                changeCountColor();
                                model.setSelected(false);
                            }
                            else {
                                countPlayers++;
                                String newString = (String.valueOf(countPlayers) + COUNT_STRING);
                                count.setText(newString);
                                changeCountColor();
                                model.setSelected(true);
                            }
                            users.set(i, model);
                            //now update adapter
                            adapter.updateRecords(users);
                        }
                    });
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        countPlayers = 0;
        String newString = String.valueOf(countPlayers) + COUNT_STRING;
        count.setText(newString);
    }

    private void changeCountColor() {
        if (countPlayers != 15) {
            count.setTextColor(Color.parseColor("#ff3232"));
        } else
        {
            count.setTextColor(Color.parseColor("#60FFA8"));
        }
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

}