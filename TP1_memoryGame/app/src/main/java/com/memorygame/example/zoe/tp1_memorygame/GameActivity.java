package com.memorygame.example.zoe.tp1_memorygame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Zoe on 15-02-06.
 * GameActivity class implements an activity that displays game view,
 * including a TableLayout and two TextView for display scores of the two players.
 * Realize game logic
 */
public class GameActivity extends ActionBarActivity implements GameFinishDialogFragment.SelectFinishItemListener{
    private static final int ROW_COUNT = 6; //Row count of table
    private static final int COL_COUNT = 4; //Column count of table
    private TextView playerScoreText1, playerScoreText2;
    private TableLayout gameTable;
    private List<Piece> piecesList; //All pieces of the table
    private List<Piece> piecesLeft; //All not matched pieces
    private List<Piece> piecesTurned; //All matched pieces

    private List<List<Integer>> piecesImgClasses; // To indicate every element of table has with index of image
    private List<Drawable> images; // Images which will be mapping in the table random
    private Drawable backImage;
    private boolean isFirstPlayer;
    private boolean isRobotPlaying; // boolean for robot mode
    private int nbImgTurned; // The number of pieces turned one round, it's always less than 2

    private Player playerOne, playerTwo;

    /**
     * This Handler used to handle players thread message
     */
    public Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            boolean isMatch = (boolean)msg.obj;
            int idx1 = msg.arg1;
            int idx2 = msg.arg2;
            if(!isRobotPlaying){ //if two human player model just update interface
                updateInterface(isMatch, idx1, idx2);
                nbImgTurned = 0;
            }else{
                if(isFirstPlayer){
                    updateInterface(isMatch, idx1, idx2);
                    //is the turn of robot's to play
                    if(!isFirstPlayer && !piecesLeft.isEmpty()){
                        /**
                         * Send message to robot play thread, let robot choose pieces
                         */
                        playerTwo.playerHandler.sendEmptyMessage(0);
                    } 
                    if(isFirstPlayer) nbImgTurned = 0;
                }else{
                    //If robot chose two pieces than turn the two pieces, after 1 second GameActivity update
                    turnPieceForRobot(isMatch,idx1,idx2);
                }
            }
        }
    };

    /**
     * To handle delay update GameActivity message
     */
    private Handler delayDisplayHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            boolean isMatch = (boolean)msg.obj;
            int idx1 = msg.arg1;
            int idx2 = msg.arg2;
            updateInterface(isMatch, idx1, idx2);
            //is the robot's to play
            if(!isFirstPlayer && !piecesLeft.isEmpty()){
                //Send message to robot play thread let robot choose pieces
                playerTwo.playerHandler.sendEmptyMessage(0);
            }
            if(isFirstPlayer) nbImgTurned = 0;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        AgentApplication.getInstance().addActivity(this);
        //create two players
        String name1 = getIntent().getStringExtra("playerOneName");
        playerOne = new HumanPlayer(name1,this);
        isRobotPlaying = getIntent().getBooleanExtra("robotPlayMode",false);
        if(isRobotPlaying){
            playerTwo = new RobotPlayer(this);
        }else{
            String name2 = getIntent().getStringExtra("playerTwoName");
            playerTwo = new HumanPlayer(name2,this);
        }

        //initialize the scores textviews and their border
        playerScoreText1 = (TextView) findViewById(R.id.scorePlayer1);
        playerScoreText1.setBackgroundResource(R.drawable.textviewborder);
        playerScoreText2 = (TextView) findViewById(R.id.scorePlayer2);
        playerScoreText2.setBackgroundResource(R.drawable.textviewborder);
        gameTable = (TableLayout) findViewById(R.id.gameViewTable);
        initialGame();
        playerOne.start(); // Starts the new Thread of execution.
        playerTwo.start(); // Starts the new Thread of execution.
    }

    private void initialGame() {
        isFirstPlayer = true;
        nbImgTurned = 0;
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

        piecesLeft.addAll(piecesList);

        for(Piece piece : piecesList) {
            piece.makeButtonVisible();
            //piece.button.setBackgroundDrawable(backImage);
        }
        playerScoreText1.setTextColor(Color.RED);
        playerScoreText2.setTextColor(Color.BLACK);
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
        //Initial all list as -1
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
        list.addAll(list); // pair the numbers between 0 and 11

        Collections.shuffle(list); // Get a list pair random numbers between 0 and 11

        //Give pair numbers between 0 and 11 to imgClasses
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
            ImageButton button = createImageButton(x, y);
            row.addView(button);
            Piece piece = new Piece(x, y,x*COL_COUNT+y, piecesImgClasses.get(x).get(y), images.get(piecesImgClasses.get(x).get(y)), backImage, button);
            piecesList.add(piece);
        }
        return row;
    }

    private ImageButton createImageButton(int numRow, int numCol) {
        final ImageButton button = new ImageButton(this);
        button.setBackgroundDrawable(backImage);
        button.setId(COL_COUNT*numRow + numCol);
        button.setEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nbImgTurned<2){
                    int btnId = v.getId();
                    int numRow = btnId/COL_COUNT;
                    int numCol = btnId%COL_COUNT;
                    //Turn image
                    button.setBackgroundDrawable(images.get(piecesImgClasses.get(numRow).get(numCol)));
                    nbImgTurned++;

                    Message humanPlayerMsg = new Message();
                    humanPlayerMsg.obj = piecesList.get(btnId);
                    if(isFirstPlayer){
                        playerOne.playerHandler.sendMessage(humanPlayerMsg);
                    }else{
                        //if the second player is not the robot(which is also a human player)
                        //we can send message for match checking process
                        if(!isRobotPlaying){
                            playerTwo.playerHandler.sendMessage(humanPlayerMsg);
                        }
                    }
                }
            }
        });
        return button;
    }

    /**
     * When players chose two pieces turn pieces back if not matched,
     * or update scores TextView if matched and check if game finished
     */

    public void updateInterface(boolean isMatch, int idx1, int idx2){
        Piece piece1 = piecesList.get(idx1);
        Piece piece2 = piecesList.get(idx2);

        if(isMatch){
            piece1.enableButton(false);
            piece2.enableButton(false);
            piecesLeft.remove(piece1);
            piecesLeft.remove(piece2);
            if(piecesTurned.contains(piece1)){
                piecesTurned.remove(piece1);
            }
            if(piecesTurned.contains(piece2)){
                piecesTurned.remove(piece2);
            }
            updateScoresTexts();
            if(piecesLeft.size() <= 0) {
                gameFinished();
            }
        }else{
            piece1.showBackImage();
            piece2.showBackImage();
            if(isFirstPlayer) {
                playerScoreText2.setTextColor(Color.RED);
                playerScoreText1.setTextColor(Color.BLACK);
            }
            else {
                playerScoreText1.setTextColor(Color.RED);
                playerScoreText2.setTextColor(Color.BLACK);
            }
            if(!piecesTurned.contains(piece1)){
                if(piecesTurned.size()>=4){
                    piecesTurned.remove(0);
                }
                piecesTurned.add(piece1);
            }
            if(!piecesTurned.contains(piece2)){
                if(piecesTurned.size()>=4){
                    piecesTurned.remove(0);
                }
                piecesTurned.add(piece2);
            }
            isFirstPlayer = !isFirstPlayer;
        }
    }

    public void turnPieceForRobot(final boolean isMatch, final int idx1, final int idx2){
        piecesList.get(idx1).showFrontImage(); // Turn on the first piece
        piecesList.get(idx2).showFrontImage(); // Turn on the second piece
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.obj = isMatch;
                msg.arg1 = idx1;
                msg.arg2 = idx2;
                delayDisplayHandler.sendMessage(msg); // Let delay 1 second before update interface
            }
        }, 1000);
    }

    public void updateScoresTexts() {
        playerScoreText1.setText(playerOne.getPlayerName() + ": " + playerOne.getScore());
        playerScoreText2.setText(playerTwo.getPlayerName() + ": " + playerTwo.getScore());
    }

    public void gameFinished() {
        //enregistrer le score du gagnant dans la base
        if(playerOne.getScore() > playerTwo.getScore()) {
            MainActivity.dbHelper.insert(playerOne.getPlayerName(),playerOne.getScore());
        }
        else if(playerOne.getScore() < playerTwo.getScore()) {
            MainActivity.dbHelper.insert(playerTwo.getPlayerName(), playerTwo.getScore());
        }
        else {
            MainActivity.dbHelper.insert(playerOne.getPlayerName(), playerOne.getScore());
            MainActivity.dbHelper.insert(playerTwo.getPlayerName(), playerTwo.getScore());
        }
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        DialogFragment newFragment = new GameFinishDialogFragment();
        newFragment.show(ft, "dialog");
    }

    /**
     * GameFinishDialogFragment listener, react according player choice
     * @param item
     * If item is 0 : try again game with the same players
     *            1 : close this activity, return to MainActivity to choose play model
     *            2 : exit the application
     */
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
    public List<Piece> getPiecesLeft(){return piecesLeft;}
    public List<Piece> getPiecesTurned(){return piecesTurned;}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this).setMessage("Do you want to abandon the current game?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exit Confirm")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GameActivity.this.finish();
                        }
                    }).setNegativeButton(android.R.string.no, null).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
