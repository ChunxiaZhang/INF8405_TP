package com.memorygame.example.zoe.tp1_memorygame;

import android.os.Looper;
import android.os.Message;
import android.os.Handler;

/**
 * Created by Zoe on 15-02-03.
 */
public class HumanPlayer extends Player{

    public HumanPlayer(String name,GameActivity gameActivity) {
        this.playerName = name;
        this.score = 0;
        this.gameActivity = gameActivity;
    }

    public void run() {
        this.setName("HumanPlayer_Thread");
        Looper.prepare();

        this.playerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Piece pieceToTurn = (Piece)msg.obj;
                if(firstPiece == null){
                    firstPiece = pieceToTurn;
                } else {
                    if (!(firstPiece.getNumRow() == pieceToTurn.getNumRow() && firstPiece.getNumCol() == pieceToTurn.getNumCol())) {
                        //the user didn't press the same piece
                        secondPiece = pieceToTurn;
                    }

                }
                if(firstPiece!=null && secondPiece!=null){
                    boolean isMatch = checkPieces();
                    Message matchMsg = new Message();
                    matchMsg.obj = isMatch;
                    matchMsg.arg1 = firstPiece.getIdx();
                    matchMsg.arg2 = secondPiece.getIdx();
                    firstPiece = null;
                    secondPiece = null;
                    gameActivity.mainHandler.sendMessageDelayed(matchMsg,1000);
                }
            }
        };
        Looper.loop();
    }


}
