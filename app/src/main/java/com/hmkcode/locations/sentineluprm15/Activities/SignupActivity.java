package com.hmkcode.locations.sentineluprm15.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import android.os.Bundle;
import android.view.KeyEvent;

import android.view.View;

import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.hmkcode.locations.sentineluprm15.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Fragments.EmergencyFragment;
import Fragments.SentinelDialogFragment;
import OtherHandlers.ValuesCollection;

/**
 * This activity handles the inputs for the login menu.
 */
public class SignupActivity extends FragmentActivity {

    private ImageButton phoneButton;
    private ImageButton proceedButton;
    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //fix orientation on Portrait
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Create listener for Alert Button
        phoneButton = (ImageButton) findViewById(R.id.phoneOnSignup);
        phoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().add(R.id.signupRLayout, new EmergencyFragment()).addToBackStack("EmergencyFromSignUp").commit();
            }
        });

        // Create listener for text field action
        final AutoCompleteTextView emailText = (AutoCompleteTextView) findViewById(R.id.email);
        final EditText editText = (EditText) findViewById(R.id.password);
        final List<TextView> l = new ArrayList<TextView>();
        l.add(emailText);
        l.add(editText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_GO) {

                    attemptSignup(l);
                    handled = true;
                }
                return handled;
            }
        });

        // Create listener for Proceed Button
        proceedButton = (ImageButton) findViewById(R.id.proceedsignup);
        proceedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup(l);
            }
        });

    }

    private boolean checkEmailFormat(String email){

        // String to be scanned to find the pattern.
        String pattern = "(.*)(@uprm?\\.edu)($)";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(email);

        return m.find();
    }

    private void attemptSignup(List textViews) {


        //These are the strings corresponding to user input (potentially pass to a handling function)
        String email = ((AutoCompleteTextView) textViews.get(0)).getText().toString();
        String pass = ((EditText) textViews.get(1)).getText().toString();

        System.out.println("email: " + email + " pass: " + pass);

        //manage failure conditions
        if(email.isEmpty() || pass.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putInt("dialogtitle", R.string.emptyformatalerttitle);
            bundle.putInt("dialogmessage", R.string.emptyformatalertmessage);
            SentinelDialogFragment dialogFragment = new SentinelDialogFragment();
            dialogFragment.setArguments(bundle);
            // Show Alert DialogFragment
            dialogFragment.show(fm, "Alert Dialog Fragment");
        }
        else if(!checkEmailFormat(email)){
            Bundle bundle = new Bundle();
            bundle.putInt("dialogtitle", R.string.incorrectemailformattitle);
            bundle.putInt("dialogmessage", R.string.incorrectemailformatmessage);
            SentinelDialogFragment dialogFragment = new SentinelDialogFragment();
            dialogFragment.setArguments(bundle);
            // Show Alert DialogFragment
            dialogFragment.show(fm, "Alert Dialog Fragment");
        }
        else{
            //TODO: call encryption mechanism
            //TODO: call request handler
            SharedPreferences credentials = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
            SharedPreferences.Editor editor = credentials.edit();

            String tk = "asdflkajls;fk"; //provisionally, accepted to be the token value

            editor.putString("token", tk).commit();

            /*Bundle bundle = new Bundle();
            bundle.putInt("dialogtitle", R.string.sendverificationalerttitle);
            bundle.putInt("dialogmessage", R.string.sendverificationalertmessage);
            final SentinelDialogFragment dialogFragment = new SentinelDialogFragment();
            dialogFragment.setArguments(bundle);
            dialogFragment.show(fm, "Alert Dialog Fragment");*/

            /*dialogFragment.onDismiss(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {*/
                        //ideally this toast will be replaced soon
                        Toast.makeText(SignupActivity.this,
                        R.string.sendverificationalerttitle, Toast.LENGTH_SHORT)
                        .show();
                        Intent veriIntent = new Intent(SignupActivity.this, VerificationActivity.class);
                        veriIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        this.startActivity(veriIntent);
                  /*  }
                });*/

        }
    }
}
