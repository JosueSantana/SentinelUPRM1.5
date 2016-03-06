package Fragments;

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

import OtherHandlers.ValuesCollection;

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

        settings = getContext().getSharedPreferences(ValuesCollection.SETTINGS_SP, 0);
        final SharedPreferences.Editor editor = settings.edit();

        enRow = (TableRow) getView().findViewById(R.id.englishRow);
        esRow = (TableRow) getView().findViewById(R.id.espanolRow);
        frRow = (TableRow) getView().findViewById(R.id.francaisRow);
        deRow = (TableRow) getView().findViewById(R.id.deutschRow);
        ptRow = (TableRow) getView().findViewById(R.id.portuguesRow);

        enRow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(getContext(),
                        "You have selected English", Toast.LENGTH_SHORT)
                        .show();
                editor.putString("appLocale","en").commit();
                setLocale("en");
                return true;
            }

        });

        esRow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(getContext(),
                        "Ha seleccionado español", Toast.LENGTH_SHORT)
                        .show();
                editor.putString("appLocale", "es").commit();
                setLocale("es");
                return true;
            }
        });

        frRow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(getContext(),
                        "Ha seleccionado español", Toast.LENGTH_SHORT)
                        .show();
                editor.putString("appLocale", "fr").commit();
                setLocale("fr");
                return true;
            }
        });

        deRow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(getContext(),
                        "Ha seleccionado español", Toast.LENGTH_SHORT)
                        .show();
                editor.putString("appLocale", "de").commit();
                setLocale("de");
                return true;
            }
        });

        ptRow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(getContext(),
                        "Ha seleccionado español", Toast.LENGTH_SHORT)
                        .show();
                editor.putString("appLocale", "pt").commit();
                setLocale("pt");
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
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
        getActivity().getSupportFragmentManager().beginTransaction().attach(this).commit();
    }
}
