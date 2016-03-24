package Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import OtherHandlers.DialogCaller;

/**
 * Created by a136803 on 2/12/16.
 */
public class IntentDialogFragment extends DialogFragment {

    Bundle transferBundle;

    public IntentDialogFragment(){
    }

    public Dialog onCreateDialog( Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        transferBundle = new Bundle();
        transferBundle.putAll(getArguments());

        builder.setTitle(getArguments().getInt("dialogtitle"));
        builder.setMessage(getArguments().getInt("dialogmessage"));
        int positiveButtonMessage = getArguments().getInt("positivetitle");
        int negativeButtonMessage = getArguments().getInt("negativetitle");


        builder.setCancelable(true);
        builder.setPositiveButton(positiveButtonMessage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //User cancelled the dialog.
                IntentDialogFragment.this.getDialog().dismiss();
                ((DialogCaller) getActivity())
                        .doPositiveClick(transferBundle);
            }
        });

        if (getArguments().getBoolean("hasneg")){
            builder.setNegativeButton(negativeButtonMessage, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //User cancelled the dialog.
                    IntentDialogFragment.this.getDialog().dismiss();
                    ((DialogCaller) getActivity())
                            .doNegativeClick(transferBundle);
                }
            });
        }


        return builder.create();
    }
}
