package com.mymemorygame.example.zoe.mymemorygame;

import android.content.res.Configuration;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;


import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity implements PlayerSettingDialogFragment.PlayerSettingListener,
        OptionDialogFragment.SelectItemListener, GameFinishDialogFragment.SelectFinishItemListener {

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
    private boolean isFirstPlayer;
    private boolean isPlayWithRobot;
    private GameService gameService;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1233:
                    checkPieces();
                    //for(Piece piece: templePiecesList) {
                       // piece.button.setEnabled(true);
                    //}
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialGameView();
        AgentApplication.getInstance().addActivity(this);
    }

    private void initialGameView() {
        gameTable = (TableLayout) findViewById(R.id.gameViewTable);
        playerOne = new Player();
        playerTwo = new Player("Robot");
        isPlayWithRobot = true;
        piecesList = new ArrayList<>();
        templePiecesList = new ArrayList<>();
        isPlayWithRobot = true;
        gameService = new GameService();
        loadImages();
        piecesIndex = gameService.getPiecesIndex(ROW_COUNT, COL_COUNT);
        backImage =  getResources().getDrawable(R.drawable.verso);
        showOptionDialog();
        firstPiece = null;
        leftPieces = COL_COUNT*ROW_COUNT;

        for (int x = 0; x < ROW_COUNT; x++)
        {
            gameTable.addView(createRow(x));
        }
        templePiecesList.addAll(piecesList);
        showScoresTexts();
    }
    private void startNewGame() {
        isFirstPlayer = true;
        piecesIndex = gameService.getPiecesIndex(ROW_COUNT, COL_COUNT);
        templePiecesList.clear();
        templePiecesList.addAll(piecesList);
        for(Piece piece : piecesList) {
            piece.button.setVisibility(View.VISIBLE);
            piece.button.setBackgroundDrawable(backImage);
        }

        leftPieces = COL_COUNT*ROW_COUNT;
        firstPiece = null;
        Toast.makeText(this, playerOne.getName(), Toast.LENGTH_LONG).show();
        showScoresTexts();
    }

    void showOptionDialog() {
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
                isPlayWithRobot = true;
                playerOne.setName("You");
                playerOne.setScore(0);
                playerTwo.setName("Robot");
                playerTwo.setScore(0);
                startNewGame();
                break;
            case 2:
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
        playerOne.setName(name1);
        playerOne.setScore(0);
        playerTwo.setName(name2);
        playerTwo.setScore(0);
        isPlayWithRobot = false;
        startNewGame();
    }


    private void showScoresTexts() {
        playerScoreText1 = (TextView) findViewById(R.id.scorePlayer1);
        playerScoreText2 = (TextView) findViewById(R.id.scorePlayer2);

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
            showScoresTexts();
        }
        else {
            secondPiece.button.setBackgroundDrawable(backImage);
            firstPiece.button.setBackgroundDrawable(backImage);
        }

        firstPiece = null;
        secondPiece = null;
        Log.i("checkPieces","checkPieces");
        changePlayer();

        if(leftPieces <= 0) {
            gameFinished();
        }
    }

    private void changePlayer() {
        isFirstPlayer = !isFirstPlayer;
        String message;
        if(isFirstPlayer) {
            message = playerOne.getName();
        }
        else {
            message = playerTwo.getName();
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        if(isPlayWithRobot && isFirstPlayer) {
            //for(Piece piece: templePiecesList) {
             //   piece.button.setEnabled(false);
          //  }

            robotPlay();

           /* Log.i("changePlayer", "It's Robot's turn!");
            handler.sendEmptyMessage(0x123);
            Timer timer = new Timer(false);
            timer.schedule(new TimerTask()
            {
                public void run()
                {
                    Log.i("changePlayer", "Timer schedule send message!");
                    handler.sendEmptyMessage(0x123);
                }
            }, 2000);*/

        }

    }

    private Piece choosePieceRandom() {
        Random rand = new Random();
        int location;
        do {
            location = rand.nextInt(ROW_COUNT*COL_COUNT);
        }while (templePiecesList.contains(piecesList.get(location)));
        return piecesList.get(location);
    }
    public void robotPlay() {
        Log.i("robotPlay: ", "start choose the first piece");
        firstPiece = choosePieceRandom();
        Log.i("robotPlay: ", "start choose the second piece");
        secondPiece = choosePieceRandom();
        Timer timer = new Timer(false);
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                Log.i("Timer run to ", "checkPieces");
                handler.sendEmptyMessage(0x1233);
            }
        }, 1000);

       /* Log.i("handler", "Robot is playing!");

        Log.i("robotPlay", "firstPiece is null");
        firstPiece = choosePieceRandom();
        Log.i("robotPlay", "choose the firstPiece: " + firstPiece.x + "  " + firstPiece.y);
        firstPiece.button.setBackgroundDrawable(images.get(pieces[firstPiece.x][firstPiece.y]));

        secondPiece = choosePieceRandom();
        secondPiece.button.setBackgroundDrawable(images.get(pieces[secondPiece.x][secondPiece.y]));
        Timer timer = new Timer(false);
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                 handler.sendEmptyMessage(0x1233);
            }
        }, 1000);*/


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


    //It doesn't work
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        String screen = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "Landscape" :"Portrait";
        Toast.makeText(this, "Change orientation" + "\n after change: "
        + screen, Toast.LENGTH_LONG).show();
    }

}
