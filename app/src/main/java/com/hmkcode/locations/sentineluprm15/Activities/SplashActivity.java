package com.hmkcode.locations.sentineluprm15.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hmkcode.locations.sentineluprm15.R;

import OtherHandlers.ValuesCollection;

/**
 * This class verifies the user data and checks whether it is the first time accessing the app, if
 * the token has been generated, and loads user preferences.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //get the credentials
        SharedPreferences credentials = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);

        checkSession(credentials);
    }

    private boolean checkSession(SharedPreferences credentials){
        SharedPreferences.Editor editor = credentials.edit();

        //verify token is saved
        if(credentials.contains("token")) {

            //verify verification code
            if (credentials.contains("isVerified") && credentials.getBoolean("isVerified", false)) {

                    //go directly to alert view
                    Intent mainIntent = new Intent(this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
            }
            else {
                //go to verification view instead
                Intent veriIntent = new Intent(this, VerificationActivity.class);
                startActivity(veriIntent);
                finish();
            }
        }
        else{
            //go to sign up screen
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    Intent signupIntent = new Intent(SplashActivity.this, SignupActivity.class);
                    startActivity(signupIntent);
                    finish();
                }
            }, 3000);


        }

        return false;
    }
}
