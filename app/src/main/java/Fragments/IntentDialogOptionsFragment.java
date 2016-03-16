package Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import OtherHandlers.DialogCaller;
import edu.uprm.Sentinel.R;

/**
 * Created by a136803 on 2/12/16.
 */

public class IntentDialogOptionsFragment extends DialogFragment {

    public IntentDialogOptionsFragment(){
    }

    public Dialog onCreateDialog( Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getArguments().getInt("dialogtitle"));
       // builder.setMessage(getArguments().getInt("dialogmessage"));


        int positiveButtonMessage = getArguments().getInt("positivetitle");
        int negativeButtonMessage = getArguments().getInt("negativetitle");
        int itemsMessage = getArguments().getInt("optionslist");

        builder.setItems(itemsMessage,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                IntentDialogOptionsFragment.this.getDialog().dismiss();
                ((DialogCaller) getActivity())
                        .doItemClick(i);
            }
        });

        builder.setCancelable(true);
        builder.setPositiveButton(positiveButtonMessage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //User cancelled the dialog.
                IntentDialogOptionsFragment.this.getDialog().dismiss();
                ((DialogCaller) getActivity())
                        .doPositiveClick();
            }
        });

        if (getArguments().getBoolean("hasneg")){
            builder.setNegativeButton(negativeButtonMessage, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //User cancelled the dialog.
                    IntentDialogOptionsFragment.this.getDialog().dismiss();
                    ((DialogCaller) getActivity())
                            .doNegativeClick();
                }
            });
        }


        return builder.create();
    }
}
