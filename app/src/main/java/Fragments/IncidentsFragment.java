package Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.hmkcode.locations.sentineluprm15.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ListViewHelpers.IncidentsAdapter;

/**
 * This fragment controls the incidents to be manipulated into the table.
 */
public class IncidentsFragment extends ListFragment {

    SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    JSONArray jsonArray = new JSONArray();
    ListView mList;

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

        mList = this.getListView();
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_container);

        //TODO: Get JSONArray from Handler
        //TODO: Should we use AsyncTasks or does the Fragment take care of that?

        //all of this...
        if(jsonArray.length() == 0){
        JSONObject jOb = new JSONObject();
        JSONObject jOb2 = new JSONObject();
        JSONObject jOb3 = new JSONObject();
        JSONObject jOb4 = new JSONObject();
        JSONObject jOb5 = new JSONObject();
        JSONObject jOb6 = new JSONObject();
        JSONObject jOb7 = new JSONObject();
        JSONObject jOb8 = new JSONObject();

        try {
            jOb.put("name","Edificio Stefani");
            jOb.put("time", "7:23 PM");
            jOb.put("date", "February 14, 2016");
            jOb2.put("name","Edificio Luchetti");
            jOb2.put("time", "2:00 AM");
            jOb2.put("date", "January 20, 2016");
            jOb3.put("name","Edificio Stefani");
            jOb3.put("time", "7:23 PM");
            jOb3.put("date", "February 14, 2016");
            jOb4.put("name","Edificio Chardon");
            jOb4.put("time", "2:00 AM");
            jOb4.put("date", "January 20, 2016");
            jOb5.put("name","Edificio Fisica");
            jOb5.put("time", "7:23 PM");
            jOb5.put("date", "February 14, 2016");
            jOb6.put("name","Edificio Monzon");
            jOb6.put("time", "2:00 AM");
            jOb6.put("date", "January 20, 2016");
            jOb7.put("name","Edificio Quimica");
            jOb7.put("time", "7:23 PM");
            jOb7.put("date", "February 14, 2016");
            jOb8.put("name","Edificio Civil");
            jOb8.put("time", "2:00 AM");
            jOb8.put("date", "January 20, 2016");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray.put(jOb);
        jsonArray.put(jOb2);
        jsonArray.put(jOb3);
        jsonArray.put(jOb4);
        jsonArray.put(jOb5);
        jsonArray.put(jOb6);
        jsonArray.put(jOb7);
        jsonArray.put(jOb8);
        }
        //...is provisional

        setListAdapter(new IncidentsAdapter(jsonArray, getActivity()));

        //makes sure it doesn't try to refresh the list while the visible list is not at the top
        mList.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                swipeRefreshLayout.setEnabled(mList != null &&
                                (mList.getChildCount() == 0 || mList.getChildCount() > 0
                                        && mList.getFirstVisiblePosition() == 0
                                        && mList.getChildAt(0).getTop() == 0)
                );
            }
        });

        //listener for when you try to refresh the list
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
                //handler.post(refreshing);
                new RefreshAdapter().execute();
            }
        });

        // sets the colors used in the refresh animation
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

        getListView();

    }

    //this asynctask runs while refreshing and gets new data from DB
    private class RefreshAdapter extends AsyncTask<Void,Long,IncidentsAdapter > {
        @Override
        protected void onPreExecute() {
        }
        /* (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected IncidentsAdapter doInBackground(Void... params) {
            // get the new data from you data source
            // TODO : request data here
            handler.post(refreshing);
            IncidentsAdapter adapter = new IncidentsAdapter(jsonArray, getActivity());
            return adapter;
        }

        protected void onPostExecute(IncidentsAdapter result) {
            // stop the animation after the data is fully loaded
            swipeRefreshLayout.setRefreshing(false);

            mList.setAdapter(result);
        }
    }

    //This runnable runs for as long as the new data hasn't arrived
    private final Runnable refreshing = new Runnable() {
        public void run() {
            try {
                // TODO : isRefreshing should be attached to your data request status
                if (isRefreshing()) {
                    // re run the verification after 1 second
                    handler.postDelayed(this, 1000);
                } else {
                    // TODO : update your list with the new data

                    JSONObject jOb = new JSONObject();
                    JSONObject jOb2 = new JSONObject();

                    try{
                        jOb.put("name","Edificio Blablabla");
                        jOb.put("time", "7:23 PM");
                        jOb.put("date", "February 14, 2016");
                        jOb2.put("name","Edificio Etc");
                        jOb2.put("time", "2:00 AM");
                        jOb2.put("date", "January 20, 2016");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonArray.put(jOb);
                    jsonArray.put(jOb2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //Verifies if connection to the server has been closed or something?
    public boolean isRefreshing() {
        //TODO: Add refresh logic
        if(jsonArray == null){
            return true;
        }
        return false;
    }

    public void onListItemClick(ListView l, View v, int position, long id){
        //TODO: Do stuff when you click
    }

    private void openIncidentMap(){

    }

    private void openIncidentMapAll(){

    }

}
