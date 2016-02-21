package Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hmkcode.locations.sentineluprm15.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import OtherHandlers.ValuesCollection;

/**
 * This fragment listens on user input when reporting.
 */
public class ReportFragment extends Fragment {

    private ImageButton mButton;

    public ReportFragment() {
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
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mButton = (ImageButton) getView().findViewById(R.id.alertButton);

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.mainLayout, new CountdownFragment()).addToBackStack("ReportFragment").commit();

                System.out.println("lol");

                Context context = getContext();
                CharSequence text = getToken();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                /*
                try {
                    sendAlert();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (CryptorException e) {
                    e.printStackTrace();
                }
                */
            }
            /*public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.mainLayout, new CountdownFragment()).addToBackStack("ReportFragment").commit();}*/
        });
    }

    private String getToken() {
        SharedPreferences credentials = this.getActivity().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        String storedToken = credentials.getString(ValuesCollection.TOKEN_KEY, null);
        return storedToken;
    }

    private void sendAlert() throws JSONException, CryptorException {

        final CryptographyHandler crypto = new CryptographyHandler();

        JSONObject alertJSON = new JSONObject();

        alertJSON.put("token", getToken());
        alertJSON.put("latitude", "18.2338540");
        alertJSON.put("longitude", "-67.1337090");

        Ion.with(getContext())
                .load(ValuesCollection.SEND_ALERT_URL)
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
    }

    private void onClick() {

    }

    private void returnToAlertActivity(){

    }

    private void beginReport(){

    }
}