package com.mymemorygame.example.zoe.mymemorygame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;


/**
 * Created by Zoe on 15-01-28.
 */
public class OptionDialogFragment extends DialogFragment {

    private static boolean isNameValid1 = false;
    private static boolean isNameValid2 = false;


    /*public static OptionDialogFragment newInstance(int title) {
        OptionDialogFragment frag = new OptionDialogFragment();
        //Bundle args = new Bundle();
       // args.putInt("title", title);
       // frag.setArguments(args);
        return frag;
    }*/

    public interface SelectItemListener {
        void onSelectItem(int item);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.game_option);

        builder.setItems(R.array.option_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SelectItemListener listener = (SelectItemListener) getActivity();
                listener.onSelectItem(which);

            }
        });

        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    Log.i("exit","exit game");
                    AgentApplication.getInstance().onTerminate();

                }
                return false;
            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }


}
