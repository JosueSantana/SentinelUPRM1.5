package Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View.OnClickListener;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import edu.uprm.Sentinel.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import OtherHandlers.ValuesCollection;

/**
 * This fragment manages the toggles in the settings.
 */
public class SettingsFragment extends Fragment{

    private TableRow contactsRow;
    private TableRow languagesRow;
    private TableRow feedbackRow;
    private TableRow policiesRow;
    private TableRow aboutusRow;

    private Switch emailSwitch;
    private Switch smsSwitch;
    private Switch pushSwitch;
    private Switch familySwitch;

    SharedPreferences settings;
    private Handler handler;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }

    @Override
         public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //get reference to row objects
        contactsRow = (TableRow) getView().findViewById(R.id.contactsrow);

        //set the contacts count
        TextView contactsText = (TextView) contactsRow.getChildAt(1);
        settings = this.getActivity().getSharedPreferences(ValuesCollection.SETTINGS_SP, 0);
        String text;

        if(settings.contains("contactsCount"))
             text = String.format(getResources().getString(R.string.contactcountlabel), settings.getInt("contactsCount", 0));
        else text = "0";

        contactsText.setText(text);

        languagesRow = (TableRow) getView().findViewById(R.id.languagesrow);
        feedbackRow = (TableRow) getView().findViewById(R.id.feedbackrow);
        policiesRow = (TableRow) getView().findViewById(R.id.policiesrow);
        aboutusRow = (TableRow) getView().findViewById(R.id.aboutusrow);

        //set all the settings listeners
        rowListener(contactsRow, new ContactsFragment());
        rowListener(languagesRow, new LanguagesFragment());
        rowListener(feedbackRow, new FeedbackFragment());
        rowListener(policiesRow, new PoliciesFragment());
        rowListener(aboutusRow, new AboutUsFragment());

        //get reference to switch objects
        emailSwitch = (Switch) getView().findViewById(R.id.notificationsemailswitch);
        smsSwitch = (Switch) getView().findViewById(R.id.notificationssmsswitch);
        pushSwitch = (Switch) getView().findViewById(R.id.notificationspushswitch);
        familySwitch = (Switch) getView().findViewById(R.id.notificationsfamilyswitch);

        //check switch states
        switchToggle(settings, emailSwitch, "mail");
        switchToggle(settings, smsSwitch, "sms");
        switchToggle(settings, pushSwitch, "push");
        switchToggle(settings, familySwitch, "family");

        //set all the switch listeners
        switchListener(settings, emailSwitch, "mail");
        switchListener(settings, smsSwitch, "sms" );
        switchListener(settings, pushSwitch, "push");
        switchListener(settings, familySwitch, "family");
    }

    private String getToken() {
        SharedPreferences credentials = this.getActivity().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        String storedToken = credentials.getString(ValuesCollection.TOKEN_KEY, null);
        return storedToken;
    }

    private void switchToggle(SharedPreferences settings, CompoundButton slideSwitch, String name){
        if(settings.getBoolean(name, false)){slideSwitch.toggle();}
    }

    private void switchListener(final SharedPreferences settings, CompoundButton slideSwitch,final String name){

        slideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //get the settings from sharedpreferences
                SharedPreferences.Editor editor = settings.edit();

                //set email status
                if (isChecked) {
                    editor.putBoolean(name, true);
                } else {
                    editor.putBoolean(name, false);
                }

                editor.commit();

                System.out.println("Current " + name + " status: " + settings.getBoolean(name, false));

            }
        });
    }

    private void rowListener(TableRow row, final Fragment frag) {
        row.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, frag).addToBackStack(null).commit();
            }
        });
    }
}
