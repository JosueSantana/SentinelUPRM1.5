package Fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.Toast;

import edu.uprm.Sentinel.R;

import java.util.Locale;

import OtherHandlers.Constants;

public class LanguagesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Locale myLocale;
    private TableRow enRow;
    private TableRow esRow;
    private TableRow deRow;
    private TableRow ptRow;
    private TableRow frRow;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public LanguagesFragment() {
        // Required empty public constructor
    }
    public static LanguagesFragment newInstance(String param1, String param2) {
        LanguagesFragment fragment = new LanguagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_languages, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        settings = getContext().getSharedPreferences(Constants.SETTINGS_SP, 0);
        editor = settings.edit();

        enRow = (TableRow) getView().findViewById(R.id.englishRow);
        esRow = (TableRow) getView().findViewById(R.id.espanolRow);
        frRow = (TableRow) getView().findViewById(R.id.francaisRow);
        deRow = (TableRow) getView().findViewById(R.id.deutschRow);
        ptRow = (TableRow) getView().findViewById(R.id.portuguesRow);

        languageRowListenerSetup(enRow, "en");
        languageRowListenerSetup(esRow, "es");
        languageRowListenerSetup(frRow, "fr");
        languageRowListenerSetup(deRow, "de");
        languageRowListenerSetup(ptRow, "pt");
    }

    public void languageRowListenerSetup(TableRow r,String localeCode){

        final String lc = localeCode;
        r.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Activity activity = getActivity();
                if(activity != null) {
                    setLocale(lc);
                    editor.putString("appLocale", lc).commit();
                    Toast.makeText(getContext(),
                            R.string.languageselectnotice, Toast.LENGTH_SHORT)
                            .show();
                    LanguagesFragment.this.getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
                return true;
            }
        });
    }

    public void setLocale(String lang) {

        myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
    }
}
