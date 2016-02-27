package Fragments;

import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;
import com.hmkcode.locations.sentineluprm15.R;

import OtherHandlers.ValuesCollection;

/**
 * This fragment manages the toggles in the settings.
 */
public class SettingsFragment extends Fragment {

    private TableRow contactsRow;
    private TableRow languagesRow;
    private TableRow feedbackRow;
    private TableRow policiesRow;
    private TableRow aboutusRow;

    private Switch emailSwitch;
    private Switch smsSwitch;
    private Switch pushSwitch;
    private Switch familySwitch;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        Context context = getContext();
        CharSequence text = "left fragment";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onPause() {
        Context context = getContext();
        CharSequence text = "left fragment";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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

        //get reference to settings shared preferences
        SharedPreferences settings = getContext().getSharedPreferences(ValuesCollection.SETTINGS_SP, 0);

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

                Context context = getContext();
                CharSequence text = "Flipped Switch";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                /*
                final CryptographyHandler crypto;

                try {
                    crypto = new CryptographyHandler();

                    JSONObject alertJSON = new JSONObject();

                    alertJSON.put("token", getToken());

                    Ion.with(getContext())
                            .load(ValuesCollection.SETTINGS_URL)
                            .setBodyParameter(ValuesCollection.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(alertJSON))
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    System.out.println(result);
                                    try {
                                        JSONObject receivedSentinelMessage = JSONHandler.convertStringToJSON(result);
                                        String encryptedJSONReceived = JSONHandler.getSentinelMessage(receivedSentinelMessage);
                                        String decryptedJSONReceived = crypto.decryptString(encryptedJSONReceived);

                                        JSONObject receivedJSON = JSONHandler.convertStringToJSON(decryptedJSONReceived);

                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    } catch (CryptorException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (CryptorException e) {
                    e.printStackTrace();
                }
                */
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
