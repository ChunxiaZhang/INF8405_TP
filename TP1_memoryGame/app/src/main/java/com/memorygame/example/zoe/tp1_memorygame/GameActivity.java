package com.memorygame.example.zoe.tp1_memorygame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Zoe on 15-02-06.
 */
public class GameActivity extends ActionBarActivity implements GameFinishDialogFragment.SelectFinishItemListener{
    private TextView playerScoreText1, playerScoreText2;
    private TableLayout gameTable;
    private Piece firstPiece;
    private Piece secondPiece;
    private List<Piece> piecesList;
    private List<Piece> leftPiecesList;
    private List<Piece> piecesRobotTurned;

    public int [][] piecesIndex = new int [MainActivity.COL_COUNT] [MainActivity.ROW_COUNT];
    public List<Drawable> images;
    public Drawable backImage;
   // private Player currentPlayer;
    private boolean isFirstPlayer;
    private boolean isRobotPlaying;
    private Player playerOne, playerTwo;

    private Thread thread;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1233:
                    if(checkPieces()) {
                        changePlayer();
                        if(!isFirstPlayer&&isRobotPlaying){
                            robotChoosePiece();
                        }
//                        if(changePlayer()) {
//                            Log.i("handler:", "robotChoosePiece for the first time");
//                            robotChoosePiece();
//                        }
                    }
                    break;
                case 0x123:
                    if(firstPiece == null) {
                        firstPiece = piecesList.get(msg.arg2+msg.arg1*MainActivity.COL_COUNT);
                        firstPiece.button.setBackgroundDrawable(images.get(piecesIndex[msg.arg1][msg.arg2]));
                        Log.i("handler:", "robotChoosePiece for the second time");
                        robotChoosePiece();
                    }
                    else {
                        Log.i("handler", "turn the secondPiece");
                        secondPiece = piecesList.get(msg.arg2+msg.arg1*MainActivity.COL_COUNT);
                        secondPiece.button.setBackgroundDrawable(images.get(piecesIndex[msg.arg1][msg.arg2]));
                        try {
                            Log.i("handler:", "sleep(500)");
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(checkPieces()) {
                            Log.i("handler: ", "need to changer player");
                            changePlayer();
                        }
                    }
                    break;

            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String name1 = getIntent().getStringExtra("playerOneName");
        playerOne = new HumanPlayer(name1);
        isRobotPlaying = getIntent().getBooleanExtra("robotPlayMode",false);
        if(isRobotPlaying){
            playerTwo = new RobotPlayer();
        }else{
            String name2 = getIntent().getStringExtra("playerTwoName");
            playerTwo = new HumanPlayer(name2);
        }

        playerScoreText1 = (TextView) findViewById(R.id.scorePlayer1);
        playerScoreText1.setBackgroundResource(R.drawable.textviewborder);
        playerScoreText2 = (TextView) findViewById(R.id.scorePlayer2);
        playerScoreText2.setBackgroundResource(R.drawable.textviewborder);

        gameTable = (TableLayout) findViewById(R.id.gameViewTable);


        initialGame();
    }

    private void initialGame() {
        isFirstPlayer = true;
        piecesList = new ArrayList<>();
        leftPiecesList = new ArrayList<>();
        piecesRobotTurned = new ArrayList<>();
        loadImages();
        backImage =  getResources().getDrawable(R.drawable.verso);
        piecesIndex = getPiecesIndex(MainActivity.ROW_COUNT, MainActivity.COL_COUNT);
        if(gameTable.getChildCount()!=0){
            gameTable.removeAllViewsInLayout();
        }
        for (int x = 0; x < MainActivity.ROW_COUNT; x++)
        {
            gameTable.addView(createRow(x));
        }

        firstPiece = null;
        secondPiece = null;
        piecesIndex = getPiecesIndex(MainActivity.ROW_COUNT, MainActivity.COL_COUNT);
        leftPiecesList.addAll(piecesList);

        for(Piece piece : piecesList) {
            piece.button.setVisibility(View.VISIBLE);
            piece.button.setBackgroundDrawable(backImage);
        }
        playerScoreText1.setTextColor(Color.RED);
        updateScoresTexts();
    }

