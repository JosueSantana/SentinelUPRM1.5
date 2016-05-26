package Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import edu.uprm.Sentinel.R;

/**
 * Created by a136803 on 2/12/16.
 */
public class SimpleDialogFragment extends DialogFragment {

    public SimpleDialogFragment(){
    }

    public Dialog onCreateDialog( Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getInt("dialogtitle"));
        builder.setMessage(getArguments().getInt("dialogmessage"));

        builder.setCancelable(true);

        builder.setPositiveButton(R.string.okmessage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //User cancelled the dialog.
                SimpleDialogFragment.this.getDialog().dismiss();
            }
        });


        return builder.create();
    }
}
