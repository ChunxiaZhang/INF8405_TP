package com.memorygame.example.zoe.tp1_memorygame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.zip.Inflater;

/**
 * Created by Zoe on 15-02-02.
 */
public class GameFinishDialogFragment extends DialogFragment {

    public interface SelectFinishItemListener {
        void onSelectFinishItem(int item);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        GameActivity gameActivity = (GameActivity)getActivity();

        String title;
        if(gameActivity.getPlayerOne().getScore() > gameActivity.getPlayerTwo().getScore()) {
            title = gameActivity.getPlayerOne().getPlayerName() + " win!\n" + gameActivity.getPlayerOne().getPlayerName() + " : " + gameActivity.getPlayerOne().getScore() +
            "     " + gameActivity.getPlayerTwo().getPlayerName() + " : " + gameActivity.getPlayerTwo().getScore();
        }
        else if(gameActivity.getPlayerOne().getScore() < gameActivity.getPlayerTwo().getScore()) {
            title = gameActivity.getPlayerTwo().getPlayerName() + " win!\n" + gameActivity.getPlayerTwo().getPlayerName() + " : " + gameActivity.getPlayerTwo().getScore() +
                    "    " + gameActivity.getPlayerOne().getPlayerName() + " : " + gameActivity.getPlayerOne().getScore();
        }
        else {
            title = "Draw!\n" + gameActivity.getPlayerOne().getPlayerName() + " : " + gameActivity.getPlayerOne().getScore() +
                    "    " + gameActivity.getPlayerTwo().getPlayerName() + " : " + gameActivity.getPlayerTwo().getScore();
        }


        builder.setTitle(title);

        builder.setItems(R.array.finish_game, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SelectFinishItemListener listener = (SelectFinishItemListener) getActivity();
                listener.onSelectFinishItem(which);

            }
        });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener(){
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    GameFinishDialogFragment.this.getActivity().finish();
                    return true;
                }
                return false;
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        // Create the AlertDialog object and return it
        return alertDialog;
    }
}
