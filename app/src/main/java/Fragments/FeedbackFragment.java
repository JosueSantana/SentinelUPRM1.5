package Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import OtherHandlers.ValuesCollection;
import edu.uprm.Sentinel.R;

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
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_feedback, container, false);

        TextView titleView = (TextView) root.findViewById(R.id.feedbacktitle);
        final AutoCompleteTextView hintView = (AutoCompleteTextView) root.findViewById(R.id.myFeedback);
        TextView footerView = (TextView) root.findViewById(R.id.feedbackmessage);
        Button submitButton = (Button) root.findViewById(R.id.button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("lollll");

                final CryptographyHandler crypto;
                try {
                    crypto = new CryptographyHandler();

                    Runnable r = new Runnable(){
                        @Override
                        public void run() {
                            try{
                                String URL = isReportProblem ? ValuesCollection.REPORT_PROBLEM_URL : ValuesCollection.SEND_FEEDBACK_URL;

                                JSONObject registerJSON = new JSONObject();
                                registerJSON.put("message", hintView.getText());
                                registerJSON.put("os", ValuesCollection.ANDROID_OS_STRING);
                                registerJSON.put("token", getToken());

                                Ion.with(getContext())
                                        .load("PUT", URL)
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

                        private String getToken() {
                            SharedPreferences credentials = getActivity().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
                            String storedToken = credentials.getString(ValuesCollection.TOKEN_KEY, null);
                            return storedToken;
                        }
                    };

                    new Thread(r).start();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        titleView.setText(title);
        hintView.setHint(hint);
        footerView.setText(footer);
        return root;
    }

}
