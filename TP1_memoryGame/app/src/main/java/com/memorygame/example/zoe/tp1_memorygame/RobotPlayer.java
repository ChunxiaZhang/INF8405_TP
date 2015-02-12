package com.memorygame.example.zoe.tp1_memorygame;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

/**
 * Created by Zoe on 15-02-03.
 */
public class RobotPlayer extends Player{

    public RobotPlayer() {
        this.name = "Robot";
        this.score = 0;
    }

    @Override
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
