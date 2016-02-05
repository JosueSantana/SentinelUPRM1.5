package com.hmkcode.locations.sentineluprm15.Activities;

import android.content.Intent;
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

import Fragments.CountdownFragment;
import Fragments.EmergencyFragment;
import Fragments.ViewPagerFragment;

/**
 * This activity handles the inputs for the login menu.
 */
public class SignupActivity extends AppCompatActivity {

    private ImageButton mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mButton = (ImageButton) findViewById(R.id.phoneOnSignup);

        // Create listener for Alert Button
        mButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                getSupportFragmentManager().beginTransaction().add(R.id.signupRLayout, new EmergencyFragment()).addToBackStack(null).commit();
                return false;
            }
        });

        EditText editText = (EditText) findViewById(R.id.password);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_GO) {
                    Intent veriIntent = new Intent(SignupActivity.this, VerificationActivity.class);
                    startActivity(veriIntent);
                    finish();
                    handled = true;
                }
                return handled;
            }
        });

    }

    private void getEmailAndPassword(){

    }

    private boolean isUserValid(){
        return false;
    }

    private void attemptLogin() {

    }
}
