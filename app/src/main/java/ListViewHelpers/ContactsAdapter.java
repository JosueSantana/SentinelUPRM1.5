package ListViewHelpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import Fragments.ContactsFragment;
import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import OtherHandlers.ValuesCollection;
import edu.uprm.Sentinel.R;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a136803 on 12/18/15.
 * This class holds the data structure for a contacts list item.
 */
public class ContactsAdapter extends BaseAdapter {

    private JSONArray dataArray;
    private Activity activity;

    private static LayoutInflater inflater = null;

    public ContactsAdapter(JSONArray jsonArray, Activity activity){
        this.dataArray = jsonArray;
        this.activity = activity;

        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return this.dataArray.length();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ContactsRow row;

        // set up convert view if it is null
        if(view == null)
        {
            view = inflater.inflate(R.layout.contacts_row, null);
            row = new ContactsRow();

            row.contactName = (TextView) view.findViewById(R.id.myContactName);
            row.contactPhone = (TextView) view.findViewById(R.id.myContactPhone);
            view.setTag(row);

            ImageButton deleteIcon = (ImageButton) view.findViewById(R.id.deleteIcon);

            deleteIcon.setTag(i);

            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Integer index = (Integer) view.getTag();

                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                deleteContact(dataArray.getJSONObject(index).getString("editedPhone"), index);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    new Thread(r).start();

                }
            });


        }
        else
        {
            row = (ContactsRow) view.getTag();
        }

        //edit row data
        try {
            JSONObject jsonObject = this.dataArray.getJSONObject(i);

            row.contactName.setText(jsonObject.getString("name"));
            row.contactPhone.setText(jsonObject.getString("editedPhone"));
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return view;
    }

    public void deleteContact(String telephone, int i) {
        final CryptographyHandler crypto;
        final int index = i;

        JSONObject registerJSON = new JSONObject();
        try {
            crypto = new CryptographyHandler();

            registerJSON.put("token", getToken());
            registerJSON.put("delete", telephone);

            Ion.with(activity.getApplicationContext())
                    .load("DELETE", ValuesCollection.DELETE_CONTACT_URL)
                    .setBodyParameter(ValuesCollection.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String receivedJSON) {

                            // Successful Request
                            if (requestIsSuccessful(e)) {
                                JSONObject decryptedValue = getDecryptedValue(receivedJSON);
                                System.out.println(decryptedValue);

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dataArray.remove(index);
                                        //setting up the amount of contacts for the settings
                                        SharedPreferences settings = activity.getSharedPreferences(ValuesCollection.SETTINGS_SP, 0);
                                        SharedPreferences.Editor editor = settings.edit();

                                        editor.putInt("contactsCount", dataArray.length()).commit();
                                        notifyDataSetChanged();
                                    }
                                });
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
    }

    private String getToken() {
        SharedPreferences credentials = this.activity.getApplicationContext().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        String storedToken = credentials.getString(ValuesCollection.TOKEN_KEY, null);
        return storedToken;
    }

    private class ContactsRow
    {
        private TextView contactName;
        private TextView contactPhone;

    }
}
