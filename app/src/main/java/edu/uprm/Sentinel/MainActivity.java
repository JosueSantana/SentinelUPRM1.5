package edu.uprm.Sentinel;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import Fragments.FeedbackFragment;
import OtherHandlers.DialogCaller;
import edu.uprm.Sentinel.R;

import Fragments.ViewPagerFragment;


public class MainActivity extends AppCompatActivity implements DialogCaller {

    private Toolbar toolbar;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();

        //fix orientation on Portrait
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //initiate main activity with viewpagerfragment embedded
        fm.beginTransaction().add(R.id.mainLayout, new ViewPagerFragment()).commit();

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

    @Override
    public void doPositiveClick() {
        //nothing
    }

    @Override
    public void doNegativeClick() {
        //nothing
    }

    //RECALL TO MODULARIZE THIS
    @Override
    public void doItemClick(int position) {
        FeedbackFragment feedbackFrag = new FeedbackFragment();
        Bundle feedbackBundle = new Bundle();

        //get the name of the view to access
        String viewName = getResources().getStringArray(R.array.policy_options)[position];

        if(viewName.equals(getResources().getString(R.string.reportproblem))){
            feedbackBundle.putString("title", getResources().getString(R.string.reportproblem));
            feedbackBundle.putString("hint", getResources().getString(R.string.problemhint));
            feedbackBundle.putString("footer", getResources().getString(R.string.reportproblemfooter));

        }
        else{
            feedbackBundle.putString("title", getResources().getString(R.string.reportfeedback));
            feedbackBundle.putString("hint", getResources().getString(R.string.feedbackhint));
            feedbackBundle.putString("footer", getResources().getString(R.string.reportfeedbackfooter));
        }

        feedbackFrag.setArguments(feedbackBundle);
        fm.beginTransaction().replace(R.id.mainLayout, feedbackFrag).addToBackStack(null).commit();
    }
}