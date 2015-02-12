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
    private static final int ROW_COUNT = 6;
    private static final int COL_COUNT = 4;
    private TextView playerScoreText1, playerScoreText2;
    private TableLayout gameTable;
    private Piece firstPiece;
    private Piece secondPiece;
    private List<Piece> piecesList;
    private List<Piece> piecesLeft;
    private List<Piece> piecesTurned;

    private List<List<Integer>> piecesImgClasses;
    public List<Drawable> images;
    public Drawable backImage;
    private boolean isFirstPlayer;
    private boolean isRobotPlaying;
    private Player playerOne, playerTwo;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    if(!checkPieces()) {
                        changePlayer();
                    }
                    if(!isFirstPlayer&&isRobotPlaying){
                        List<Piece> piecesChosen = playerTwo.choosePiece(piecesLeft,piecesTurned);
                        robotTurnPiece(piecesChosen);
                    }
                    break;

            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //create two players
        String name1 = getIntent().getStringExtra("playerOneName");
        playerOne = new HumanPlayer(name1);
        isRobotPlaying = getIntent().getBooleanExtra("robotPlayMode",false);
        if(isRobotPlaying){
            playerTwo = new RobotPlayer();
        }else{
            String name2 = getIntent().getStringExtra("playerTwoName");
            playerTwo = new HumanPlayer(name2);
        }

        //initialize the scores textviews and their border
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
        piecesLeft = new ArrayList<>();
        piecesTurned = new ArrayList<>();
        loadImages();
        piecesImgClasses = getPiecesClasses(ROW_COUNT, COL_COUNT);

        if(gameTable.getChildCount()!=0){
            gameTable.removeAllViewsInLayout();
        }
        for (int x = 0; x < ROW_COUNT; x++)
        {
            gameTable.addView(createRow(x));
        }

        firstPiece = null;
        secondPiece = null;
        piecesLeft.addAll(piecesList);

        for(Piece piece : piecesList) {
            piece.makeButtonVisible();
            //piece.button.setBackgroundDrawable(backImage);
        }
        playerScoreText1.setTextColor(Color.RED);
        updateScoresTexts();
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
        backImage =  getResources().getDrawable(R.drawable.verso);
    }

    public List<List<Integer>> getPiecesClasses(int row, int col) {
        List<List<Integer>> imgClasses = new ArrayList<>();
        for(int x = 0; x < row; x++){
            imgClasses.add(new ArrayList<Integer>());
            for(int y = 0; y < col; y++){
                imgClasses.get(x).add(-1);
            }
        }

        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < row*col/2; i++) {
            list.add(new Integer(i));
        }
        list.addAll(list);

        Collections.shuffle(list);

        Iterator<Integer> iterator = list.iterator();
        for(int x = 0; x < row; x++){
            for(int y = 0; y < col; y++){
                imgClasses.get(x).set(y,iterator.next());
            }
        }
        return imgClasses;
    }

    private TableRow createRow(int x)
    {
        TableRow row = new TableRow(gameTable.getContext());
        row.setHorizontalGravity(Gravity.CENTER);
        for(int y = 0; y < COL_COUNT; y++) {
            Button button = createImageButton(x, y);
            row.addView(button);
            Piece piece = new Piece(x, y, piecesImgClasses.get(x).get(y), images.get(piecesImgClasses.get(x).get(y)), button);
            piecesList.add(piece);
        }
        return row;
    }

    private Button createImageButton(int numRow, int numCol) {
        final Button button = new Button(this);
        button.setBackgroundDrawable(backImage);
        button.setId(COL_COUNT*numRow + numCol);
//        if(isRobotPlaying && !isFirstPlayer) {
//            button.setEnabled(false);
//        }
//        else {
//            button.setEnabled(true);
//        }
        button.setEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstPiece!=null && secondPiece != null){
                    return;
                }
                int id = v.getId();
                int numRow = id/COL_COUNT;
                int numCol = id%COL_COUNT;

                humanTurnPiece((Button) v, numRow, numCol);
            }
        });
        return button;
    }

    private void humanTurnPiece(Button button, int numRow, int numCol) {
        int numPiece = COL_COUNT*numRow+numCol;
        button.setBackgroundDrawable(images.get(piecesImgClasses.get(numRow).get(numCol)));
        if(firstPiece == null){
            firstPiece = piecesList.get(numPiece);
        }
        else{
            if(firstPiece.getNumRow() == numRow && firstPiece.getNumCol() == numCol){
                return; //the user pressed the same piece
            }
            secondPiece = piecesList.get(numPiece);

            Timer timer = new Timer(false);
            timer.schedule(new TimerTask()
            {
                public void run()
                {
                    handler.sendEmptyMessage(0x001);
                }
            }, 2000);
        }
    }

    private void robotTurnPiece(List<Piece> piecesChosen){
        firstPiece = piecesChosen.get(0);
        firstPiece.showFrontImage();
        secondPiece = piecesChosen.get(1);
        secondPiece.showFrontImage();
        Timer timer = new Timer(false);
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                handler.sendEmptyMessage(0x001);
            }
        }, 1000);
    }

    public boolean checkPieces(){
        boolean isMatch;
        if(firstPiece.getImgClass() == secondPiece.getImgClass()){
            firstPiece.enableButton(false);
            secondPiece.enableButton(false);
            piecesLeft.remove(firstPiece);
            piecesLeft.remove(secondPiece);

            if(isFirstPlayer) {
                playerOne.increaseScore();
            }
            else {
                playerTwo.increaseScore();
            }
            updateScoresTexts();
            isMatch = true;
        }
        else {
            if(isRobotPlaying) {
                if(!piecesTurned.contains(firstPiece)){
                    piecesTurned.add(firstPiece);
                }
                if(!piecesTurned.contains(secondPiece)){
                    piecesTurned.add(secondPiece);
                }
            }
            firstPiece.showBackImage(backImage);
            secondPiece.showBackImage(backImage);
            isMatch = false;
        }

        firstPiece = null;
        secondPiece = null;
        if(piecesLeft.size() <= 0) {
            //enregistrer le score du gagnant dans la base
            saveScore();
            gameFinished();
        }
        return isMatch;
    }

    private void changePlayer() {
        if(isFirstPlayer) {
            playerScoreText2.setTextColor(Color.RED);
            playerScoreText1.setTextColor(Color.BLACK);
        }
        else {
            playerScoreText1.setTextColor(Color.RED);
            playerScoreText2.setTextColor(Color.BLACK);
        }
        isFirstPlayer = !isFirstPlayer;
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
