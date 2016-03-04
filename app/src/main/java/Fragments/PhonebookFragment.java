package Fragments;

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

import com.hmkcode.locations.sentineluprm15.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ListViewHelpers.ContactsAdapter;
import ListViewHelpers.PhonebookAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhonebookFragment extends ListFragment{


    JSONArray contactsList;

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

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};

        CursorLoader loader = new CursorLoader(this.getActivity(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();

        contactsList = new JSONArray();

        if(cursor.moveToFirst()){
            do{
                try {
                    if(cursor.getInt(cursor.getColumnIndex(projection[2])) == 1){
                        System.out.println("DOING PHONE STUFF");

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
                            System.out.println("M MATCHES " + m.group(1) + m.group(2) + m.group(3));
                            item.put("phone", m.group(1) + m.group(2) + m.group(3));
                        }

                        contactsList.put(item);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            while(cursor.moveToNext());
        }

        System.out.println("JSONARRAY: " + contactsList.toString());

        setListAdapter(new PhonebookAdapter(contactsList, getActivity()));
        getListView();
    }

    public void onListItemClick(ListView l, View v, int position, long id){
        
    }



}