    public int[][] getPiecesIndex(int row, int col) {
        int[][] index = new int [row][col];

        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < row*col/2; i++) {
            list.add(new Integer(i));
        }
        list.addAll(list);

        Collections.shuffle(list);

        Iterator<Integer> iterator = list.iterator();
        for(int x = 0; x < row; x++){
            for(int y = 0; y < col; y++){
                index[x][y] = iterator.next();

            }
        }
        return index;
    }

    private void loadImages() {
        images = new ArrayList<>();
        images.add(getResources().getDrawable(R.drawable.g_image1));
        images.add(getResources().getDrawable(R.drawable.g_image2));
        images.add(getResources().getDrawable(R.drawable.g_image3));
        images.add(getResources().getDrawable(R.drawable.g_image4));
        images.add(getResources().getDrawable(R.drawable.g_image5));
        images.add(getResources().getDrawable(R.drawable.g_image6));
        images.add(getResources().getDrawable(R.drawable.g_image7));
        images.add(getResources().getDrawable(R.drawable.g_image8));
        images.add(getResources().getDrawable(R.drawable.g_image9));
        images.add(getResources().getDrawable(R.drawable.g_image10));
        images.add(getResources().getDrawable(R.drawable.g_image11));
        images.add(getResources().getDrawable(R.drawable.g_image12));
    }

    private TableRow createRow(int x)
    {
        TableRow row = new TableRow(gameTable.getContext());
        row.setHorizontalGravity(Gravity.CENTER);
        for(int y = 0; y < MainActivity.COL_COUNT; y++) {
            Button button = createImageButton(x, y);
            row.addView(button);
            Piece piece = new Piece(button, x, y);

            piece.setIndex(piecesIndex[x][y]);
            piece.setDrawable(images.get(piecesIndex[x][y]));
            piecesList.add(piece);

        }
        return row;
    }

    private Button createImageButton(int x, int y) {
        final Button button = new Button(this);
        button.setBackgroundDrawable(backImage);
        button.setId(100*x + y);
        if(isRobotPlaying && !isFirstPlayer) {
            Log.i("createImageButton: ", "robot is playing");
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
            firstPiece = piecesList.get(location);
        }
        else{

            if(firstPiece.x == x && firstPiece.y == y){
                return; //the user pressed the same piece
            }
            secondPiece = piecesList.get(location);

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

    public boolean checkPieces(){
        boolean isNeedChangerPlayer = false;
        if(piecesIndex[secondPiece.x][secondPiece.y] == piecesIndex[firstPiece.x][firstPiece.y]){
            firstPiece.button.setEnabled(false);
            firstPiece.isMatched = true;
            secondPiece.button.setEnabled(false);
            secondPiece.isMatched = true;

            leftPiecesList.remove(firstPiece);
            leftPiecesList.remove(secondPiece);

            if(isFirstPlayer) {
                playerOne.increaseScore();
            }
            else {
                playerTwo.increaseScore();
            }
            updateScoresTexts();
            isNeedChangerPlayer = false;
            Log.i("checkPiece: ", "two pieces are the same image");
        }
        else {
            Log.i("checkPiece: ", "two pieces are different");
            if(isRobotPlaying && !isFirstPlayer) {
                Log.i("checkPiece: ", "add pieces to the pieces turned by robot");
                piecesRobotTurned.add(firstPiece);
                piecesRobotTurned.add(secondPiece);
            }

            secondPiece.button.setBackgroundDrawable(backImage);

            firstPiece.button.setBackgroundDrawable(backImage);
            Log.i("checkPieces: ", "turn to backImage");
            isNeedChangerPlayer = true;
            //changePlayer();
        }

        firstPiece = null;
        secondPiece = null;
        Log.i("checkPieces","checkPieces");
        if(leftPiecesList.size() <= 0) {
            Log.i("checkPieces: ", "game finished");
            //enregistrer le score du gagnant dans la base
            saveScore();
            gameFinished();
        }
        return isNeedChangerPlayer;
    }

    private void changePlayer() {
        if(isFirstPlayer) {
            Log.i("changePlayer: ", "to secondPlayer");
            playerScoreText2.setTextColor(Color.RED);
            playerScoreText1.setTextColor(Color.BLACK);
        }
        else {
            Log.i("changePlayer: ", "to firstPlayer");
            playerScoreText1.setTextColor(Color.RED);
            playerScoreText2.setTextColor(Color.BLACK);
        }
        isFirstPlayer = !isFirstPlayer;

        //return (MainActivity.isRobotPlaying && !isFirstPlayer);
    }

    public void robotChoosePiece() {
            Timer timer = new Timer(false);
            timer.schedule(new TimerTask()
            {
                public void run()
                {

                    int x, y;
                    Piece piece;
                    Random rand = new Random();
                    //do{
                    x = rand.nextInt(MainActivity.ROW_COUNT);
                    y = rand.nextInt(MainActivity.COL_COUNT);
                    piece = piecesList.get(y+x*MainActivity.COL_COUNT);
                    //}while(leftPiecesList.contains(piece));

                    Message msg = new Message();
                    msg.what = 0x123;
                    msg.arg1 = x;
                    msg.arg2 = y;
                    handler.sendMessage(msg);
                }
            }, 500);
    }

    public void robotPlaying() {
        Log.i("robotPlaying:", "start playing");
        int x, y;
        Piece piece;
        Random rand = new Random();
        //do{
            x = rand.nextInt(MainActivity.ROW_COUNT);
            y = rand.nextInt(MainActivity.COL_COUNT);
            //piece = piecesList.get(y+x*MainActivity.COL_COUNT);
       // }while(!piece.isMatched);

        Log.i("robotPlaying:" , "choose the first piece" + x + y);
        firstPiece = piecesList.get(y+x*MainActivity.COL_COUNT);
        firstPiece.button.setBackgroundDrawable(images.get(piecesIndex[x][y]));
        /*try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }*/

        //do{
            x = rand.nextInt(MainActivity.ROW_COUNT);
            y = rand.nextInt(MainActivity.COL_COUNT);
            //piece = piecesList.get(y+x*MainActivity.COL_COUNT);
        //}while(!piece.isMatched && (firstPiece.x == x && firstPiece.y == y));

        Log.i("robotPlaying:" , "choose the second piece" + x + y);
        secondPiece = piecesList.get(y + x * MainActivity.COL_COUNT);
        secondPiece.button.setBackgroundDrawable(images.get(piecesIndex[x][y]));
        /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }*/
        checkPieces();
        //if(checkPieces()) {
            //changePlayer();
       // }

    }

    private void updateScoresTexts() {

        playerScoreText1.setText(playerOne.getName() + ": " + playerOne.getScore());
        playerScoreText2.setText(playerTwo.getName() + ": " + playerTwo.getScore());
    }

    private void saveScore() {
        if(playerOne.getScore() > playerTwo.getScore()) {
            MainActivity.dbHelper.insert(playerOne.getName(),playerOne.getScore());
        }
        else if(playerOne.getScore() < playerTwo.getScore()) {
            MainActivity.dbHelper.insert(playerTwo.getName(), playerTwo.getScore());
        }
        else {
            MainActivity.dbHelper.insert(playerOne.getName(), playerOne.getScore());
            MainActivity.dbHelper.insert(playerTwo.getName(), playerTwo.getScore());
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
                initialGame();
                break;
            case 1:
                this.finish();
                break;
            case 2:
                AgentApplication.getInstance().onTerminate();
                break;
            default:
                break;
        }
    }


    public Player getPlayerOne(){return playerOne;}
    public Player getPlayerTwo(){return playerTwo;}

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            new AlertDialog.Builder(this).setMessage("Do you want to abandon the current game?")
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent i = new Intent(GameActivity.this, MainActivity.class);
//                            startActivity(i);
//                        }
//                    }).setNegativeButton(android.R.string.no,null).show();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
