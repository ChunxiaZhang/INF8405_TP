package com.mymemorygame.example.zoe.mymemorygame;

import android.app.ActionBar;
import android.content.res.Configuration;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;
import android.util.Pair;
import android.view.Gravity;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;

import android.widget.Button;


import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity implements PlayerSettingDialogFragment.PlayerSettingListener,
        OptionDialogFragment.SelectItemListener, GameFinishDialogFragment.SelectFinishItemListener, Observer {

    private TableLayout gameTable;
    private TextView playerScoreText1, playerScoreText2;
    public static int ROW_COUNT = 6;
    public static int COL_COUNT = 4;
    public Piece firstPiece;
    public Piece secondPiece;
    public List<Piece> piecesList;
    private List<Piece> templePiecesList;
    public int leftPieces;
    public int [][] piecesIndex = new int [COL_COUNT] [ROW_COUNT];
    public List<Drawable> images;
    public Drawable backImage;
    public static Player playerOne, playerTwo;
    private Player currentPlayer;
    private boolean isFirstPlayer;
    private boolean isRobotPlaying;
    private GameService gameService;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1233:
                    checkPieces();
                    break;
            }
        }
    };

    private MemoryGameSQLiteOpenHelper dbHelper;
    private PopupWindow scorePopupWindow;
    private View popupLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        playerScoreText1 = (TextView) findViewById(R.id.scorePlayer1);
        playerScoreText1.setBackgroundResource(R.drawable.textviewborder);
        playerScoreText2 = (TextView) findViewById(R.id.scorePlayer2);
        playerScoreText2.setBackgroundResource(R.drawable.textviewborder);

        LayoutInflater inflater = LayoutInflater.from(this);
        popupLayout = inflater.inflate(R.layout.popup_bestscores, null);
        scorePopupWindow = new PopupWindow(popupLayout,LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        Button btn_dismiss = (Button) popupLayout.findViewById(R.id.btn_dismiss);
        btn_dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scorePopupWindow.dismiss();
                scorePopupWindow.setFocusable(false);
                showOptionDialog();
            }
        });
        initialGameView();
        dbHelper = new MemoryGameSQLiteOpenHelper(MainActivity.this);
        AgentApplication.getInstance().addActivity(this);
    }

    private void initialGameView() {
        gameTable = (TableLayout) findViewById(R.id.gameViewTable);
        playerOne = new HumanPlayer();
        playerTwo = new RobotPlayer();
        currentPlayer = playerOne;
        firstPiece = null;
        secondPiece = null;
        piecesList = new ArrayList<>();
        templePiecesList = new ArrayList<>();
        isRobotPlaying = false;
        gameService = new GameService();
        loadImages();
        piecesIndex = gameService.getPiecesIndex(ROW_COUNT, COL_COUNT);
        backImage =  getResources().getDrawable(R.drawable.verso);
        showOptionDialog();

        leftPieces = COL_COUNT*ROW_COUNT;

        for (int x = 0; x < ROW_COUNT; x++)
        {
            gameTable.addView(createRow(x));
        }
        templePiecesList.addAll(piecesList);
        updateScoresTexts();
    }
    private void startNewGame() {
        isFirstPlayer = true;
        firstPiece = null;
        secondPiece = null;
        leftPieces = COL_COUNT*ROW_COUNT;
        currentPlayer = playerOne;
        piecesIndex = gameService.getPiecesIndex(ROW_COUNT, COL_COUNT);
        templePiecesList.clear();
        templePiecesList.addAll(piecesList);
        for(Piece piece : piecesList) {
            piece.button.setVisibility(View.VISIBLE);
            piece.button.setBackgroundDrawable(backImage);
        }
        playerScoreText1.setTextColor(Color.RED);
        updateScoresTexts();
    }

    public void showOptionDialog() {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DialogFragment newFragment = new OptionDialogFragment();
        newFragment.show(ft, "dialog");
    }

    @Override
    public void onSelectItem(int item)
    {
        switch (item) {
            case 0:
                showPlayerSettingDialog();
                break;
            case 1:
                playerOne = new HumanPlayer();
                playerTwo = new RobotPlayer();
                startNewGame();
                break;
            case 2:
                //afficher les scores
                showBestScores();
                break;
            case 3:
                AgentApplication.getInstance().onTerminate();
                break;
            default:
                break;
        }
    }

    public void showPlayerSettingDialog() {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DialogFragment newFragment = new PlayerSettingDialogFragment();
        newFragment.show(ft, "dialog");
    }
    @Override
    public void onPlayerSetting(String name1, String name2) {
        playerOne = new HumanPlayer(name1);
        playerTwo = new HumanPlayer(name2);
        startNewGame();
    }

    public void showBestScores(){
        scorePopupWindow.setAnimationStyle(R.style.PopupAnimation);
        scorePopupWindow.showAtLocation(findViewById(R.id.overAllView), Gravity.NO_GRAVITY, 0, 0);
        List<Pair<String,Integer>> bestScores = dbHelper.findBestScore();
        if(bestScores.size()>0){
            TextView playerName1 = (TextView)popupLayout.findViewById(R.id.playerName1);
            playerName1.setText(bestScores.get(0).first);
            TextView score1 = (TextView)popupLayout.findViewById(R.id.playerScore1);
            score1.setText(bestScores.get(0).second.toString());
        }
        if(bestScores.size()>1){
            TextView playerName2 = (TextView)popupLayout.findViewById(R.id.playerName2);
            playerName2.setText(bestScores.get(1).first);
            TextView score2 = (TextView)popupLayout.findViewById(R.id.playerScore2);
            score2.setText(bestScores.get(1).second.toString());
        }
        if(bestScores.size()>2){
            TextView playerName3 = (TextView)popupLayout.findViewById(R.id.playerName3);
            playerName3.setText(bestScores.get(2).first);
            TextView score3 = (TextView)popupLayout.findViewById(R.id.playerScore3);
            score3.setText(bestScores.get(2).second.toString());
        }
        if(bestScores.size()>3) {
            TextView playerName4 = (TextView) popupLayout.findViewById(R.id.playerName4);
            playerName4.setText(bestScores.get(3).first);
            TextView score4 = (TextView) popupLayout.findViewById(R.id.playerScore4);
            score4.setText(bestScores.get(3).second.toString());
        }
        if(bestScores.size()>4) {
            TextView playerName5 = (TextView) popupLayout.findViewById(R.id.playerName5);
            playerName5.setText(bestScores.get(4).first);
            TextView score5 = (TextView) popupLayout.findViewById(R.id.playerScore5);
            score5.setText(bestScores.get(4).second.toString());
        }
        scorePopupWindow.setFocusable(true);
        scorePopupWindow.update();

    }

    private void updateScoresTexts() {

        playerScoreText1.setText(this.playerOne.getName() + ": " + this.playerOne.getScore());
        playerScoreText2.setText(this.playerTwo.getName() + ": " + this.playerTwo.getScore());
    }


    private TableRow createRow(int x)
    {
        TableRow row = new TableRow(gameTable.getContext());
        row.setHorizontalGravity(Gravity.CENTER);
        for(int y = 0; y < COL_COUNT; y++) {
            Button button = createImageButton(x, y);
            row.addView(button);
            Piece piece = new Piece(button, x, y);

            piece.setIndex(piecesIndex[x][y]);
            piece.setDrawable(images.get(piecesIndex[x][y]));
            piecesList.add(piece);

            Log.i("piecesList: " , " x: " + piece.x + " y: " + piece.y + " index: " + piece.index +
            " image " + images.get(piecesIndex[x][y]).toString());
        }
        return row;
    }

    private Button createImageButton(int x, int y) {
        final Button button = new Button(this);
        button.setBackgroundDrawable(backImage);
        button.setId(100*x + y);
        if(isRobotPlaying) {
            button.setEnabled(false);
        }
        else {
            button.setEnabled(true);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstPiece!=null && secondPiece != null){
                    return;
                }
                int id = v.getId();
                int x = id/100;
                int y = id%100;

                turnPiece((Button) v, x, y);
            }
        });
        return button;
    }

    private void turnPiece(Button button, int x, int y) {
        int location = Piece.getLocation(x, y);
        Log.i("turnPiece", " location:" + location + " x: " + x + " y: "+y);
        //button.setBackgroundDrawable(piecesList.get(location).image);
        //button.setBackgroundDrawable(images.get(location));
        button.setBackgroundDrawable(images.get(piecesIndex[x][y]));
        if(firstPiece == null){
            firstPiece = piecesList.get(y+x*COL_COUNT);
        }
        else{
            if(firstPiece.x == x && firstPiece.y == y){
                return; //the user pressed the same piece
            }
            secondPiece = piecesList.get(y+x*COL_COUNT);

            Timer timer = new Timer(false);
            timer.schedule(new TimerTask()
            {
                public void run()
                {
                    handler.sendEmptyMessage(0x1233);
                }
            }, 1000);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.option_menu:
                showOptionDialog();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkPieces(){
        if(piecesIndex[secondPiece.x][secondPiece.y] == piecesIndex[firstPiece.x][firstPiece.y]){
            firstPiece.button.setEnabled(false);
            secondPiece.button.setEnabled(false);
            leftPieces -= 2;
            templePiecesList.remove(firstPiece);
            templePiecesList.remove(secondPiece);
            if(isFirstPlayer) {
                playerOne.increaseScore();
            }
            else {
                playerTwo.increaseScore();
            }
            updateScoresTexts();
        }
        else {
            secondPiece.button.setBackgroundDrawable(backImage);
            firstPiece.button.setBackgroundDrawable(backImage);
            changePlayer();
        }

        firstPiece = null;
        secondPiece = null;
        Log.i("checkPieces","checkPieces");


        if(leftPieces <= 0) {
            //enregistrer le score du gagnant dans la base
            saveScore();
            gameFinished();
        }
    }

    private void changePlayer() {
        isFirstPlayer = !isFirstPlayer;
        if(currentPlayer == playerOne) {
            currentPlayer = playerTwo;

            playerScoreText2.setTextColor(Color.RED);
            playerScoreText1.setTextColor(Color.BLACK);
        }
        else {
            currentPlayer = playerOne;
            playerScoreText1.setTextColor(Color.RED);
            playerScoreText2.setTextColor(Color.BLACK);
        }
        isRobotPlaying = currentPlayer instanceof RobotPlayer;

       // Toast.makeText(this, currentPlayer.getName(), Toast.LENGTH_LONG).show();
        currentPlayer.choosePiece();
    }

    @Override
    public void update(Observable object, Object arg) {
        Toast.makeText(this, "Robot chose", Toast.LENGTH_LONG).show();
    }

    private void saveScore() {
        if(playerOne.getScore() > playerTwo.getScore()) {
            dbHelper.insert(playerOne.getName(),playerOne.getScore());
        }
        else if(playerOne.getScore() < playerTwo.getScore()) {
            dbHelper.insert(playerTwo.getName(),playerTwo.getScore());
        }
        else {
            dbHelper.insert(playerOne.getName(),playerOne.getScore());
            dbHelper.insert(playerTwo.getName(),playerTwo.getScore());
        }
    }

    public void gameFinished() {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        DialogFragment newFragment = new GameFinishDialogFragment();

        newFragment.show(ft, "dialog");
    }
    @Override
    public void onSelectFinishItem(int item) {
        switch (item) {
            case 0:
                playerOne.setScore(0);
                playerTwo.setScore(0);
                startNewGame();
                break;
            case 1:
                showOptionDialog();
                break;
            case 2:
                AgentApplication.getInstance().onTerminate();
                break;
            default:
                break;
        }
    }

    private void loadImages() {
        images = new ArrayList<>();
        images.add(getResources().getDrawable(R.drawable.g_bag));
        images.add(getResources().getDrawable(R.drawable.g_bird));
        images.add(getResources().getDrawable(R.drawable.g_chiken));
        images.add(getResources().getDrawable(R.drawable.g_dog));
        images.add(getResources().getDrawable(R.drawable.g_doll));
        images.add(getResources().getDrawable(R.drawable.g_fish));
        images.add(getResources().getDrawable(R.drawable.g_flour));
        images.add(getResources().getDrawable(R.drawable.g_jewelry));
        images.add(getResources().getDrawable(R.drawable.g_juice));
        images.add(getResources().getDrawable(R.drawable.g_mouse));
        images.add(getResources().getDrawable(R.drawable.g_orange));
        images.add(getResources().getDrawable(R.drawable.g_rabbit));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i("Back:" ,"backKey pressed");
           AgentApplication.getInstance().onTerminate();
            return true;
        }
        //return super.onKeyDown(keyCode,event);
        return false;
    }
   /* @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Log.i("Back","backKey pressed");
        super.onBackPressed();
        AgentApplication.getInstance().onTerminate();

    }*/

    //It doesn't work
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        String screen = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "Landscape" :"Portrait";
        Toast.makeText(this, "Change orientation" + "\n after change: "
        + screen, Toast.LENGTH_LONG).show();
    }

}
