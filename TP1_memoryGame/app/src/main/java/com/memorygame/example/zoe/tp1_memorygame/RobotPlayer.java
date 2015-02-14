package com.memorygame.example.zoe.tp1_memorygame;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Zoe on 15-02-03.
 */
public class RobotPlayer extends Player{

    public RobotPlayer(GameActivity gameActivity) {
        this.playerName = "Robot";
        this.score = 0;
        this.gameActivity = gameActivity;
    }

    public void run(){
        this.setName("RobotPlayer_Thread");
        Looper.prepare();
        this.playerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<Piece> piecesLeft = gameActivity.getPiecesLeft();
                List<Piece> piecesTurned = gameActivity.getPiecesTurned();
                List<Piece> piecesChosen = choosePiece(piecesLeft,piecesTurned);
                firstPiece = piecesChosen.get(0);
                secondPiece = piecesChosen.get(1);
                boolean isMatch = checkPieces();
                Message matchMsg = new Message();
                matchMsg.obj = isMatch;
                matchMsg.arg1 = piecesChosen.get(0).getIdx();
                matchMsg.arg2 = piecesChosen.get(1).getIdx();

                /**
                 * After choose the second piece send message to mainHandler
                 * and let delay 1 second before update interface?????????
                 */
                gameActivity.mainHandler.sendMessageDelayed(matchMsg,1000);
            }
        };
        Looper.loop();
    }

    public List<Piece> choosePiece(List<Piece> piecesLeft,List<Piece> piecesTurned){
        List<Integer> listClasses = new ArrayList<>();
        for(int i = 0;i<piecesTurned.size();i++){
            listClasses.add(piecesTurned.get(i).getImgClass());
        }
        //check if a pair of match cards exists in the list piecesTurned
        List<Piece> piecesChosen = new ArrayList<>();
        for(int i = 0;i<listClasses.size();i++){
            List<Integer> listCopied = new ArrayList<>();
            listCopied.addAll(listClasses);
            listCopied.set(i, null);
            if(listCopied.contains(listClasses.get(i))){
                piecesChosen.add(piecesTurned.get(i));
                int idxMatch = listCopied.indexOf(listClasses.get(i));
                piecesChosen.add(piecesTurned.get(idxMatch));
                break;
            }
        }
        //choose 2 cards randomly from the list piecesLeft
        if(piecesChosen.isEmpty()){
            Random r = new Random();
            int nbPiecesLeft = piecesLeft.size();
            int idx1 = r.nextInt(nbPiecesLeft);
            piecesChosen.add(piecesLeft.get(idx1));
            int idx2 = -1;
            while(idx1==idx2 || idx2<0){
                idx2 = r.nextInt(nbPiecesLeft);
            }
            piecesChosen.add(piecesLeft.get(idx2));
        }
        return piecesChosen;
    }

}
