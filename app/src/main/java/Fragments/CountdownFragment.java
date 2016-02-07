package Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hmkcode.locations.sentineluprm15.R;

public class CountdownFragment extends Fragment {

    private TextView countDownDisplay;
    public CountdownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("---Created Countdown----");

        FragmentManager fm = getActivity().getSupportFragmentManager();

        System.out.println("Number of Fragments in Backstack: " + fm.getBackStackEntryCount());
        for(int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
            System.out.println("Found fragment: " + fm.getBackStackEntryAt(entry).getId());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("---Created Countdown VIEW----");
        return inflater.inflate(R.layout.fragment_countdown, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        countDownDisplay = (TextView) getView().findViewById(R.id.countDownDisplay);

        new CountDownTimer(7000, 1000) {

            public void onTick(long millisUntilFinished) {
                countDownDisplay.setText(String.valueOf((millisUntilFinished / 1000) - 1));
            }

            public void onFinish() {
                //removing current fragment and replacing it with the last fragment on backstack
                getActivity().getSupportFragmentManager().popBackStackImmediate();

            }
        }.start();

    }
}