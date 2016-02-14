package Fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hmkcode.locations.sentineluprm15.R;

import org.cryptonode.jncryptor.AES256JNCryptor;
import org.cryptonode.jncryptor.CryptorException;
import org.cryptonode.jncryptor.InvalidHMACException;
import org.cryptonode.jncryptor.JNCryptor;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;

public class CountdownFragment extends Fragment {
    public CountdownFragment() {
        // Required empty public constructor
    }

    private TextView countDownDisplay;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("---Created Countdown----");

        try {

            CryptographyHandler crypto = new CryptographyHandler();

            JSONObject receivedJSON = new JSONObject();
            receivedJSON.put("SentinelMessage", "AwFXWwsGAXxKtBU+tZsX9d0qGMjxGUY9zhi+Rizvwhj61wDH2M36LoJe31yCFsw/0IoqaXXpfOa/2SOIJoZ3CrYpv4b53dNQZQxbi5QLMg9AKA==");

            String receivedJSONString = receivedJSON.toString();

            JSONObject convertedJSON = JSONHandler.convertStringToJSON(receivedJSONString);
            String receivedKey = JSONHandler.getSentinelMessage(convertedJSON);

            String decryptedKey = crypto.decryptString(receivedKey);
            System.out.println(receivedKey);

            System.out.println("decrypted key is: " + decryptedKey);
            JSONObject receivedConfirmationValue = JSONHandler.convertStringToJSON(decryptedKey);
            String success = receivedConfirmationValue.get("success").toString();

            System.out.println(success);

            /*
            JNCryptor encryptor = new AES256JNCryptor();
            byte[] decryptedMessageArray = encryptor.decryptData(Base64.decode(receivedKey, 0), SENTINEL_ENCRYPTION_KEY.toCharArray());

            String decryptedMessage = new String(decryptedMessageArray, StandardCharsets.UTF_8);
            System.out.println(decryptedMessage);
            */

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (CryptorException e) {
            e.printStackTrace();
        }

        /*
        try {
            JSONObject receivedJSON = new JSONObject(receivedMessage);
            String encryptedMessage = receivedJSON.get("SentinelMessage").toString();
            System.out.println("received JSON parameter is: " + encryptedMessage);

            System.out.println("Android Unique ID is: " + Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID) );

            JNCryptor encryptor = new AES256JNCryptor();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

        /*
        try {
            final CryptographyHandler cryptoManager = new CryptographyHandler();

            try {
                Ion.with(getContext())
                        .load("https://136.145.219.61:7213/de41089f1ae36d9395/user/ec8d28cf1ae36d9395/session")
                        .setBodyParameter("SentinelMessage", cryptoManager.encryptJSON())
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                try {
                                    JSONObject json = new JSONObject(result);
                                    String valueOfJson = json.get("SentinelMessage").toString();
                                    System.out.println(valueOfJson);
                                    String decodedMessage = cryptoManager.decryptJSON(valueOfJson);
                                    System.out.println(decodedMessage);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                } catch (CryptorException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });

            } catch (CryptorException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_countdown, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        countDownDisplay = (TextView) getView().findViewById(R.id.countDownDisplay);

        new CountDownTimer(7000, 1000) {

                public void onTick(long millisUntilFinished) {
                    countDownDisplay.setText(String.valueOf((millisUntilFinished / 1000) - 1));
                }

                public void onFinish() {
                    //removing current fragment and replacing it with the last fragment on backstack
                    getActivity().getSupportFragmentManager().popBackStackImmediate();

                }
            }.start();

        }

    }