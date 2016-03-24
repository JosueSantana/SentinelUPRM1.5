package Fragments;

import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
public class SettingsFragment extends Fragment {

    private TableRow contactsRow;
    private TableRow languagesRow;
    private TableRow feedbackRow;
    private TableRow policiesRow;
    private TableRow aboutusRow;
    private TableRow unsubscribeRow;

    private Switch emailSwitch;
    private Switch smsSwitch;
    private Switch pushSwitch;
    private Switch familySwitch;

    SharedPreferences settings;
    SharedPreferences credentials;
    FragmentManager fm;

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


        fm = getActivity().getSupportFragmentManager();
        credentials = this.getActivity().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        settings = this.getActivity().getSharedPreferences(ValuesCollection.SETTINGS_SP, 0);

        //get reference to row objects
        contactsRow = (TableRow) getView().findViewById(R.id.contactsrow);

        //set the contacts count
        TextView contactsText = (TextView) contactsRow.getChildAt(1);
        settings = this.getActivity().getSharedPreferences(ValuesCollection.SETTINGS_SP, 0);
        String text;

        if(settings.contains("contactsCount"))
             text = String.format(getResources().getString(R.string.contactcountlabel),
                     settings.getInt("contactsCount", 0));
        else text = "0 out of 5";

        contactsText.setText(text);

        languagesRow = (TableRow) getView().findViewById(R.id.languagesrow);
        feedbackRow = (TableRow) getView().findViewById(R.id.feedbackrow);
        policiesRow = (TableRow) getView().findViewById(R.id.policiesrow);
        aboutusRow = (TableRow) getView().findViewById(R.id.aboutusrow);
        unsubscribeRow = (TableRow) getView().findViewById(R.id.unsubscriberow);

        //set all the settings listeners
        rowListener(contactsRow, new ContactsFragment());
        rowListener(languagesRow, new LanguagesFragment());

