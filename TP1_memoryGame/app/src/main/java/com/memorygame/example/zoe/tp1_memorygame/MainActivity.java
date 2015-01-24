package com.memorygame.example.zoe.tp1_memorygame;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;



public class MainActivity extends ActionBarActivity{

    Button btnTwoPlayersModel;
    Button btnPlayWithTablet;
    Button btnBestScores;
    Button btnExit;
    public static boolean isPlayWithTable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTwoPlayersModel = (Button) findViewById(R.id.btnTwoPlayers);

        btnTwoPlayersModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlayWithTable = false;
                Intent i = new Intent(MainActivity.this, UserSettingActivity.class);
                startActivity(i);
            }
        });

        btnPlayWithTablet = (Button) findViewById(R.id.btnWithTablet);

        btnPlayWithTablet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlayWithTable = true;
                Intent i = new Intent(MainActivity.this, UserSettingActivity.class);
                startActivity(i);
            }
        });

        btnBestScores = (Button) findViewById(R.id.btnScores);
        btnBestScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ScoresActivity.class);

                startActivity(i);
            }
        });

        btnExit = (Button) findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
