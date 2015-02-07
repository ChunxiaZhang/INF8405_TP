package com.memorygame.example.zoe.tp1_memorygame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Zoe on 15-02-06.
 */
public class PlayerSettingDialogFragment extends DialogFragment {
    private EditText playerName1;
    private EditText playerName2;

    public interface PlayerSettingListener {
        void onPlayerSetting(String name1, String name2, boolean isRobotPlaying);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final boolean isRobotPlaying = getArguments().getBoolean("RobotPlaying");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_playersetting, null);
        playerName1 = (EditText) view.findViewById(R.id.playerOne);
        playerName2 = (EditText) view.findViewById(R.id.PlayerTwo);
        if(isRobotPlaying) {
            playerName2.setText("Robot");
            playerName2.setEnabled(false);
        }

        builder.setView(view)
                .setPositiveButton("Start Game",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("PlayerSettingDialogFragment:", "Start Game");
                                PlayerSettingListener listener = (PlayerSettingListener) getActivity();
                                listener.onPlayerSetting(playerName1.getText().toString(),
                                        playerName2.getText().toString(), isRobotPlaying);
                            }
                        });
        return builder.create();
    }
}