        feedbackRow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showFeedbackDialog(R.string.feedbacklabel, R.string.okmessage,
                        R.string.okmessage, R.array.policy_options, "feedback", false);
            }
        });

        rowListener(policiesRow, new PoliciesFragment());
        rowListener(aboutusRow, new AboutUsFragment());

        unsubscribeRow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showUnsubscribeDialog(R.string.unsubscribedialogtitle, R.string.unsubscribedialogmessage,
                        R.string.okmessage, R.string.cancelmessage, "unsubscribe", true);
            }
        });

        //get reference to switch objects
        emailSwitch = (Switch) getView().findViewById(R.id.notificationsemailswitch);
        smsSwitch = (Switch) getView().findViewById(R.id.notificationssmsswitch);
        pushSwitch = (Switch) getView().findViewById(R.id.notificationspushswitch);
        familySwitch = (Switch) getView().findViewById(R.id.notificationsfamilyswitch);

        //initialize all settings to true
        emailSwitch.setChecked(true);
        smsSwitch.setChecked(true);
        pushSwitch.setChecked(true);
        familySwitch.setChecked(true);

        //set all the switch listeners
        switchListener(settings, emailSwitch, "mail");
        switchListener(settings, smsSwitch, "sms");
        switchListener(settings, pushSwitch, "push");
        switchListener(settings, familySwitch, "family");

        //check switch states
        switchToggle(emailSwitch, "mail");
        switchToggle(smsSwitch, "sms");
        switchToggle(pushSwitch, "push");
        switchToggle(familySwitch, "family");

    }

    private String getToken() {
        credentials = this.getActivity().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        String storedToken = credentials.getString(ValuesCollection.TOKEN_KEY, null);
        return storedToken;
    }

    private void switchToggle(CompoundButton slideSwitch, String name){
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


                Thread postSettingsThread = new Thread(postSettings(name));
                postSettingsThread.start();

                System.out.println("Current " + name + " status: " + settings.getBoolean(name, false));


            }
        });
    }

    private void rowListener(TableRow row, final Fragment frag) {
        row.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction().replace(R.id.mainLayout, frag).addToBackStack(null).commit();
            }
        });
    }

    public Runnable postSettings(String setting){

        final String name = setting;

        String values = "";

        switch (setting) {
            case "mail": values =  ValuesCollection.EMAIL_KEY;
                break;
            case "push": values =  ValuesCollection.PUSH_KEY;
                break;
            case "sms": values =  ValuesCollection.SMS_KEY;
                break;
            case "family":  values = ValuesCollection.FAMILY_KEY;
                break;
        }

        final String valuesFinal = values;

        return new Runnable() {
            @Override
            public void run() {
                System.out.println("IS THIS RUNNING?!?!?!");
                final CryptographyHandler crypto;

                JSONObject registerJSON = new JSONObject();
                try {
                    crypto = new CryptographyHandler();

                    registerJSON.put("token", getToken());
                    registerJSON.put(valuesFinal, settings.getBoolean(name, false));
                    Ion.with(getContext())
                            .load(ValuesCollection.SETTINGS_URL)
                            .setBodyParameter(ValuesCollection.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String receivedJSON) {
                                    // Successful Request
                                    if (requestIsSuccessful(e)) {

                                        //JSONObject decryptedValue = getDecryptedValue(receivedJSON);
                                        //System.out.println(decryptedValue);

                                        //Context context = getContext();
                                        //CharSequence text = "";
                                        // Received Success Message
                                            /*if (receivedSuccessMessage(decryptedValue)) {
                                                text = "Settings Successfully Updated ";
                                            }
                                            // Message Was Not Successful.
                                            else {
                                                text = "There was an Error Updating Your Settings";
                                            }
                                            int duration = Toast.LENGTH_SHORT;
                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();*/
                                    }
                                    // Errors
                                    else {

                                    }
                                }

                                // Extract Success Message From Received JSON.
                                private boolean receivedSuccessMessage(JSONObject decryptedValue) {
                                    String success = null;
                                    try {
                                        success = decryptedValue.getString("success");
                                        return success.equals("1");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    return false;
                                }

                                // Verify if there was an Error in the Request.
                                private boolean requestIsSuccessful(Exception e) {
                                    return e == null;
                                }

                                // Convert received JSON String into a Decrypted JSON.
                                private JSONObject getDecryptedValue(String receivedJSONString) {
                                    try {
                                        JSONObject receivedJSON =
                                                JSONHandler.convertStringToJSON(receivedJSONString);
                                        String encryptedStringValue =
                                                JSONHandler.getSentinelMessage(receivedJSON);
                                        String decryptedStringValue =
                                                crypto.decryptString(encryptedStringValue);
                                        JSONObject decryptedJSON =
                                                JSONHandler.convertStringToJSON(decryptedStringValue);
                                        return decryptedJSON;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (CryptorException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (CryptorException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private void showFeedbackDialog(int titleID, int positiveID, int negativeID, int optionsListID, String kind, boolean hasNeg) {
        //prepare strings to pass to Fragment through Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("dialogtitle", titleID);
        bundle.putInt("optionslist", optionsListID);
        bundle.putInt("positivetitle", positiveID);
        bundle.putInt("negativetitle", negativeID);
        bundle.putString("intentkind", kind);
        bundle.putBoolean("hasneg", hasNeg );

        //Call up AlertDialog
        IntentDialogOptionsFragment dialogFragment = new IntentDialogOptionsFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "Proceed Dialog Fragment");
    }

    private void showUnsubscribeDialog(int titleID, int messageID, int positiveID, int negativeID, String kind, boolean hasNeg) {
        //prepare strings to pass to Fragment through Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("dialogtitle", titleID);
        bundle.putInt("dialogmessage", messageID);
        bundle.putInt("positivetitle", positiveID);
        bundle.putInt("negativetitle", negativeID);
        bundle.putString("intentkind", kind);
        bundle.putBoolean("hasneg", hasNeg );

        //Call up AlertDialog
        IntentDialogFragment dialogFragment = new IntentDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "Proceed Dialog Fragment");
    }
}
