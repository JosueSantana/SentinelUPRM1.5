package Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import edu.uprm.Sentinel.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ListViewHelpers.ContactsAdapter;
import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import OtherHandlers.ValuesCollection;

public class ContactsFragment extends ListFragment{

    private JSONArray jsonArray;

    public ContactsFragment() {
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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO: Get JSONArray from Handler
        //TODO: Should we use AsyncTasks or does the Fragment take care of that?

        //all of this...
        jsonArray = new JSONArray();

        final CryptographyHandler crypto;

        JSONObject registerJSON = new JSONObject();
        try {
            crypto = new CryptographyHandler();

            registerJSON.put("token", getToken());
            System.out.println("lol");

            Ion.with(getContext())
                    .load(ValuesCollection.CONTACT_LIST_URL)
                    .setBodyParameter(ValuesCollection.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String receivedJSON) {

                            // Successful Request
                            if (requestIsSuccessful(e)) {

                                JSONObject decryptedValue = getDecryptedValue(receivedJSON);
                                System.out.println(decryptedValue);

                                try {
                                    JSONArray contacts = decryptedValue.getJSONArray("contact");

                                    for(int i = 0; i < contacts.length(); i++) {
                                        JSONObject tempJSON = new JSONObject();
                                        tempJSON.put("name", contacts.getJSONObject(i).get("name"));
                                        tempJSON.put("editedPhone", contacts.getJSONObject(i).get("phone"));
                                        jsonArray.put(tempJSON);
                                    }
                                    setListAdapter(new ContactsAdapter(jsonArray, getActivity()));

                                    //setting up the amount of contacts for the settings
                                    SharedPreferences settings = ContactsFragment.this.getActivity().getSharedPreferences(ValuesCollection.SETTINGS_SP, 0);
                                    SharedPreferences.Editor editor = settings.edit();

                                    editor.putInt("contactsCount", jsonArray.length()).commit();

                                    System.out.println("COUNT OF JSON ARRAY LENGTH " + jsonArray.length());


                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                                // Created a new session; there are no registered contacts yet.
                                if(listIsEmpty(decryptedValue)) {
                                }
                                // Received contact list.
                                else if (receivedExistingContacts(decryptedValue)) {
                                }
                                //
                                else {

                                }
                            }
                            // Errors
                            else {
                            }
                        }

                        private boolean listIsEmpty(JSONObject decryptedValue) {
                            String success = null;
                            try {
                                success = decryptedValue.getString("success");
                                return success.equals("2");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }

                        // Extract Success Message From Received JSON.
                        private boolean receivedExistingContacts(JSONObject decryptedValue) {
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

        setListAdapter(new ContactsAdapter(jsonArray, getActivity()));
        getListView();
    }

    private String getToken() {
        SharedPreferences credentials = this.getActivity().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        String storedToken = credentials.getString(ValuesCollection.TOKEN_KEY, null);
        return storedToken;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contacts, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_contacts:
                // User chose the "Settings" item, show the app settings UI...
                if(jsonArray.length() >= 5){
                    showSignupError(R.string.alertoverloadtitle,R.string.alertoverloadmessage);
                }
                else{
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                    new PhonebookFragment()).addToBackStack(null).commit();;
                }

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /*
    public void setListAdapter(JSONArray jsonArray){
        this.contactsListView.setAdapter(new ContactsAdapter(jsonArray, getActivity()));
    }


    private class GetMyContactsTask extends AsyncTask<JSONHandler, Long, JSONArray>{

        @Override
        protected JSONArray doInBackground(JSONHandler... jsonHandlers) {

            //get the resulting JSONArray from HTTP Requests

            //format: return params[0].theMethodThatReturnsJSONArray();
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setListAdapter(jsonArray);
        }
    }*/

    private void showSignupError(int titleID, int messageID) {
        //prepare strings to pass to Fragment through Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("dialogtitle", titleID);
        bundle.putInt("dialogmessage", messageID);

        //Call up AlertDialog
        SentinelDialogFragment dialogFragment = new SentinelDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getChildFragmentManager(), "Alert Dialog Fragment");
    }

}
