package Fragments;


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

import java.util.concurrent.TimeUnit;

import OtherHandlers.ValuesCollection;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertWaitFragment extends Fragment {

    private TextView timeCount;
    private SharedPreferences credentials;
    private Handler handler = new Handler();
    private SharedPreferences.Editor editor;

    /*//This runnable runs for as long as a minute hasn't passed by
    private final Runnable refreshing = new Runnable() {
        public void run() {
            timeCount = (TextView) getActivity().findViewById(R.id.bottomlabelalerted);

            if (isRefreshing()) {
                // re run the verification after 1 minute
                String minutesToAlert = String.valueOf(ValuesCollection.TIMER_PERIOD - TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - credentials.getLong("alertTime", 0)));
                System.out.println("IN RUNNABLE!!");
                String text = String.format(getResources().getString(R.string.bottomlabelalerted), minutesToAlert);
                timeCount.setText(text);

                getActivity().getSupportFragmentManager().beginTransaction().detach(AlertWaitFragment.this).commit();
                getActivity().getSupportFragmentManager().beginTransaction().attach(AlertWaitFragment.this).commit();
                handler.postDelayed(this, 60000);
            }
            else{
                editor = credentials.edit();
                editor.putLong("alertTime", 0);
                editor.putBoolean("alertDisabled", false);
                editor.commit();

                if(getActivity() instanceof SplashActivity){
                    getActivity().getSupportFragmentManager().beginTransaction().remove(AlertWaitFragment.this).commit();
                    Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                    startActivity(mainIntent);
                    getActivity().finish();
                }
                else{
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ViewPagerFragment()).commit();
                }
            }


        }
    };*/

   /* private boolean isRefreshing() {
        long alertTime = credentials.getLong("alertTime", 0);

        return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - alertTime) < ValuesCollection.TIMER_PERIOD;
    }*/

    public AlertWaitFragment() {
        // Required empty public constructor
    }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_alert_wait, container, false);
        }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //get reference to bottom label object
        TextView timeCount = (TextView) getView().findViewById(R.id.bottomlabelalerted);

        //get reference to credentials
        credentials = getContext().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        editor = credentials.edit();

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
        //handler.post(refreshing);
    }
}
