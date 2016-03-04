package com.hmkcode.locations.sentineluprm15.Activities;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fix orientation on Portrait
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //initiate main activity with viewpagerfragment embedded
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, new ViewPagerFragment()).commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            Typeface myTypeface = Typeface.createFromAsset(getAssets(), "stentiga.ttf");
            toolbarTitle.setTypeface(myTypeface);
            //getSupportActionBar().setTitle("My custom toolbar!");
            //getSupportActionBar().setHomeButtonEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //SharedPreferences credentials = getBaseContext().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        //SharedPreferences.Editor editor = credentials.edit();
        //editor.clear().commit();

    }

}