package Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import edu.uprm.Sentinel.R;

import com.google.android.gms.maps.GoogleMap;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import ListViewHelpers.IncidentsAdapter;
import OtherHandlers.CryptographyHandler;
import OtherHandlers.DateHandler;
import OtherHandlers.JSONHandler;
import OtherHandlers.ValuesCollection;

/**
 * This fragment controls the incidents to be manipulated into the table.
 */
public class IncidentsFragment extends ListFragment {

    SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    JSONArray jsonArray;
    ListView mList;
    private int numberOfIncidents;
    private GoogleMap mMap;
    private Double longitude;
    private Double latitude ;
    private String placeName = "";
    private boolean multipleMarkers = false;
    private MapFragment mapFragment;

    public IncidentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.numberOfIncidents = 0;
        this.jsonArray = new JSONArray();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_incidents, container, false);

        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_container);
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

        mList = this.getListView();

        mList.setEmptyView(this.getView().findViewById(R.id.noincidentstext));

        //runnable of the list filling
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                final CryptographyHandler crypto;
                try {
                    crypto = new CryptographyHandler();

                    JSONObject registerJSON = new JSONObject();
                    registerJSON.put("token", getToken());

                    Ion.with(getContext())
                            .load(ValuesCollection.GET_ALERTS_URL)
                            .setBodyParameter(ValuesCollection.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String receivedJSON) {
                                    // Successful Request
                                    if (requestIsSuccessful(e)) {
                                        JSONObject decryptedValue = getDecryptedValue(receivedJSON);
                                        //System.out.println(decryptedValue);
                                        try {
                                            JSONArray incidents = decryptedValue.getJSONArray("incident");
                                            numberOfIncidents = incidents.length();
                                            //System.out.println(incidents);

                                            for(int i = 0; i < incidents.length(); i++) {
                                                JSONObject tempJSON = new JSONObject();
                                                DateHandler date = new DateHandler(incidents.getJSONObject(i).get("created_on").toString());
                                                tempJSON.put("name", incidents.getJSONObject(i).get("regionFullname"));
                                                tempJSON.put("date", date.getDisplayDate());
                                                tempJSON.put("time", date.getDisplayTime());
                                                tempJSON.put("latitude", incidents.getJSONObject(i).get("latitude"));
                                                tempJSON.put("longitude", incidents.getJSONObject(i).get("longitude"));
                                                jsonArray.put(tempJSON);
                                            }

                                            mList.post(new Runnable(){
                                                public void run(){
                                                    mList.setAdapter(new IncidentsAdapter(jsonArray, getActivity()));
                                                }
                                            });

                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                        // Received Success Message
                                        if (receivedSuccessMessage(decryptedValue)) {

                                        }
                                        // Message Was Not Successful.
                                        else {

                                        }
                                    }
                                    // Errors
                                    else {

                                    }
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

        if(jsonArray.length() == 0) {
            Thread mythread = new Thread(r);
            mythread.start();
        }

        //TODO: Get JSONArray from Handler
        //TODO: Should we use AsyncTasks or does the Fragment take care of that?

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

    }


    private String getToken() {
        SharedPreferences credentials = this.getActivity().getSharedPreferences(ValuesCollection.CREDENTIALS_SP, 0);
        String storedToken = credentials.getString(ValuesCollection.TOKEN_KEY, null);
        return storedToken;
    }

    //this asynctask runs while refreshing and gets new data from DB
    private class RefreshAdapter extends AsyncTask<Void,Long,IncidentsAdapter > {
        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setRefreshing(true);
        }
        /* (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected IncidentsAdapter doInBackground(Void... params) {
            // get the new data from you data source
            // TODO : request data here
            final CryptographyHandler crypto;
            try {
                crypto = new CryptographyHandler();

                JSONObject registerJSON = new JSONObject();
                registerJSON.put("token", getToken());

                Ion.with(getContext())
                        .load(ValuesCollection.GET_ALERTS_URL)
                        .setBodyParameter(ValuesCollection.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String receivedJSON) {
                                // Successful Request
                                if (requestIsSuccessful(e)) {
                                    JSONObject decryptedValue = getDecryptedValue(receivedJSON);
                                    //System.out.println(decryptedValue);

                                    try {
                                        JSONArray incidents = decryptedValue.getJSONArray("incident");

                                        // new incidents reported since last refresh
                                        if(incidents.length() > numberOfIncidents){

                                            numberOfIncidents = incidents.length();
                                            jsonArray = new JSONArray();

                                            for(int i = 0; i < incidents.length(); i++) {
                                                JSONObject tempJSON = new JSONObject();
                                                DateHandler date = new DateHandler(incidents.getJSONObject(i).get("created_on").toString());
                                                tempJSON.put("name", incidents.getJSONObject(i).get("regionName"));
                                                tempJSON.put("date", date.getDisplayDate());
                                                tempJSON.put("time", date.getDisplayTime());
                                                tempJSON.put("latitude", incidents.getJSONObject(i).get("latitude"));
                                                tempJSON.put("longitude", incidents.getJSONObject(i).get("longitude"));
                                                tempJSON.put("fullname", incidents.getJSONObject(i).get("regionName"));

                                                jsonArray.put(tempJSON);
                                            }

                                        } else {
                                            // no new incidents reported; do nothing.
                                        }

                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }

                                    // Received Success Message
                                    if (receivedSuccessMessage(decryptedValue)) {

                                    }

                                    // Message Was Not Successful.
                                    else {

                                    }

                                }
                                // Errors
                                else {

                                }
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
            return  true;
        }
        return false;
    }

    public void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);

        try {
            System.out.println("lat: " + ((JSONObject) jsonArray.get(position)).getString("latitude") + "long: " + ((JSONObject) jsonArray.get(position)).getString("latitude"));
            latitude = Double.parseDouble(((JSONObject) jsonArray.get(position)).getString("latitude"));
            longitude = Double.parseDouble(((JSONObject) jsonArray.get(position)).getString("longitude"));
            placeName = ((JSONObject) jsonArray.get(position)).getString("name");
            multipleMarkers = false;

            mapFragment = new MapFragment();
            Bundle mapFragBundle = new Bundle();
            mapFragBundle.putDouble("latitude", latitude);
            mapFragBundle.putDouble("longitude", longitude);
            mapFragBundle.putString("name", placeName);
            mapFragBundle.putBoolean("multipleMarkers", multipleMarkers);
            mapFragment.setArguments(mapFragBundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, mapFragment, "mapFrag").addToBackStack("mapFrag").commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_incidents, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_all:
                //do something
                multipleMarkers = true;

                mapFragment = new MapFragment();
                Bundle mapFragBundle = new Bundle();

                double[] latitudeArray = new double[jsonArray.length()];
                double[] longitudeArray = new double[jsonArray.length()];
                String[] nameArray = new String[jsonArray.length()];
                for(int i = 0; i < jsonArray.length(); i++){
                    try {
                        latitudeArray[i] = Double.parseDouble(jsonArray.getJSONObject(i).getString("latitude"));
                        longitudeArray[i] = Double.parseDouble(jsonArray.getJSONObject(i).getString("longitude"));
                        nameArray[i] = jsonArray.getJSONObject(i).getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mapFragBundle.putDoubleArray("latitude", latitudeArray);
                mapFragBundle.putDoubleArray("longitude", longitudeArray);
                mapFragBundle.putStringArray("name", nameArray);
                mapFragBundle.putBoolean("multipleMarkers", multipleMarkers);
                mapFragment.setArguments(mapFragBundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, mapFragment, "mapFrag").addToBackStack("mapFrag").commit();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
