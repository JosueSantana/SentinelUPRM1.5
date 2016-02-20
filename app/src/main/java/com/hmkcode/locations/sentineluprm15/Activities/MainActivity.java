package com.hmkcode.locations.sentineluprm15.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.hmkcode.locations.sentineluprm15.R;

import java.util.List;

import Fragments.ViewPagerFragment;
import OtherHandlers.ValuesCollection;

// Added simple comment.

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

        //Just to clear sharedpreferences when necessary
        /*SharedPreferences sp = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();*/

    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        List<Fragment> fragmentList =  getSupportFragmentManager().getFragments();
        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {


            getFragmentManager().popBackStack();
        }

    }

}