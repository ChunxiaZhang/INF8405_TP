package com.memorygame.example.zoe.tp1_memorygame;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

/**
 * The MainActivity class implements an activity that displays game model choose view
 */

public class MainActivity extends ActionBarActivity
        implements PlayerSettingDialogFragment.PlayerSettingListener{

    Button btnTwoPlayersModel;
    Button btnPlayWithRobot;
    Button btnBestScores;
    Button btnExit;

    public static MemoryGameSQLiteOpenHelper dbHelper;
    private PopupWindow scorePopupWindow;
    private View popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AgentApplication.getInstance().addActivity(this);
        dbHelper = new MemoryGameSQLiteOpenHelper(MainActivity.this);
        btnTwoPlayersModel = (Button) findViewById(R.id.btnTwoPlayers);

        /**
         *  Two human players model button listener
         *  To open player name setting dialog
         */
        btnTwoPlayersModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                DialogFragment newFragment = new PlayerSettingDialogFragment();
                Bundle args = new Bundle();
                args.putBoolean("RobotPlaying", false); // Set play model is not playing with robot
                newFragment.setArguments(args);
                newFragment.show(ft, "dialog");

            }
        });

        btnPlayWithRobot = (Button) findViewById(R.id.btnWithRobot);

        /**
         *  Play with robot model button listener
         *  To open player name setting dialog
         */
        btnPlayWithRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                DialogFragment newFragment = new PlayerSettingDialogFragment();
                Bundle args = new Bundle();
                args.putBoolean("RobotPlaying", true); //Set play model is playing with robot
                newFragment.setArguments(args);
                newFragment.show(ft, "dialog");
            }
        });

        btnBestScores = (Button) findViewById(R.id.btnScores);
        /**
         * Best scores button listener
         * To open scores dialog
         */
        btnBestScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("onCLick Scores", "button pressed");
                showBestScores();
            }
        });

        btnExit = (Button) findViewById(R.id.btnExit);
        /**
         * Exit button listener
         * To quit perfectly the application by using AgentApplication class via onTerminate() closing all activities
         */
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgentApplication.getInstance().onTerminate();
            }
        });

        LayoutInflater inflater = LayoutInflater.from(this);
        popupView = inflater.inflate(R.layout.popup_bestscores, null);
        scorePopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        Button btn_dismiss = (Button) popupView.findViewById(R.id.btn_dismiss);
        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scorePopupWindow.dismiss();
                scorePopupWindow.setFocusable(false);
            }
        });
        LinearLayout popupLayout = (LinearLayout) popupView.findViewById(R.id.layout_score);
        popupLayout.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)
                    scorePopupWindow.dismiss();
                return false;
            }
        });

    }

    @Override
    public void startNewGame(String name1, String name2, boolean isRobotPlaying) {
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        i.putExtra("playerOneName",name1);
        i.putExtra("playerTwoName",name2);
        i.putExtra("robotPlayMode",isRobotPlaying);
        startActivity(i); //Send two players' name to GameActivity

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

    /**
     * Get data from MemoryGameSQLiteOpenHelper and show them
     */
    public void showBestScores(){
        scorePopupWindow.setAnimationStyle(R.style.PopupAnimation);
        Log.i("showBestScores:", "set animation");
        scorePopupWindow.showAtLocation(findViewById(R.id.gameBtnLinearLayout), Gravity.NO_GRAVITY, 0, 0);
        Log.i("showBestScores:", "showAtLocation");
        List<Pair<String,Integer>> bestScores = dbHelper.findBestScore();
        Log.i("showBestScores:", "bestScores list");
        if(bestScores.size()>0){
            TextView playerName1 = (TextView)popupView.findViewById(R.id.playerName1);
            playerName1.setText(bestScores.get(0).first);
            TextView score1 = (TextView)popupView.findViewById(R.id.playerScore1);
            score1.setText(bestScores.get(0).second.toString());
        }
        if(bestScores.size()>1){
            TextView playerName2 = (TextView)popupView.findViewById(R.id.playerName2);
            playerName2.setText(bestScores.get(1).first);
            TextView score2 = (TextView)popupView.findViewById(R.id.playerScore2);
            score2.setText(bestScores.get(1).second.toString());
        }
        if(bestScores.size()>2){
            TextView playerName3 = (TextView)popupView.findViewById(R.id.playerName3);
            playerName3.setText(bestScores.get(2).first);
            TextView score3 = (TextView)popupView.findViewById(R.id.playerScore3);
            score3.setText(bestScores.get(2).second.toString());
        }
        if(bestScores.size()>3) {
            TextView playerName4 = (TextView) popupView.findViewById(R.id.playerName4);
            playerName4.setText(bestScores.get(3).first);
            TextView score4 = (TextView) popupView.findViewById(R.id.playerScore4);
            score4.setText(bestScores.get(3).second.toString());
        }
        if(bestScores.size()>4) {
            TextView playerName5 = (TextView) popupView.findViewById(R.id.playerName5);
            playerName5.setText(bestScores.get(4).first);
            TextView score5 = (TextView) popupView.findViewById(R.id.playerScore5);
            score5.setText(bestScores.get(4).second.toString());
        }
        scorePopupWindow.setFocusable(true);
        scorePopupWindow.update();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AgentApplication.getInstance().onTerminate();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
