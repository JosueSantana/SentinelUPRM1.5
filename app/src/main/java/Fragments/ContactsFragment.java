package Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import OtherHandlers.Constants;
import OtherHandlers.HttpHelper;
import OtherHandlers.Toasts;
import edu.uprm.Sentinel.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ListViewHelpers.ContactsAdapter;
import OtherHandlers.CryptographyHandler;
import edu.uprm.Sentinel.SplashActivity;

public class ContactsFragment extends ListFragment{

    private JSONArray jsonArray;
    private ListView mList;
    private volatile Thread loaderThread;
    private ProgressBar spinner;
    private SharedPreferences credentials;
    private SharedPreferences.Editor editor;

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

    public void onDestroyView(){
        super.onDestroyView();

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        jsonArray = new JSONArray();

        mList = this.getListView();

        spinner = (ProgressBar) getView().findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        credentials = this.getActivity().getSharedPreferences(Constants.CREDENTIALS_SP, 0);
        editor = credentials.edit();

        loaderThread = new Thread(new Runnable(){
            @Override
            public void run() {

                try {

                    if(Thread.currentThread().interrupted()) throw new InterruptedException();
                    final CryptographyHandler crypto;
                    JSONObject registerJSON = new JSONObject();
                    crypto = new CryptographyHandler();

                    registerJSON.put("token", Constants.getToken(getContext()));
                    System.out.println();

                    Ion.with(getContext())
                            .load(Constants.CONTACT_LIST_URL)
                            .setBodyParameter(Constants.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String receivedJSON) {
                                    // Successful Request
                                    if (HttpHelper.requestIsSuccessful(e)) {

                                        JSONObject decryptedValue = crypto.getDecryptedValue(receivedJSON);

                                        if(HttpHelper.receivedSuccessMessage(decryptedValue, "1")){

                                        }
                                        else if(HttpHelper.receivedSuccessMessage(decryptedValue, "2")){

                                            editor.putBoolean("sessionDropped", true).commit();

                                            Constants.deleteToken(getContext());
                                            Intent splashIntent = new Intent(getActivity(), SplashActivity.class);
                                            splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
                                            startActivity(splashIntent);
                                        } else {
                                            Toasts.genericErrorToast(getContext());
                                        }

                                        try {
                                            JSONArray contacts = decryptedValue.getJSONArray("contact");

                                            for (int i = 0; i < contacts.length(); i++) {
                                                JSONObject tempJSON = new JSONObject();
                                                tempJSON.put("name", contacts.getJSONObject(i).get("name"));
                                                tempJSON.put("editedPhone", contacts.getJSONObject(i).get("phone"));
                                                jsonArray.put(tempJSON);
                                            }

                                            mList.post(new Runnable(){
                                                @Override
                                                public void run() {
                                                    try{
                                                        setListAdapter(new ContactsAdapter(jsonArray, getActivity()));}
                                                    catch(NullPointerException e1){
                                                        e1.printStackTrace();
                                                    }
                                                }
                                            });

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    spinner.setVisibility(View.GONE);
                                                }
                                            });

                                            //setting up the amount of contacts for the settings
                                            try{
                                                SharedPreferences settings = ContactsFragment.this.getActivity().getSharedPreferences(Constants.SETTINGS_SP, 0);
                                                SharedPreferences.Editor editor = settings.edit();

                                                editor.putInt("contactsCount", jsonArray.length()).apply();}
                                            catch(NullPointerException e1){
                                                Toasts.genericErrorToast(getContext());
                                            }

                                        } catch (JSONException e1) {
                                            Toasts.genericErrorToast(getContext());
                                        }

                                        // Created a new session; there are no registered contacts yet.
                                        if (listIsEmpty(decryptedValue)) {}
                                        // Received contact list.
                                        else if (receivedExistingContacts(decryptedValue)) {}
                                        //
                                        else {
                                            Toasts.genericErrorToast(getContext());
                                        }
                                    }
                                    // Errors
                                    else {
                                        Toasts.connectionErrorToast(getContext());
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
                            });
                } catch (CryptorException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        loaderThread.setPriority(Thread.MAX_PRIORITY);
        loaderThread.start();
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
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.mainLayout,
                            new PhonebookFragment()).addToBackStack(null).commit();
                }

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    //Verifies if connection to the server has been closed or something?
    public boolean isRefreshing() {
        //TODO: Add refresh logic
        if(jsonArray == null){
            return  true;
        }
        return false;
    }

    private void showSignupError(int titleID, int messageID) {
        //prepare strings to pass to Fragment through Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("dialogtitle", titleID);
        bundle.putInt("dialogmessage", messageID);

        //Call up AlertDialog
        SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getChildFragmentManager(), "Alert Dialog Fragment");
    }
}
