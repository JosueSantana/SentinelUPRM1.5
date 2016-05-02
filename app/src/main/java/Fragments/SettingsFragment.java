package Fragments;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
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

import OtherHandlers.Constants;
import OtherHandlers.HttpHelper;
import OtherHandlers.Toasts;
import edu.uprm.Sentinel.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import edu.uprm.Sentinel.SplashActivity;

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

    private SharedPreferences credentials;
    private SharedPreferences.Editor editor;

    private SharedPreferences settings;
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
        credentials = this.getActivity().getSharedPreferences(Constants.CREDENTIALS_SP, 0);
        editor = credentials.edit();

        settings = this.getActivity().getSharedPreferences(Constants.SETTINGS_SP, 0);

        //get reference to row objects
        contactsRow = (TableRow) getView().findViewById(R.id.contactsrow);

        //set the contacts count
        TextView contactsText = (TextView) contactsRow.getChildAt(1);
        settings = this.getActivity().getSharedPreferences(Constants.SETTINGS_SP, 0);
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


        //set all the switch listeners
        switchListener(settings, emailSwitch, "mail");
        switchListener(settings, smsSwitch, "sms");
        switchListener(settings, pushSwitch, "push");
        switchListener(settings, familySwitch, "family");
//
//        emailSwitch.setChecked(settings.getBoolean("mail", true));
//        smsSwitch.setChecked(settings.getBoolean("sms", true));
//        pushSwitch.setChecked(settings.getBoolean("push", true));
//        familySwitch.setChecked(settings.getBoolean("family", true));

        //check switch states
        switchToggle(emailSwitch, "mail");
        switchToggle(smsSwitch, "sms");
        switchToggle(pushSwitch, "push");
        switchToggle(familySwitch, "family");

    }

    private void switchToggle(CompoundButton slideSwitch, String name){
        if(settings.getBoolean(name, true)){slideSwitch.toggle();}
    }

    private void switchListener(final SharedPreferences settings, CompoundButton slideSwitch,final String name){

        slideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //get the settings from sharedpreferences
                SharedPreferences.Editor editor = settings.edit();
                System.out.println("SETTING CHECKED/UNCHECKED");
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
                fm.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.mainLayout, frag).addToBackStack(null).commit();
            }
        });
    }

    public Runnable postSettings(String setting){

        final String name = setting;

        String values = "";

        switch (setting) {
            case "mail": values =  Constants.EMAIL_KEY;
                break;
            case "push": values =  Constants.PUSH_KEY;
                break;
            case "sms": values =  Constants.SMS_KEY;
                break;
            case "family":  values = Constants.FAMILY_KEY;
                break;
        }

        final String valuesFinal = values;

        return new Runnable() {
            @Override
            public void run() {
                final CryptographyHandler crypto;

                JSONObject registerJSON = new JSONObject();
                try {
                    crypto = new CryptographyHandler();

                    registerJSON.put("token", Constants.getToken(getContext()));
                    registerJSON.put(valuesFinal, settings.getBoolean(name, false));

                    Ion.with(getContext())
                            .load(Constants.SETTINGS_URL)
                            .setBodyParameter(Constants.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String receivedJSON) {
                                    // Successful Request
                                    if(HttpHelper.requestIsSuccessful(e)){
                                        JSONObject decryptedValue = crypto.getDecryptedValue(receivedJSON);

                                        if(HttpHelper.receivedSuccessMessage(decryptedValue, "2")){
                                            editor.putBoolean("sessionDropped", true).commit();
                                            // delete token here.
                                            Intent splashIntent = new Intent(getActivity(), SplashActivity.class);
                                            splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
                                            startActivity(splashIntent);
                                        }
                                        else if(HttpHelper.receivedSuccessMessage(decryptedValue, "3")){

                                            Toasts.genericErrorToast(getContext());
                                        }
                                    } else {
                                        Toasts.connectionErrorToast(getContext());
                                    }
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
