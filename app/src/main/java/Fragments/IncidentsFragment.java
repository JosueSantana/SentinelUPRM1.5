package Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hmkcode.locations.sentineluprm15.R;

/**
 * This fragment controls the incidents to be manipulated into the table.
 */
public class IncidentsFragment extends Fragment {

    public IncidentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_incidents, container, false);
    }

    private void onItemClick(){

    }

    private void openIncidentMap(){

    }

    private void openIncidentMapAll(){

    }

}
