package com.hmkcode.locations.sentineluprm15.Activities;

import android.content.SharedPreferences;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.hmkcode.locations.sentineluprm15.R;

import Fragments.ViewPagerFragment;
import OtherHandlers.ValuesCollection;

// Added simple comment.

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.coordlayout, new ViewPagerFragment()).commit();


        //Just to clear sharedpreferences when necessary
        SharedPreferences sp = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();

    }


}