package edu.uprm.Sentinel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import Fragments.FeedbackFragment;
import OtherHandlers.Constants;
import OtherHandlers.CryptographyHandler;
import OtherHandlers.DialogCaller;
import OtherHandlers.HttpHelper;
import OtherHandlers.JSONHandler;

import Fragments.ViewPagerFragment;
import OtherHandlers.Toasts;

public class MainActivity extends AppCompatActivity implements DialogCaller {

    private Toolbar toolbar;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();

        //fix orientation on Portrait
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //initiate main activity with viewpagerfragment embedded
        fm.beginTransaction().add(R.id.mainLayout, new ViewPagerFragment()).commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            Typeface myTypeface = Typeface.createFromAsset(getAssets(), "stentiga.ttf");
            toolbarTitle.setTypeface(myTypeface);
            //toolbarTitle.setGravity(Gravity.CENTER);
            //getSupportActionBar().setTitle("My custom toolbar!");
            //getSupportActionBar().setHomeButtonEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //SharedPreferences credentials = getBaseContext().getSharedPreferences(Constants.CREDENTIALS_SP, 0);
        //SharedPreferences.Editor editor = credentials.edit();
        //editor.clear().commit();
    }

    @Override
    public void doPositiveClick(Bundle bundle) {
        if(bundle != null && bundle.getString("intentkind").equals("unsubscribe")){
            final Intent unsubscribeIntent = new Intent(MainActivity.this, SignupActivity.class);
            unsubscribeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //clear all credentials
            getSharedPreferences(Constants.CREDENTIALS_SP, 0).edit().clear().commit();
            getSharedPreferences(Constants.SETTINGS_SP, 0).edit().clear().commit();


            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    final CryptographyHandler crypto;
                    try {
                        crypto = new CryptographyHandler();

                        JSONObject registerJSON = new JSONObject();
                        registerJSON.put(Constants.TOKEN_KEY, Constants.getToken(getApplicationContext()));

                        Ion.with(getApplicationContext())
                                .load(Constants.UNSUBSCRIBE_URL)
                                .setBodyParameter(Constants.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String receivedJSON) {
                                        // Successful Request
                                        Constants.deleteToken(getApplicationContext());
                                        if (HttpHelper.requestIsSuccessful(e)) {
                                            JSONObject decryptedValue = crypto.getDecryptedValue(receivedJSON);

                                            // Received Success Message
                                            if (HttpHelper.receivedSuccessMessage(decryptedValue, "1")) {
                                            }
                                            // Message Was Not Successful.
                                            else {
                                                Toasts.genericErrorToast(getApplicationContext());
                                            }
                                        }
                                        // Errors
                                        else {
                                            Toasts.connectionErrorToast(getApplicationContext());
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
            new Thread(r).start();

            startActivity(unsubscribeIntent);
            finish();
        }
        else if(bundle != null && bundle.getString("intentkind").equals("outofbounds")){
            getSupportFragmentManager().popBackStackImmediate();
        }

        else if(bundle != null && bundle.getString("intentkind").equals("timenotout")){
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void doNegativeClick(Bundle bundle) {

    }

    /*
    private String getToken() {
        SharedPreferences credentials = this.getSharedPreferences(Constants.CREDENTIALS_SP, 0);
        String storedToken = credentials.getString(Constants.TOKEN_KEY, null);
        return storedToken;
    }
    */

    @Override
    public void doItemClick(Bundle bundle) {

        if(bundle != null && bundle.getString("intentkind").equals("feedback")) {
            //preraparing fragment to send the title data
            FeedbackFragment feedbackFrag = new FeedbackFragment();
            Bundle feedbackBundle = new Bundle();

            //get the name of the view to access
            String viewName = getResources().getStringArray(R.array.policy_options)[bundle.getInt("position")];

            if (viewName.equals(getResources().getString(R.string.reportproblem))) {
                feedbackBundle.putString("title", getResources().getString(R.string.reportproblem));
                feedbackBundle.putString("hint", getResources().getString(R.string.problemhint));
                feedbackBundle.putString("footer", getResources().getString(R.string.reportproblemfooter));
                feedbackBundle.putBoolean("reportProblem", true);

            } else {
                feedbackBundle.putString("title", getResources().getString(R.string.reportfeedback));
                feedbackBundle.putString("hint", getResources().getString(R.string.feedbackhint));
                feedbackBundle.putString("footer", getResources().getString(R.string.reportfeedbackfooter));
                feedbackBundle.putBoolean("reportProblem", false);
            }

            feedbackFrag.setArguments(feedbackBundle);
            fm.beginTransaction().replace(R.id.mainLayout, feedbackFrag).addToBackStack(null).commit();
        }
    }


}