package com.hmkcode.locations.sentineluprm15.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hmkcode.locations.sentineluprm15.R;

import OtherHandlers.ValuesCollection;

/**
 * Created by a136803 on 2/3/16.
 */
public class VerificationActivity extends AppCompatActivity {

    private ImageButton proceedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        proceedButton = (ImageButton) findViewById(R.id.proceedverification);

        EditText editText = (EditText) findViewById(R.id.codeEnter);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_GO) {
                    attemptVerification();
                    handled = true;
                }
                return handled;
            }
        });

        // Create listener for Alert Button
        proceedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                attemptVerification();
                return true;
            }
        });
    }

    private void attemptVerification() {

        //TODO: call encryption mechanism
        //TODO: call request handler
        //TODO: manage failure conditions

        SharedPreferences credentials = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);

        SharedPreferences.Editor editor = credentials.edit();

        editor.putBoolean("isVerified", true).commit();

        Intent veriIntent = new Intent(VerificationActivity.this, MainActivity.class);
        veriIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
        startActivity(veriIntent);

    }

}

