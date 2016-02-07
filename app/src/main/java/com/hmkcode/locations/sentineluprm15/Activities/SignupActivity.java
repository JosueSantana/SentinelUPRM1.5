package com.hmkcode.locations.sentineluprm15.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hmkcode.locations.sentineluprm15.R;

import Fragments.EmergencyFragment;
import OtherHandlers.ValuesCollection;

/**
 * This activity handles the inputs for the login menu.
 */
public class SignupActivity extends AppCompatActivity {

    private ImageButton phoneButton;
    private ImageButton proceedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        phoneButton = (ImageButton) findViewById(R.id.phoneOnSignup);
        proceedButton = (ImageButton) findViewById(R.id.proceedsignup);

        // Create listener for Alert Button
        phoneButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                getSupportFragmentManager().beginTransaction().add(R.id.signupRLayout, new EmergencyFragment()).addToBackStack(null).commit();
                return true;
            }
        });

        // Create listener for text field action
        EditText editText = (EditText) findViewById(R.id.password);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_GO) {
                    attemptSignup();
                    handled = true;
                }
                return handled;
            }
        });

        // Create listener for Alert Button
        proceedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                attemptSignup();
                return true;
            }
        });
    }
    private void attemptSignup() {

        //TODO: call encryption mechanism
        //TODO: call request handler
        //TODO: manage failure conditions

        SharedPreferences credentials = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);

        SharedPreferences.Editor editor = credentials.edit();

        String tk = "asdflkajls;fk";//provisionally, accepted to be the token value

        editor.putString("token", tk).commit();

        Intent veriIntent = new Intent(SignupActivity.this, VerificationActivity.class);
        veriIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
        startActivity(veriIntent);
    }
}
