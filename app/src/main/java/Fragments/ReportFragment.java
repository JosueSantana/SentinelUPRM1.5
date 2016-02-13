package Fragments;

import android.view.View.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

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

        // Create listener for Alert Button
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.beginTransaction().replace(R.id.coordlayout, new CountdownFragment()).addToBackStack("ReportFragment").commit();
            }
        });

    }

    private void onClick() {

    }

    private void sendAlert(){

    }

    private void returnToAlertActivity(){

    }

    private void beginReport(){

    }
}