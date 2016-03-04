package Fragments;

import android.support.v4.app.ListFragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hmkcode.locations.sentineluprm15.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ListViewHelpers.ContactsAdapter;


public class ContactsFragment extends ListFragment{

    private ListView contactsListView;

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
        JSONArray jsonArray = new JSONArray();
        JSONObject jOb = new JSONObject();
        JSONObject jOb2 = new JSONObject();

        try {
            jOb.put("name","Fulano Detal");
            jOb.put("editedPhone", "7874531523");
            jOb2.put("name","Mengano Talcual");
            jOb2.put("editedPhone", "7871093123");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray.put(jOb);
        jsonArray.put(jOb2);
        //...is provisional

        setListAdapter(new ContactsAdapter(jsonArray, getActivity()));
        getListView();

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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new PhonebookFragment()).addToBackStack(null).commit();;
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void onListItemClick(ListView l, View v, int position, long id){
        //TODO: Do stuff when you click
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


}
