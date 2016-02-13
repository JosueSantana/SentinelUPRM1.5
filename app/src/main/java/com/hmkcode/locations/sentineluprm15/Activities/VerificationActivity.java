package com.hmkcode.locations.sentineluprm15.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hmkcode.locations.sentineluprm15.R;

import Fragments.EmergencyFragment;
import Fragments.SentinelDialogFragment;
import OtherHandlers.ValuesCollection;

/**
 * Created by a136803 on 2/3/16.
 */
public class VerificationActivity extends AppCompatActivity {

    private ImageButton phoneButton;
    private ImageButton proceedButton;
    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //fix orientation on Portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Create listener for Alert Button
        phoneButton = (ImageButton) findViewById(R.id.phoneOnSignup2);
        phoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().add(R.id.verificationRLayout, new EmergencyFragment()).addToBackStack("EmergencyFromVerification").commit();
            }
        });

        //create listener for "GO" edit text action
        final EditText editText = (EditText) findViewById(R.id.codeEnter);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_GO) {
                    attemptVerification(editText.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });

        // Create listener for Proceed Button
        proceedButton = (ImageButton) findViewById(R.id.proceedverification);
        proceedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptVerification(editText.getText().toString());
            }
        });
    }

    private void attemptVerification(String vCode) {

        //TODO: call encryption mechanism
        //TODO: call request handler
        //TODO: manage failure conditions

        boolean itMatches = true; //provisional variable until code can be verified with server

        //manage failure conditions
        if(vCode.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putInt("dialogtitle", R.string.emptyformatalerttitle);
            bundle.putInt("dialogmessage", R.string.emptyformatalertmessage);
            SentinelDialogFragment dialogFragment = new SentinelDialogFragment();
            dialogFragment.setArguments(bundle);
            // Show Alert DialogFragment
            dialogFragment.show(fm, "Alert Dialog Fragment");
        }
        //TODO: Match the code with the server
        else if(!itMatches){
            Bundle bundle = new Bundle();
            bundle.putInt("dialogtitle", R.string.wrongcodetitle);
            bundle.putInt("dialogmessage", R.string.wrongcodemessage);
            SentinelDialogFragment dialogFragment = new SentinelDialogFragment();
            dialogFragment.setArguments(bundle);
            // Show Alert DialogFragment
            dialogFragment.show(fm, "Alert Dialog Fragment");
        }
        else {

            SharedPreferences credentials = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
            SharedPreferences.Editor editor = credentials.edit();

            editor.putBoolean("isVerified", true).commit();

            Intent veriIntent = new Intent(VerificationActivity.this, MainActivity.class);
            veriIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
            startActivity(veriIntent);
        }

    }

}

