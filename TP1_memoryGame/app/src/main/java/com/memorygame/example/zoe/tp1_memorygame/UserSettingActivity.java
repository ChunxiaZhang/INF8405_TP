package com.memorygame.example.zoe.tp1_memorygame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Zoe on 15-01-23.
 */
public class UserSettingActivity extends ActionBarActivity {
    EditText nameEdit1;
    EditText nameEdit2;
    Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);
        nameEdit1 = (EditText) findViewById(R.id.nameEditText1);
        nameEdit2 = (EditText)findViewById(R.id.nameEditText2);
        if (MainActivity.isPlayWithTable)
        {
            nameEdit2.setHint("Tablet");
            // Switch to non-editable
            nameEdit2.setKeyListener(null);

        }

        startButton = (Button)findViewById(R.id.btnGameStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((nameEdit1.getText().toString().trim().length() != 0 &&
                        (nameEdit2.getText().toString().trim().length() != 0 && !MainActivity.isPlayWithTable)) ||
                        (nameEdit1.getText().toString().trim().length() != 0 && MainActivity.isPlayWithTable))
                {
                    if(nameEdit1.getText().toString().equalsIgnoreCase(nameEdit2.getText().toString()))
                    {
                        Toast.makeText(getApplicationContext(),"The names are same. Enter again!",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //Open GameView
                        //Toast.makeText(getApplicationContext(), "Start game!", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(UserSettingActivity.this, GamePlaying.class);

                        startActivity(i);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please enter your name!",Toast.LENGTH_SHORT).show();

                }


            }
        });

    }
}

