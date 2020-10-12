package com.igaltech.goaltime;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.ArrayList;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;


public class RaitingBoardActivity extends AppCompatActivity implements CallBack<Player> {

    private SortableTableView table;

    private final String[] tableHeader = {"שם", "גולים", "בישולים", "ניצחונות", "תיקו", "משחקים", "רייטינג"};
    private String[][] data;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_raiting_board);
    }
    @Override
    protected  void onStart(){
        super.onStart();
        progressBar = (ProgressBar)findViewById(R.id.progressBar_rating);
        table = (SortableTableView) findViewById(R.id.table);
        table.setHeaderBackgroundColor(Color.parseColor("#2ecc71"));
        table.setHeaderAdapter(new SimpleTableHeaderAdapter(RaitingBoardActivity.this, tableHeader));
        table.setColumnCount(7);
        table.setTextDirection(View.TEXT_DIRECTION_RTL);
        table.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        DBUpdate.getInstance().getAllPlayers(RaitingBoardActivity.this);
    }

    @Override
    public void callBack(ArrayList<Player> players) {
        data = new String[players.size()][7];
        for (int i = 0; i < players.size(); ++i) {
            Player player = players.get(i);
            data[i][0] = player.getName();
            data[i][1] = String.valueOf(player.getGoals());
            data[i][2] = String.valueOf(player.getAssists());
            data[i][3] = String.valueOf(player.getWins());
            data[i][4] = String.valueOf(player.getTies());
            data[i][5] = String.valueOf(player.getGames());
            data[i][6] = String.valueOf(player.calculateRating());
        }
        SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(RaitingBoardActivity.this, data);
        dataAdapter.setTextColor(Color.parseColor("#FFFFFF"));
        table.setDataAdapter(dataAdapter);
        progressBar.setVisibility(View.INVISIBLE);

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
    public void onResume(){
        super.onResume();
    }


}