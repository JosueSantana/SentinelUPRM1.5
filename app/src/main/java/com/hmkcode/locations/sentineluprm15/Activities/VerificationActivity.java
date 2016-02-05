package com.hmkcode.locations.sentineluprm15.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hmkcode.locations.sentineluprm15.R;

/**
 * Created by a136803 on 2/3/16.
 */
public class VerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        EditText editText = (EditText) findViewById(R.id.codeEnter);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_GO) {
                    Intent veriIntent = new Intent(VerificationActivity.this, MainActivity.class);
                    startActivity(veriIntent);
                    finish();
                    handled = true;
                }
                return handled;
            }
        });
    }

}

