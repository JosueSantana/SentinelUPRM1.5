package Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import OtherHandlers.Constants;
import OtherHandlers.HttpHelper;
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
import edu.uprm.Sentinel.SplashActivity;

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
    private ProgressBar spinner;
    private boolean allowRefresh = true;

    private SharedPreferences credentials;
    private SharedPreferences.Editor editor;


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
                if (allowRefresh) {
                    System.out.println("allowrefresh: " + allowRefresh);
                    System.out.println("ALLOWED REFRESH");
                    crypto = new CryptographyHandler();

                    allowRefresh = false;
                    JSONObject registerJSON = new JSONObject();
                    registerJSON.put("token", Constants.getToken(getContext()));

                    Ion.with(getContext())
                            .load(Constants.GET_ALERTS_URL)
                            .setBodyParameter(Constants.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String receivedJSON) {
                                    // Successful Request
                                    if (HttpHelper.requestIsSuccessful(e)) {
                                        JSONObject decryptedValue = crypto.getDecryptedValue(receivedJSON);
                                        // Received Success Message
                                        if (HttpHelper.receivedSuccessMessage(decryptedValue, "1")) {
                                            try {
                                                JSONArray incidents = decryptedValue.getJSONArray("incident");

                                                // new incidents reported since last refresh
                                                if (incidents.length() > numberOfIncidents) {

                                                    numberOfIncidents = incidents.length();
                                                    jsonArray = new JSONArray();

                                                    for (int i = 0; i < incidents.length(); i++) {
                                                        JSONObject tempJSON = new JSONObject();
                                                        DateHandler date = new DateHandler(incidents.getJSONObject(i).get("created_on").toString());
                                                        tempJSON.put("name", incidents.getJSONObject(i).get("regionFullname"));
                                                        tempJSON.put("date", date.getDisplayDate());
                                                        tempJSON.put("time", date.getDisplayTime());
                                                        tempJSON.put("latitude", incidents.getJSONObject(i).get("latitude"));
                                                        tempJSON.put("longitude", incidents.getJSONObject(i).get("longitude"));
                                                        tempJSON.put("fullname", incidents.getJSONObject(i).get("regionName"));

                                                        jsonArray.put(tempJSON);
                                                        allowRefresh = true;
                                                    }

                                                } else {
                                                    // no new incidents reported; do nothing.
                                                }

                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                        else if(HttpHelper.receivedSuccessMessage(decryptedValue,"2")){
                                            editor.putBoolean("sessionDropped", true).commit();

                                            Intent splashIntent = new Intent(getActivity(), SplashActivity.class);
                                            splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
                                            startActivity(splashIntent);
                                        }
                                        // Message Was Not Successful.
                                        else {}
                                    }
                                    // Errors
                                    else {}
                                }
                            });
                }
            }catch (JSONException e) {
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

    public void onResume(){
        super.onResume();

        spinner = (ProgressBar) getView().findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        mList = this.getListView();

        mList.setEmptyView(this.getView().findViewById(R.id.noincidentstext));

        //runnable of the list filling
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                final CryptographyHandler crypto;
                try {
                    allowRefresh = false;
                    System.out.println("REFRESH IS NOW FALSE");
                    crypto = new CryptographyHandler();

                    JSONObject registerJSON = new JSONObject();
                    registerJSON.put("token", Constants.getToken(getContext()));

                    Ion.with(getContext())
                            .load(Constants.GET_ALERTS_URL)
                            .setBodyParameter(Constants.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(registerJSON))
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String receivedJSON) {
                                    // Successful Request
                                    if (HttpHelper.requestIsSuccessful(e)) {
                                        JSONObject decryptedValue = crypto.getDecryptedValue(receivedJSON);

                                        // Received Success Message
                                        if (HttpHelper.receivedSuccessMessage(decryptedValue, "1")) {
                                            try {
                                                JSONArray incidents = decryptedValue.getJSONArray("incident");
                                                numberOfIncidents = incidents.length();

                                                for (int i = 0; i < incidents.length(); i++) {
                                                    JSONObject tempJSON = new JSONObject();

                                                    if(incidents.length() != jsonArray.length() || incidents.length() == 0) {
                                                        DateHandler date = new DateHandler(incidents.getJSONObject(i).get("created_on").toString());
                                                        tempJSON.put("name", incidents.getJSONObject(i).get("regionFullname"));
                                                        tempJSON.put("date", date.getDisplayDate());
                                                        tempJSON.put("time", date.getDisplayTime());
                                                        tempJSON.put("latitude", incidents.getJSONObject(i).get("latitude"));
                                                        tempJSON.put("longitude", incidents.getJSONObject(i).get("longitude"));
                                                        jsonArray.put(tempJSON);
                                                    }
                                                    else if(!jsonArray.getJSONObject(i).get("name").equals(incidents.getJSONObject(i).get("regionFullname"))){
                                                        System.out.println("JSONARRAY DOESNT EQUAL");
                                                        jsonArray.put(i, incidents.get(i));
                                                    }
                                                }

                                                mList.post(new Runnable() {
                                                    public void run() {
                                                        mList.setAdapter(new IncidentsAdapter(jsonArray, getActivity()));
                                                    }
                                                });

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        spinner.setVisibility(View.GONE);
                                                        allowRefresh = true;
                                                    }
                                                });

                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                        else if(HttpHelper.receivedSuccessMessage(decryptedValue, "2")){
                                            editor.putBoolean("sessionDropped", true).commit();

                                            Intent splashIntent = new Intent(getActivity(), SplashActivity.class);
                                            splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
                                            startActivity(splashIntent);
                                        }

                                        // Message Was Not Successful.
                                        else {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    spinner.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    }


                                    // Errors
                                    else {

                                    }
                                }
                            });

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (CryptorException e) {
                    e.printStackTrace();
                }
            }
        };

        //if (jsonArray.length() == 0) {
        Thread mythread = new Thread(r);
        mythread.start();
        //}

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
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.mainLayout, mapFragment, "mapFrag").addToBackStack("mapFrag").commit();

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
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.mainLayout, mapFragment, "mapFrag").addToBackStack("mapFrag").commit();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
