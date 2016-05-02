package edu.uprm.Sentinel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import Fragments.EmergencyFragment;
import Fragments.IntentDialogFragment;
import Fragments.SimpleDialogFragment;
import OtherHandlers.Constants;
import OtherHandlers.CryptographyHandler;
import OtherHandlers.DialogCaller;
import OtherHandlers.HttpHelper;
import OtherHandlers.JSONHandler;
import OtherHandlers.NetworkUtil;
import OtherHandlers.Toasts;

import static OtherHandlers.JSONHandler.getTokenFromJSON;

/**
 * Created by a136803 on 2/3/16.
 */
public class VerificationActivity extends AppCompatActivity implements DialogCaller {

    private ImageButton phoneButton;
    private ImageButton proceedButton;
    private FragmentManager fm = getSupportFragmentManager();
    private ProgressBar spinner;
    private EditText editText;
    private TextView goBackText;
    private SharedPreferences credentials;
    private SharedPreferences.Editor credentialsEditor;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //this.token = getIntent().getStringExtra("token");

        //fix orientation on Portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        credentials = getSharedPreferences(Constants.CREDENTIALS_SP, 0);
        credentialsEditor = credentials.edit();

        // Create listener for Alert Button
        phoneButton = (ImageButton) findViewById(R.id.phoneOnSignup2);
        phoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().add(R.id.verificationRLayout, new EmergencyFragment()).addToBackStack("EmergencyFromVerification").commit();
            }
        });

        goBackText = (TextView) findViewById(R.id.codenotreceived);
        goBackText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showGoBackMessage(R.string.gobacktitle, R.string.gobackmessage,
                                R.string.continuemessage, R.string.cancelmessage, true);
                    }
                });
            }
        });

        //create listener for "GO" edit text action
        editText = (EditText) findViewById(R.id.codeEnter);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_GO && NetworkUtil.getConnectivityStatus(getBaseContext()) != NetworkUtil.TYPE_NOT_CONNECTED ) {
                    try {


                        // Check if no view has focus:
                        View view = VerificationActivity.this.getCurrentFocus();
                        if (view != null) {
                            editText.clearFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        toggleUIClicking(false);
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

    private void showVerificationError(int titleID, int messageID) {
        //prepare strings to pass to Fragment through Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("dialogtitle", titleID);
        bundle.putInt("dialogmessage", messageID);

        //Call up AlertDialog
        SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "Confirm Dialog Fragment");
    }

    private void showGoBackMessage(int titleID, int messageID, int positiveID, int negativeID, boolean hasNeg) {
        //prepare strings to pass to Fragment through Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("dialogtitle", titleID);
        bundle.putInt("dialogmessage", messageID);
        bundle.putInt("positivetitle", positiveID);
        bundle.putInt("negativetitle", negativeID);
        bundle.putBoolean("hasneg", hasNeg);

        //Call up AlertDialog
        IntentDialogFragment dialogFragment = new IntentDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "Proceed Dialog Fragment");
    }

    public void toggleUIClicking(boolean toggler){
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

    private void attemptVerification(String vCode) throws JSONException, CryptorException {

        //TODO: call encryption mechanism
        //TODO: call request handler
        //TODO: manage failure conditions

        spinner.setVisibility(View.VISIBLE);

        //manage failure conditions
        if(vCode.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putInt("dialogtitle", R.string.emptyformatalerttitle);
            bundle.putInt("dialogmessage", R.string.emptyformatalertmessage);
            SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
            dialogFragment.setArguments(bundle);
            // Show Alert DialogFragment
            dialogFragment.show(fm, "Alert Dialog Fragment");
            spinner.setVisibility(View.GONE);
        }
        else {

            /*
            SharedPreferences credentials = getSharedPreferences(Constants.CREDENTIALS_SP, 0);
            SharedPreferences.Editor editor = credentials.edit();
             */

            final CryptographyHandler crypto = new CryptographyHandler();
            String emailAddress = credentials.getString(Constants.EMAIL_KEY, null);

            final JSONObject verificationJSON = new JSONObject();
            verificationJSON.put("email", emailAddress);
            verificationJSON.put("checkToken", vCode);

            Runnable r = new Runnable(){

                @Override
                public void run() {
                    try {
                        Ion.with(getBaseContext())
                                .load(Constants.PASSCODE_VALIDATION_URL)
                                .setBodyParameter(Constants.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(verificationJSON))
                                .asString()
                                .setCallback(new FutureCallback<String>() {

                                    @Override
                                    public void onCompleted(Exception e, String receivedJSON) {
                                        // Successful Request
                                        if (HttpHelper.requestIsSuccessful(e)) {
                                            JSONObject decryptedValue = crypto.getDecryptedValue(receivedJSON);

                                            // Received Success Message
                                            if (HttpHelper.receivedSuccessMessage(decryptedValue, "1")) {
                                                // Store token in Shared Preferences.
                                                String token = null;
                                                try {
                                                    token = getTokenFromJSON(decryptedValue);
                                                    Constants.storeToken(getApplicationContext(), token);
                                                    userIsVerified();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            spinner.setVisibility(View.GONE);
                                                            startMainActivity();
                                                            finish();
                                                        }
                                                    });
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                    Toasts.genericErrorToast(VerificationActivity.this.getApplicationContext());
                                                }

                                            } else if(HttpHelper.receivedSuccessMessage(decryptedValue, "2")){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        spinner.setVisibility(View.GONE);
                                                        showVerificationError(R.string.wrongcodetitle, R.string.wrongcodemessage);
                                                        toggleUIClicking(true);
                                                    }
                                                });
                                            }
                                            // Message Was Not Successful.
                                            else {
                                                Toasts.genericErrorToast(VerificationActivity.this.getApplicationContext());
                                            }
                                        }
                                        // Request Was Not Successful
                                        else {
                                            Toasts.connectionErrorToast(VerificationActivity.this.getApplicationContext());
                                        }
                                        spinner.setVisibility(View.GONE);
                                    }
                                    private void startMainActivity() {
                                        Intent veriIntent = new Intent(VerificationActivity.this, MainActivity.class);
                                        veriIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
                                        startActivity(veriIntent);
                                    }
                                    private void userIsVerified() {
                                        credentialsEditor.putBoolean("isVerified", true).commit();
                                    }
                                });
                    } catch (CryptorException e) {
                        e.printStackTrace();
                    }
                }
            };

            new Thread(r).start();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void doPositiveClick(Bundle bundle) {
        System.out.println("DOING POSITIVE CLICK");

        credentialsEditor.remove(Constants.EMAIL_KEY).commit();
        final Intent veriIntent = new Intent(VerificationActivity.this, SignupActivity.class);
        veriIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(veriIntent);
        finish();
    }

    @Override
    public void doNegativeClick(Bundle bundle) {
        //do nothing
    }

    @Override
    public void doItemClick(Bundle bundle) {
        //does not apply
    }
}