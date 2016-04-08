package Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import OtherHandlers.Constants;
import OtherHandlers.CryptographyHandler;
import OtherHandlers.HttpHelper;
import OtherHandlers.JSONHandler;
import edu.uprm.Sentinel.R;
import edu.uprm.Sentinel.SplashActivity;

public class FeedbackFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "title";
    private static final String ARG_PARAM2 = "hint";
    private static final String ARG_PARAM3 = "footer";
    private static final String ARG_PARAM4 = "reportProblem";

    // TODO: Rename and change types of parameters
    private String title;
    private String hint;
    private String footer;
    private boolean isReportProblem;
    private AutoCompleteTextView hintView;
    private ProgressBar spinner;
    private boolean sendingReport = false;

    private SharedPreferences credentials;
    private SharedPreferences.Editor editor;


    public FeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackFragment newInstance(String param1, String param2) {
        FeedbackFragment fragment = new FeedbackFragment();
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

            title = getArguments().getString(ARG_PARAM1);
            hint = getArguments().getString(ARG_PARAM2);
            footer = getArguments().getString(ARG_PARAM3);
            isReportProblem = getArguments().getBoolean(ARG_PARAM4);

            System.out.println("print title: " + title);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_feedback, container, false);

        TextView titleView = (TextView) root.findViewById(R.id.feedbacktitle);
        hintView = (AutoCompleteTextView) root.findViewById(R.id.myFeedback);
        TextView footerView = (TextView) root.findViewById(R.id.feedbackmessage);

        titleView.setText(title);
        hintView.setHint(hint);
        footerView.setText(footer);

        spinner = (ProgressBar) root.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        credentials = this.getActivity().getSharedPreferences(Constants.CREDENTIALS_SP, 0);
        editor = credentials.edit();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_feedback, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit_feedback:
                System.out.println("hintView: " + hintView);
                if(!hintView.getText().toString().equals("") && !sendingReport) {
                    final CryptographyHandler crypto;
                    sendingReport = true;
                    try {
                        crypto = new CryptographyHandler();

                        spinner.setVisibility(View.VISIBLE);

                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String URL = isReportProblem ? Constants.REPORT_PROBLEM_URL : Constants.SEND_FEEDBACK_URL;

                                    JSONObject registerJSON = new JSONObject();
                                    registerJSON.put("message", hintView.getText().toString());
                                    registerJSON.put("os", Constants.ANDROID_OS_STRING);
                                    registerJSON.put("token", getToken());

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            hintView.clearFocus();
                                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                                        }
                                    });


                                    Ion.with(getContext())
                                            .load("PUT", URL)
                                            .setBodyParameter(Constants.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                                            .asString()
                                            .setCallback(new FutureCallback<String>() {
                                                @Override
                                                public void onCompleted(Exception e, String receivedJSON) {
                                                    // Successful Request
                                                    if (HttpHelper.requestIsSuccessful(e)) {
                                                        JSONObject decryptedValue = crypto.getDecryptedValue(receivedJSON);
                                                        System.out.println(decryptedValue);

                                                        // Received Success Message
                                                        if (HttpHelper.receivedSuccessMessage(decryptedValue, "1")) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    spinner.setVisibility(View.GONE);
                                                                    showProceedMessage(R.string.reportalertonsendtitle, R.string.reportalertonsendmessage,
                                                                            R.string.okmessage, R.string.okmessage, false);
                                                                }
                                                            });
                                                            FeedbackFragment.this.getActivity().getSupportFragmentManager().popBackStackImmediate();
                                                        }

                                                        if(HttpHelper.receivedSuccessMessage(decryptedValue, "2")){
                                                            editor.putBoolean("sessionDropped", true).commit();

                                                            Intent splashIntent = new Intent(getActivity(), SplashActivity.class);
                                                            splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
                                                            startActivity(splashIntent);
                                                        }

                                                        // Message Was Not Successful.
                                                        else {

                                                        }
                                                    }
                                                    // Errors
                                                    else {

                                                    }
                                                }

                                                // Extract Success Message From Received JSON.
                                                /*
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
                                                */

                                            });
                                } catch (CryptorException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            private String getToken() {
                                SharedPreferences credentials = getActivity().getSharedPreferences(Constants.CREDENTIALS_SP, 0);
                                String storedToken = credentials.getString(Constants.TOKEN_KEY, null);
                                return storedToken;
                            }
                        };

                        new Thread(r).start();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if (!sendingReport){
                    showProceedMessage(R.string.emptyformatalerttitle, R.string.emptyformatalertmessage,
                            R.string.okmessage, R.string.okmessage, false);
                }

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void showProceedMessage(int titleID, int messageID, int positiveID, int negativeID, boolean hasNeg) {
        //prepare strings to pass to Fragment through Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("dialogtitle", titleID);
        bundle.putInt("dialogmessage", messageID);
        bundle.putInt("positivetitle", positiveID);
        bundle.putInt("negativetitle", negativeID);
        bundle.putBoolean("hasneg", hasNeg);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "Proceed Dialog Fragment");
    }
}
