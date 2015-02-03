package com.memorygame.example.zoe.tp1_memorygame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * Created by Zoe on 15-01-26.
 */
public class GamePlaying extends Activity {

    GridView gridViewGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_view);
        gridViewGame = (GridView) findViewById(R.id.gameGridView);

        gridViewGame.setAdapter(new ImageAdapter(this));

        /*gridViewGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

        }
    }


