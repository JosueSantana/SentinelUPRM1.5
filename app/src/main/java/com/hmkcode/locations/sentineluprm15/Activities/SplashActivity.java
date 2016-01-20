package com.hmkcode.locations.sentineluprm15.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hmkcode.locations.sentineluprm15.R;

/**
 * This class verifies the user data and checks whether it is the first time accessing the app, if
 * the token has been generated, and loads user preferences.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    private boolean verifyIfFirstTimeLoadingApp(){

        return false;
    }

    private boolean verifyIfTokenIsStored(){

        return false;
    }
    private boolean verifysessionUniqueness(){

        return false;
    }
    private void loadUserPreferences(){}
}
