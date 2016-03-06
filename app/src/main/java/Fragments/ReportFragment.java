package Fragments;

import android.content.SharedPreferences;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import edu.uprm.Sentinel.R;

import OtherHandlers.ValuesCollection;

/**
 * This fragment listens on user input when reporting.
 */
public class ReportFragment extends Fragment {

    private ImageButton mButton;

    public ReportFragment() {
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
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences settings = this.getActivity().getSharedPreferences(ValuesCollection.SETTINGS_SP, 0);
        System.out.println("APPLOCALE IS: " + settings.getString("appLocale", "no"));
        mButton = (ImageButton) getView().findViewById(R.id.alertButton);

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.mainLayout, new CountdownFragment()).addToBackStack("ReportFragment").commit();
            }
            /*public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.mainLayout, new CountdownFragment()).addToBackStack("ReportFragment").commit();}*/
        });
    }

    private void onClick() {

    }

    private void returnToAlertActivity(){

    }

    private void beginReport(){

    }
}