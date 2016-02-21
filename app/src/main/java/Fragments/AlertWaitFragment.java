package Fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hmkcode.locations.sentineluprm15.Activities.MainActivity;
import com.hmkcode.locations.sentineluprm15.Activities.SplashActivity;
import com.hmkcode.locations.sentineluprm15.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import OtherHandlers.ValuesCollection;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertWaitFragment extends Fragment {

    private TextView timeCount;
    private SharedPreferences credentials;
    private SharedPreferences.Editor editor;
    private static TimerTask tTask;
    private static Timer updateTimer;

    public AlertWaitFragment() {
        // Required empty public constructor
    }

    public TimerTask produceTimerTask() {
        TimerTask timer = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("IN RUNNABLE!!");
                        if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - credentials.getLong("alertTime", 0)) < ValuesCollection.TIMER_PERIOD) {
                            String minutesToAlert = String.valueOf(ValuesCollection.TIMER_PERIOD - TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - credentials.getLong("alertTime", 0)));
                            String text = String.format(getResources().getString(R.string.bottomlabelalerted), minutesToAlert);
                            timeCount.setText(text);
                        }
                    }
                });
                if (!(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - credentials.getLong("alertTime", 0)) < ValuesCollection.TIMER_PERIOD)) {
                    editor = credentials.edit();
                    editor.putLong("alertTime", 0);
                    editor.putBoolean("alertDisabled", false);
                    editor.commit();
                    updateTimer.cancel();

                    if (getActivity() instanceof SplashActivity) {
                        System.out.println("INSTANCEOF SPLASH");
                        Activity myActivity = getActivity();
                        getActivity().getSupportFragmentManager().beginTransaction().remove(AlertWaitFragment.this).commitAllowingStateLoss();
                        Intent mainIntent = new Intent(myActivity, MainActivity.class);
                        startActivity(mainIntent);
                        myActivity.finish();
                    } else if (getActivity() instanceof MainActivity) {
                        System.out.println("INSTANCEOF MAIN");
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ViewPagerFragment()).commitAllowingStateLoss();
                    }

                    this.cancel();
                }
            }
        };
        return timer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_alert_wait, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        System.out.println("CALLING ONACTIVITYCREATED");
        //get reference to credentials
        credentials = getContext().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        editor = credentials.edit();

        //get reference to bottom label object
        timeCount = (TextView) getView().findViewById(R.id.bottomlabelalerted);

        //first time accessing this screen since alert?
        if(!credentials.contains("alertDisabled")|| !credentials.getBoolean("alertDisabled", false)){
            long currentTime = System.currentTimeMillis();

            //set the string's timer to the timer period
            String text = String.format(getResources().getString(R.string.bottomlabelalerted), String.valueOf(ValuesCollection.TIMER_PERIOD));
            timeCount.setText(text);

            //put the timer-related variables in credentials
            editor.putLong("alertTime", currentTime).commit();
            editor.putBoolean("alertDisabled", true).commit();
        }
        else{

            //get the time difference until ready
            String minutesToAlert = String.valueOf(ValuesCollection.TIMER_PERIOD - TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - credentials.getLong("alertTime", 0)));

            String text = String.format(getResources().getString(R.string.bottomlabelalerted), minutesToAlert);
            timeCount.setText(text);

            //Just a provisional button action on the shield to wipe out timer-related credentials
            ImageView logo = (ImageView) getView().findViewById(R.id.sentinelLogo);
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   editor.remove("alertTime").remove("alertDisabled").commit();
                    System.out.println("CLEARED!!");
                }
            });
        }

        /*// Inflate the layout for this fragment
        updateTimer = new Timer();
        tTask = produceTimerTask();
        updateTimer.scheduleAtFixedRate(tTask, 0, 60000);*/
    }



    public void onResume(){
        super.onResume();
        System.out.println("CALLING ONRESUME");

        //get the time difference until ready
        String minutesToAlert = String.valueOf(ValuesCollection.TIMER_PERIOD - TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - credentials.getLong("alertTime", 0)));

        String text = String.format(getResources().getString(R.string.bottomlabelalerted), minutesToAlert);
        timeCount.setText(text);

        // Inflate the layout for this fragment
        updateTimer = new Timer();
        tTask = produceTimerTask();
        updateTimer.scheduleAtFixedRate(tTask, 0, 60000);
    }

    public void onStop(){
        System.out.println("CALLING ONSTOP");
        System.out.println("CANCELLED?: " + tTask.cancel());
        updateTimer.cancel();
        updateTimer.purge();
        super.onStop();
    }
}
