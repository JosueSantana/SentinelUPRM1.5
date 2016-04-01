package edu.uprm.Sentinel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.TextView;

import Fragments.FeedbackFragment;
import OtherHandlers.DialogCaller;
import OtherHandlers.ValuesCollection;
import edu.uprm.Sentinel.R;

import Fragments.ViewPagerFragment;
import edu.uprm.Sentinel.Services.GCMListenerService;


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
            //toolbarTitle.setGravity(Gravity.CENTER);
            //getSupportActionBar().setTitle("My custom toolbar!");
            //getSupportActionBar().setHomeButtonEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //SharedPreferences credentials = getBaseContext().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        //SharedPreferences.Editor editor = credentials.edit();
        //editor.clear().commit();
    }

    @Override
    public void doPositiveClick(Bundle bundle) {
        if(bundle != null && bundle.getString("intentkind").equals("unsubscribe")){
            final Intent unsubscribeIntent = new Intent(MainActivity.this, SignupActivity.class);
            unsubscribeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //clear all credentials
            getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0).edit().clear().commit();
            getSharedPreferences(ValuesCollection.SETTINGS_SP, 0).edit().clear().commit();

            startActivity(unsubscribeIntent);
            finish();

        }
    }

    @Override
    public void doNegativeClick(Bundle bundle) {

    }

    @Override
    public void doItemClick(Bundle bundle) {

        if(bundle != null && bundle.getString("intentkind").equals("feedback")) {
            //preraparing fragment to send the title data
            FeedbackFragment feedbackFrag = new FeedbackFragment();
            Bundle feedbackBundle = new Bundle();

            //get the name of the view to access
            String viewName = getResources().getStringArray(R.array.policy_options)[bundle.getInt("position")];

            if (viewName.equals(getResources().getString(R.string.reportproblem))) {
                feedbackBundle.putString("title", getResources().getString(R.string.reportproblem));
                feedbackBundle.putString("hint", getResources().getString(R.string.problemhint));
                feedbackBundle.putString("footer", getResources().getString(R.string.reportproblemfooter));
                feedbackBundle.putBoolean("reportProblem", true);

            } else {
                feedbackBundle.putString("title", getResources().getString(R.string.reportfeedback));
                feedbackBundle.putString("hint", getResources().getString(R.string.feedbackhint));
                feedbackBundle.putString("footer", getResources().getString(R.string.reportfeedbackfooter));
                feedbackBundle.putBoolean("reportProblem", false);
            }

            feedbackFrag.setArguments(feedbackBundle);
            fm.beginTransaction().replace(R.id.mainLayout, feedbackFrag).addToBackStack(null).commit();
        }
    }


}