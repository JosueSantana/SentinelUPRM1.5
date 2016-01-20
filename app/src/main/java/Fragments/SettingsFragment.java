package Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableRow;

import com.hmkcode.locations.sentineluprm15.R;

/**
 * This fragment manages the toggles in the settings.
 */
public class SettingsFragment extends Fragment {

    private TableRow contactsrow;
    private TableRow languagesrow;
    private TableRow feedbackRow;
    private TableRow policiesRow;

    public SettingsFragment() {
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        contactsrow = (TableRow) getView().findViewById(R.id.contactsrow);
        languagesrow = (TableRow) getView().findViewById(R.id.languagesrow);
        feedbackRow = (TableRow) getView().findViewById(R.id.feedbackrow);
        policiesRow = (TableRow) getView().findViewById(R.id.policiesrow);

        //set all the settings listeners
        contactsListListener();

        languagesListListener();

        termsListener();

        reportProblemListener();
    }

    private void togglePreference(){

    }

    private void contactsListListener(){
        contactsrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.coordlayout, new ContactsFragment()).addToBackStack(null).commit();
                return false;
            }
        });
        return;
    }

    private void languagesListListener(){
        languagesrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.coordlayout, new LanguagesFragment()).addToBackStack(null).commit();
                return false;
            }
        });
    }

    private void termsListener(){
        feedbackRow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.coordlayout, new FeedbackFragment()).addToBackStack(null).commit();
                return false;
            }
        });
    }

    private void reportProblemListener(){
        policiesRow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.coordlayout, new PoliciesFragment()).addToBackStack(null).commit();
                return false;
            }
        });
    }

}
