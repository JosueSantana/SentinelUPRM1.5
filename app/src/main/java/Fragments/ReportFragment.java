package Fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.hmkcode.locations.sentineluprm15.R;

/**
 * This fragment listens on user input when reporting.
 */
public class ReportFragment extends Fragment {
    public ReportFragment() {
        // Required empty public constructor
    }

    private ImageButton mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mButton = (ImageButton) getView().findViewById(R.id.alertButton);

        mButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mButton.setBackgroundResource(R.drawable.alert_button_pressed);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mButton.setBackgroundResource(R.drawable.alert_button);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.coordlayout, new CountdownFragment()).commit();
                }
                return false;
            }
        });

    }

    private void onClick(){

    }

    private void sendAlert(){

    }

    private void returnToAlertActivity(){

    }

    private void beginReport(){

    }
}