package Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hmkcode.locations.sentineluprm15.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ListViewHelpers.ContactsAdapter;
import ListViewHelpers.IncidentsAdapter;

/**
 * This fragment controls the incidents to be manipulated into the table.
 */
public class IncidentsFragment extends ListFragment {

    public IncidentsFragment() {
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
        return inflater.inflate(R.layout.fragment_incidents, container, false);
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
            jOb.put("name","Edificio Stefani");
            jOb.put("time", "7:23 PM");
            jOb.put("date", "February 14, 2016");
            jOb2.put("name","Edificio Luchetti");
            jOb2.put("time", "2:00 AM");
            jOb2.put("date", "January 20, 2016");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray.put(jOb);
        jsonArray.put(jOb2);
        //...is provisional

        setListAdapter(new IncidentsAdapter(jsonArray, getActivity()));
        getListView();

    }

    public void onListItemClick(ListView l, View v, int position, long id){
        //TODO: Do stuff when you click
    }


    private void openIncidentMap(){

    }

    private void openIncidentMapAll(){

    }

}
