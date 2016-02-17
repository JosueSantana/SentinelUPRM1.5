package Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hmkcode.locations.sentineluprm15.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertWaitFragment extends Fragment {


    public AlertWaitFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert_wait, container, false);
    }

}
