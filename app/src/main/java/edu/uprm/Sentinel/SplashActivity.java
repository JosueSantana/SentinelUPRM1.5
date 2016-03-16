package edu.uprm.Sentinel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import edu.uprm.Sentinel.R;

import Fragments.AlertWaitFragment;
import OtherHandlers.ValuesCollection;
import edu.uprm.Sentinel.R;

/**
 * This class verifies the user data and checks whether it is the first time accessing the app, if
 * the token has been generated, and loads user preferences.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(ValuesCollection.SETTINGS_SP, 0);
        if(settings.getString("appLocale", null) != null) {
            setLocale(settings.getString("appLocale", null));
        }

        setContentView(R.layout.activity_splash);

        //get the credentials
        SharedPreferences credentials = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);

        checkSession(credentials);
    }
    //TODO: Add Code to go verification screen when necessary
    private boolean checkSession(SharedPreferences credentials){
        SharedPreferences.Editor editor = credentials.edit();

        //verify token is saved
        if(credentials.contains("token")) {
            //verify verification code

            if (credentials.contains("alertDisabled") && credentials.getBoolean("alertDisabled", false)) {
                long alertTime = credentials.getLong("alertTime", 0);
                if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - alertTime) < ValuesCollection.TIMER_PERIOD) {
                    getSupportFragmentManager().beginTransaction().add(R.id.splashRelative, new AlertWaitFragment()).commit();
                } else {
                    editor.putLong("alertTime", 0);
                    editor.putBoolean("alertDisabled", false);
                    editor.commit();
                    //go directly to alert view
                    Intent mainIntent = new Intent(this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }
            } else {
                //go directly to alert view
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }
        else{
            if(credentials.contains("atSignup") && credentials.contains(ValuesCollection.EMAIL_KEY)) {
                //go directly to alert view
                Intent mainIntent = new Intent(SplashActivity.this, VerificationActivity.class);
                startActivity(mainIntent);
                finish();
            }
            else if((credentials.getBoolean("atSignup", false))){
                Intent signupIntent = new Intent(SplashActivity.this, SignupActivity.class);
                startActivity(signupIntent);
                finish();
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

        }

        return false;
    }

    public void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
}
