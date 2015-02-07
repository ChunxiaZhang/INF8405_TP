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

        String title, message;
        if(MainActivity.playerOne.getScore() > MainActivity.playerTwo.getScore()) {
            title = MainActivity.playerOne.getName() + " win! Congratulations!\n" + MainActivity.playerOne.getName() + " : " + MainActivity.playerOne.getScore() +
            "     " + MainActivity.playerTwo.getName() + " : " + MainActivity.playerTwo.getScore();
        }
        else if(MainActivity.playerOne.getScore() < MainActivity.playerTwo.getScore()) {
            title = MainActivity.playerTwo.getName() + " win! Congratulations!\n" + MainActivity.playerTwo.getName() + " : " + MainActivity.playerTwo.getScore() +
                    "    " + MainActivity.playerOne.getName() + " : " + MainActivity.playerOne.getScore();
        }
        else {
            title = "Draw!\n" + MainActivity.playerOne.getName() + " : " + MainActivity.playerOne.getScore() +
                    "    " + MainActivity.playerTwo.getName() + " : " + MainActivity.playerTwo.getScore();
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
