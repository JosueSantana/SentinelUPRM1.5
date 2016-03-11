package edu.uprm.Sentinel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import android.os.Bundle;
import android.view.KeyEvent;

import android.view.View;

import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import edu.uprm.Sentinel.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Fragments.EmergencyFragment;
import Fragments.SentinelDialogFragment;
import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import OtherHandlers.ValuesCollection;

/**
 * This activity handles the inputs for the login menu.
 */
public class SignupActivity extends FragmentActivity {

    private ImageButton phoneButton;
    private ImageButton proceedButton;
    private AutoCompleteTextView emailText;
    private EditText editText;

    private FragmentManager fm = getSupportFragmentManager();
    private SharedPreferences credentials;
    private SharedPreferences.Editor editor;

    GoogleCloudMessaging gcm;
    InstanceID instanceID;
    String token;
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getRegId();

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        credentials = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        editor = credentials.edit();

        editor.putBoolean("atSignup", true).commit();

        //fix orientation on Portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // Create listener for Alert Button
        phoneButton = (ImageButton) findViewById(R.id.phoneOnSignup);
        phoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction().add(R.id.signupRLayout, new EmergencyFragment()).addToBackStack("EmergencyFromSignUp").commit();
            }
        });

        // Create listener for text field action
        emailText = (AutoCompleteTextView) findViewById(R.id.email);
        editText = (EditText) findViewById(R.id.phone);
        final List<TextView> l = new ArrayList<TextView>();
        l.add(emailText);
        l.add(editText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_GO) {

                    try {

                        // Check if no view has focus:
                        View view = SignupActivity.this.getCurrentFocus();
                        if (view != null) {
                            emailText.clearFocus();
                            editText.clearFocus();
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        toggleUIClicking(false);

                        attemptSignup(l);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (CryptorException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handled = true;
                }
                return handled;
            }
        });

        // Create listener for Proceed Button
        proceedButton = (ImageButton) findViewById(R.id.proceedsignup);
        proceedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptSignup(l);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (CryptorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private boolean checkFormat(String email, String pattern) {
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(email);

        return m.find();
    }

    private void showSignupError(int titleID, int messageID) {
        //prepare strings to pass to Fragment through Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("dialogtitle", titleID);
        bundle.putInt("dialogmessage", messageID);

        //Call up AlertDialog
        SentinelDialogFragment dialogFragment = new SentinelDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "Alert Dialog Fragment");
    }

    private void attemptSignup(List textViews) throws JSONException, CryptorException, IOException {

        spinner.setVisibility(View.VISIBLE);

        //These are the strings corresponding to user input (potentially pass to a handling function)
        final String email = ((AutoCompleteTextView) textViews.get(0)).getText().toString();
        final String phone = ((EditText) textViews.get(1)).getText().toString();

        //manage failure conditions
        if (email.isEmpty() || phone.isEmpty()) {
            spinner.setVisibility(View.GONE);
            showSignupError(R.string.emptyformatalerttitle, R.string.emptyformatalertmessage);
            toggleUIClicking(true);
        } else if (!checkFormat(email, "(.*)(@uprm?\\.edu)($)")) {
            spinner.setVisibility(View.GONE);
            showSignupError(R.string.incorrectemailformattitle, R.string.incorrectemailformatmessage);
            toggleUIClicking(true);
        } else if (!checkFormat(phone, "[0-9]{10}$")) {
            spinner.setVisibility(View.GONE);
            showSignupError(R.string.incorrectphoneformattitle, R.string.incorrectphoneformatmessage);
            toggleUIClicking(true);
        } else {
            //ideally this toast will be replaced soon
            //Toast.makeText(SignupActivity.this, R.string.sendverificationalerttitle, Toast.LENGTH_SHORT).show();

            final Intent veriIntent = new Intent(SignupActivity.this, VerificationActivity.class);
            veriIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            final CryptographyHandler crypto = new CryptographyHandler();

            /*
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            */

            //String token = getGCMToken();

            //registerJSON.put("deviceID", Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID).toString());
            //registerJSON.put("deviceID", instanceID.getToken(ValuesCollection.ANDROID_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE));

            Runnable r = new Runnable(){
                @Override
                public void run() {
                    try{
                    JSONObject registerJSON = new JSONObject();
                    registerJSON.put("email", email);
                    registerJSON.put("phone", phone);
                    registerJSON.put("os", ValuesCollection.ANDROID_OS_STRING);
                    registerJSON.put("deviceID", token);

                    final SharedPreferences.Editor credentialsEditor = credentials.edit();

                    credentialsEditor.putString(ValuesCollection.ANDROID_SENDER_ID, token);
                    credentialsEditor.commit();
                    Ion.with(getBaseContext())
                            .load(ValuesCollection.REGISTER_URL)
                            .setBodyParameter(ValuesCollection.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String receivedJSON) {
                                    // Successful Request
                                    if (requestIsSuccessful(e)) {
                                        JSONObject decryptedValue = getDecryptedValue(receivedJSON);
                                        System.out.println(decryptedValue);

                                        // Received Success Message
                                        if (receivedSuccessMessage(decryptedValue)) {
                                            SharedPreferences credentials = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
                                            SharedPreferences.Editor emailEditor = credentials.edit();
                                            emailEditor.putString(ValuesCollection.EMAIL_KEY, email);
                                            emailEditor.commit();
                                        }

                                        // Message Was Not Successful.
                                        else {

                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                spinner.setVisibility(View.GONE);
                                                startActivity(veriIntent);
                                                finish();
                                            }
                                        });

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
                                        JSONObject receivedJSON = JSONHandler.convertStringToJSON(receivedJSONString);
                                        String encryptedStringValue = JSONHandler.getSentinelMessage(receivedJSON);
                                        String decryptedStringValue = crypto.decryptString(encryptedStringValue);
                                        JSONObject decryptedJSON = JSONHandler.convertStringToJSON(decryptedStringValue);
                                        return decryptedJSON;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (CryptorException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            });
                } catch (CryptorException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            new Thread(r).start();


        }
    }

    public void toggleUIClicking(boolean toggler){
        emailText.setClickable(toggler);
        emailText.setFocusable(toggler);
        emailText.setFocusableInTouchMode(toggler);

        editText.setClickable(toggler);
        editText.setFocusable(toggler);
        editText.setFocusableInTouchMode(toggler);

        phoneButton.setClickable(toggler);
        phoneButton.setFocusable(toggler);
        phoneButton.setFocusableInTouchMode(toggler);

        proceedButton.setClickable(toggler);
        proceedButton.setFocusable(toggler);
        proceedButton.setFocusableInTouchMode(toggler);
    }


    /*
        Get the Instance ID From Google.
     */
    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                instanceID = InstanceID.getInstance(getApplicationContext());
                try {
                    String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    return token;
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                token = msg;
            }
        }.execute(null, null, null);
    }
}