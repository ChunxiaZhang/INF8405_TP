package com.memorygame.example.zoe.tp1_memorygame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

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

        String title, message;
        if(gameActivity.getPlayerOne().getScore() > gameActivity.getPlayerTwo().getScore()) {
            title = gameActivity.getPlayerOne().getName() + " win! Congratulations!\n" + gameActivity.getPlayerOne().getName() + " : " + gameActivity.getPlayerOne().getScore() +
            "     " + gameActivity.getPlayerTwo().getName() + " : " + gameActivity.getPlayerTwo().getScore();
        }
        else if(gameActivity.getPlayerOne().getScore() < gameActivity.getPlayerTwo().getScore()) {
            title = gameActivity.getPlayerTwo().getName() + " win! Congratulations!\n" + gameActivity.getPlayerTwo().getName() + " : " + gameActivity.getPlayerTwo().getScore() +
                    "    " + gameActivity.getPlayerOne().getName() + " : " + gameActivity.getPlayerOne().getScore();
        }
        else {
            title = "Draw!\n" + gameActivity.getPlayerOne().getName() + " : " + gameActivity.getPlayerOne().getScore() +
                    "    " + gameActivity.getPlayerTwo().getName() + " : " + gameActivity.getPlayerTwo().getScore();
        }


        builder.setTitle(title);

        builder.setItems(R.array.finish_game, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SelectFinishItemListener listener = (SelectFinishItemListener) getActivity();
                listener.onSelectFinishItem(which);

            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }
}
