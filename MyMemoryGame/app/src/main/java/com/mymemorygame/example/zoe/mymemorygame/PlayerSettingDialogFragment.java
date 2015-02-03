package com.mymemorygame.example.zoe.mymemorygame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Zoe on 15-02-02.
 */
public class PlayerSettingDialogFragment extends DialogFragment {
    private EditText playerName1;
    private EditText playerName2;

    public interface PlayerSettingListener {
        void onPlayerSetting(String name1, String name2);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_playersetting, null);
        playerName1 = (EditText) view.findViewById(R.id.playerOne);
        playerName2 = (EditText) view.findViewById(R.id.PlayerTwo);
        builder.setView(view)
                .setPositiveButton("Start Game",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                    PlayerSettingListener listener = (PlayerSettingListener) getActivity();
                                    listener.onPlayerSetting(playerName1.getText().toString(), playerName2.getText().toString());

                            }
                        });
        return builder.create();
    }

}
