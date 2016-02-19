package com.hmkcode.locations.sentineluprm15.Activities;

import android.content.SharedPreferences;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.hmkcode.locations.sentineluprm15.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import Fragments.ViewPagerFragment;
import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import OtherHandlers.ValuesCollection;

// Added simple comment.

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, new ViewPagerFragment()).commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            //getSupportActionBar().setTitle("My custom toolbar!");
            //getSupportActionBar().setHomeButtonEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        System.out.println("lol");
        //sendAlert();


    }

    private void sendAlert() throws JSONException, CryptorException {

        final CryptographyHandler crypto = new CryptographyHandler();

        JSONObject alertJSON = new JSONObject();

        alertJSON.put("token", getToken());
        alertJSON.put("latitude", "18.2338540");
        alertJSON.put("longitude", "-67.1337090");

        Ion.with(getBaseContext())
                .load(ValuesCollection.SEND_ALERT_URL)
                .setBodyParameter(ValuesCollection.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(alertJSON))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        System.out.println(result);
                        try {
                            JSONObject receivedSentinelMessage = JSONHandler.convertStringToJSON(result);
                            String encryptedJSONReceived = JSONHandler.getSentinelMessage(receivedSentinelMessage);
                            String decryptedJSONReceived = crypto.decryptString(encryptedJSONReceived);

                            JSONObject receivedJSON = JSONHandler.convertStringToJSON(decryptedJSONReceived);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        } catch (CryptorException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private String getToken() {
        SharedPreferences credentials = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        String storedToken = credentials.getString(ValuesCollection.TOKEN_KEY, null);
        return storedToken;
    }
}