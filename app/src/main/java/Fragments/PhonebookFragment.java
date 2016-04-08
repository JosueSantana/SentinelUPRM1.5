package Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ListFragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import OtherHandlers.Constants;
import OtherHandlers.HttpHelper;
import edu.uprm.Sentinel.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ListViewHelpers.PhonebookAdapter;
import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import edu.uprm.Sentinel.SplashActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhonebookFragment extends ListFragment{


    JSONArray contactsList;
    private ProgressBar spinner;

    public PhonebookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phonebook, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        spinner = (ProgressBar) getView().findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};

        CursorLoader loader = new CursorLoader(this.getActivity(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();

        contactsList = new JSONArray();

        if(cursor.moveToFirst()){
            do{
                try {
                    if(cursor.getInt(cursor.getColumnIndex(projection[2])) == 1){

                        JSONObject item = new JSONObject();

                        item.put("id", cursor.getString(cursor.getColumnIndex(projection[0])));
                        item.put("name", cursor.getString(cursor.getColumnIndex(projection[1])));

                        CursorLoader phonesLoader = new CursorLoader(this.getActivity(),
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ cursor.getString(cursor.getColumnIndex(projection[0])), null, null);
                        Cursor phones = phonesLoader.loadInBackground();
                        phones.moveToFirst();

                        String phoneStr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                        // Create a Pattern object
                        Pattern r = Pattern.compile(".*([0-9]{3}).*([0-9]{3})-([0-9]{4})");

                        // Now create matcher object.
                        Matcher m = r.matcher(phoneStr);
                        if(m.matches()){
                            item.put("phone", m.group(1) + m.group(2) + m.group(3));
                        }

                        contactsList.put(item);
                        phones.close();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        setListAdapter(new PhonebookAdapter(contactsList, getActivity()));
        getListView();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        try {
            final String name = ((JSONObject) contactsList.get(position)).getString("name");
            final String phone = ((JSONObject) contactsList.get(position)).getString("phone");

            Runnable r =  new Runnable() {
                @Override
                public void run() {
                    final CryptographyHandler crypto;

                    JSONObject registerJSON = new JSONObject();
                    try {
                        crypto = new CryptographyHandler();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spinner.setVisibility(View.VISIBLE);
                            }
                        });
                        
                        SharedPreferences credentials = getActivity().getSharedPreferences(Constants.CREDENTIALS_SP, 0);

                        registerJSON.put("token", getToken());
                        registerJSON.put("name", name);
                        registerJSON.put("phone", phone);

                        Ion.with(getContext())
                                .load("PUT", Constants.ADD_CONTACT_URL)
                                .setBodyParameter(Constants.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String receivedJSON) {
                                        System.out.println("lol");
                                        JSONObject decryptedValue = getDecryptedValue(receivedJSON);
                                        System.out.println(decryptedValue);

                                        // Successful Request

                                        if(HttpHelper.receivedSuccessMessage("2", decryptedValue)){
                                            Intent splashIntent = new Intent(getActivity(), SplashActivity.class);
                                            splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
                                            startActivity(splashIntent);
                                        }


                                /*
                                if (requestIsSuccessful(e)) {
                                    JSONObject decryptedValue = getDecryptedValue(receivedJSON);
                                    System.out.println(decryptedValue);
                                }
                                // Errors
                                else {
                                }
                                */
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                                                spinner.setVisibility(View.GONE);
                                            }
                                        });

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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (CryptorException e) {
                        e.printStackTrace();
                    }
                }
            };

            new Thread(r).start();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getToken() {
        SharedPreferences credentials = this.getActivity().getSharedPreferences(Constants.CREDENTIALS_SP, 0);
        String storedToken = credentials.getString(Constants.TOKEN_KEY, null);
        return storedToken;
    }
}
