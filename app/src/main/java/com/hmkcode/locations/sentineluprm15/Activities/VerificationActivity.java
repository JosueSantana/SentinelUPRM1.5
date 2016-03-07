package com.hmkcode.locations.sentineluprm15.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import edu.uprm.Sentinel.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import Fragments.EmergencyFragment;
import Fragments.SentinelDialogFragment;
import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import OtherHandlers.ValuesCollection;

/**
 * Created by a136803 on 2/3/16.
 */
public class VerificationActivity extends AppCompatActivity {

    private ImageButton phoneButton;
    private ImageButton proceedButton;
    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //fix orientation on Portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Create listener for Alert Button
        phoneButton = (ImageButton) findViewById(R.id.phoneOnSignup2);
        phoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().add(R.id.verificationRLayout, new EmergencyFragment()).addToBackStack("EmergencyFromVerification").commit();
            }
        });

        //create listener for "GO" edit text action
        final EditText editText = (EditText) findViewById(R.id.codeEnter);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_GO) {
                    try {
                        attemptVerification(editText.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (CryptorException e) {
                        e.printStackTrace();
                    }
                    handled = true;
                }
                return handled;
            }
        });

        // Create listener for Proceed Button
        proceedButton = (ImageButton) findViewById(R.id.proceedverification);
        proceedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptVerification(editText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (CryptorException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void attemptVerification(String vCode) throws JSONException, CryptorException {

        //TODO: call encryption mechanism
        //TODO: call request handler
        //TODO: manage failure conditions

        //manage failure conditions
        if(vCode.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putInt("dialogtitle", R.string.emptyformatalerttitle);
            bundle.putInt("dialogmessage", R.string.emptyformatalertmessage);
            SentinelDialogFragment dialogFragment = new SentinelDialogFragment();
            dialogFragment.setArguments(bundle);
            // Show Alert DialogFragment
            dialogFragment.show(fm, "Alert Dialog Fragment");
        }
        else {

            /*
            SharedPreferences credentials = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
            SharedPreferences.Editor editor = credentials.edit();
             */

            final CryptographyHandler crypto = new CryptographyHandler();

            SharedPreferences credentials = getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
            final SharedPreferences.Editor credentialsEditor = credentials.edit();
            String emailAddress = credentials.getString(ValuesCollection.EMAIL_KEY, null);

            JSONObject verificationJSON = new JSONObject();
            verificationJSON.put("email", emailAddress);
            verificationJSON.put("checkToken", vCode);

            Ion.with(getBaseContext())
                    .load(ValuesCollection.PASSCODE_VALIDATION_URL)
                    .setBodyParameter(ValuesCollection.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(verificationJSON))
                    .asString()
                    .setCallback(new FutureCallback<String>() {

                        @Override
                        public void onCompleted(Exception e, String receivedJSON) {
                            // Successful Request
                            if (requestIsSuccessful(e)) {
                                JSONObject decryptedValue = getDecryptedValue(receivedJSON);

                                // Received Success Message
                                if (receivedSuccessMessage(decryptedValue)) {
                                    // Store token in Shared Preferences.
                                    String token = getToken(decryptedValue);
                                    storeToken(token);
                                    userIsVerified();
                                    startMainActivity();
                                } else if(userEntersIncorrectPasscode(decryptedValue)){
                                    Context context = getApplicationContext();
                                    CharSequence text = "Inputted Passcode is Incorrect";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                                // Message Was Not Successful.
                                else {}
                            }
                            // Request Was Not Successful
                            else {}
                        }

                        // User Enters Incorrect Passcode (success:2).
                        private boolean userEntersIncorrectPasscode(JSONObject decryptedValue) {
                            String success = null;
                            try {
                                success = decryptedValue.getString("success");
                                return success.equals("2");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }

                        // Store Token.
                        private void storeToken(String token) {
                            credentialsEditor.putString(ValuesCollection.TOKEN_KEY, token);
                            credentialsEditor.commit();
                        }

                        // Extract Token from JSON.
                        private String getToken(JSONObject decryptedValue) {
                            try {
                                return decryptedValue.getString("token");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        private void startMainActivity() {
                            Intent veriIntent = new Intent(VerificationActivity.this, MainActivity.class);
                            veriIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
                            startActivity(veriIntent);
                        }

                        //
                        private void userIsVerified() {
                            credentialsEditor.putBoolean("isVerified", true).commit();
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

                        /*
                        @Override
                        public void onCompleted(Exception e, String result) {
                            System.out.println(result);
                            try {
                                JSONObject receivedSentinelMessage = JSONHandler.convertStringToJSON(result);
                                String encryptedJSONReceived = receivedSentinelMessage.getString(ValuesCollection.SENTINEL_MESSAGE_KEY);
                                String decryptedJSONReceived = crypto.decryptString(encryptedJSONReceived);

                                JSONObject receivedJSON = JSONHandler.convertStringToJSON(decryptedJSONReceived);

                                // If successfully registered and user received the Token from the Server.
                                if(receivedJSON.getString("success").equals("1")) {
                                    // Store token in Shared Preferences.
                                    String token = receivedJSON.getString("token");
                                    credentialsEditor.putString(ValuesCollection.TOKEN_KEY,token);
                                    credentialsEditor.commit();
                                } else if (receivedJSON.getString("success").equals("2")) { // Wrong token inputted

                                } else {

                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            } catch (CryptorException e1) {
                                e1.printStackTrace();
                            }
                        }
                        */
                    });
        }

    }

}