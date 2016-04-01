package Fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import OtherHandlers.Constants;
import edu.uprm.Sentinel.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;

public class CountdownFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private android.location.Location mLastLocation;
    private LocationRequest mLocationRequest;
    private TextView countDownDisplay;

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FASTEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 5; // 10 meters
    private ImageButton cancelButton;
    private ImageButton sendButton;
    CountDownTimer CDT;
    boolean buttonPressed = false;

    public CountdownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("---Created Countdown----");

        // Create an instance of GoogleAPIClient and location request
        buildGoogleApiClient();
        createLocationRequest();

        if(savedInstanceState != null){
            mLastLocation.setLatitude(savedInstanceState.getDouble("latitude"));
            mLastLocation.setLatitude(savedInstanceState.getDouble("longitude"));
            System.out.println("lat: " + mLastLocation.getLatitude() + "long: " + mLastLocation.getLongitude());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //assure the service is connected
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();

        //checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        //ensure client is disconnected when fragment is destroyed
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        // stop trying to update while fragment is not active
        stopLocationUpdates();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_countdown, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //The string that holds the countdown number
        countDownDisplay = (TextView) getView().findViewById(R.id.countDownDisplay);

        //Set up the countdown timer
        CDT = new CountDownTimer(7000, 1000) {

            public void onTick(long millisUntilFinished) {
                countDownDisplay.setText(String.valueOf((millisUntilFinished / 1000) - 1));
                //periodically print location so we know it's working
                if (mGoogleApiClient.isConnected()) {
                    String text = String.valueOf(mLastLocation.getLatitude()) + ", " + String.valueOf(mLastLocation.getLongitude());
                    System.out.println("Coordinates: " + text);
                }
            }

            public void onFinish() {
                if(!buttonPressed){
                    goBackToAlertWaitFragment();
                }
            }
        }.start();

        //reference to the send button, moves into the alertwaitfragment
        sendButton = (ImageButton) getView().findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send the Alert.
                buttonPressed = true;
                toggleUIClicking(false);
                goBackToAlertWaitFragment();
            }
        });

        //reference to the back button,
        cancelButton = (ImageButton) getView().findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPressed = true;
                toggleUIClicking(false);
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

    }

    private String getToken() {
        SharedPreferences credentials = this.getActivity().getSharedPreferences(Constants.CREDENTIALS_SP, 0);
        String storedToken = credentials.getString(Constants.TOKEN_KEY, null);
        return storedToken;
    }

    private void sendAlert() throws JSONException, CryptorException {

        final CryptographyHandler crypto = new CryptographyHandler();
        final JSONObject alertJSON = new JSONObject();

        Runnable r = new Runnable(){
            @Override
            public void run() {


                try {
                alertJSON.put("token", getToken());
                alertJSON.put("latitude", mLastLocation.getLatitude());
                alertJSON.put("longitude", mLastLocation.getLongitude());

                    Ion.with(getContext())
                            .load(Constants.SEND_ALERT_URL)
                            .setBodyParameter(Constants.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(alertJSON))
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    System.out.println(result);
                                    try {
                                        JSONObject receivedSentinelMessage = JSONHandler.convertStringToJSON(result);
                                        String encryptedJSONReceived = JSONHandler.getSentinelMessage(receivedSentinelMessage);
                                        String decryptedJSONReceived = crypto.decryptString(encryptedJSONReceived);

                                        final JSONObject receivedJSON = JSONHandler.convertStringToJSON(decryptedJSONReceived);

                                        CountdownFragment.this.getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                System.out.println("received:" + receivedJSON);

                                                try {
                                                    if(receivedJSON.getString("success").equals("3"))
                                                    {
                                                        buttonPressed = true;
                                                        Toast.makeText(CountdownFragment.this.getActivity(), R.string.alertnoinlocationmessage, Toast.LENGTH_SHORT).show();
                                                        getActivity().getSupportFragmentManager().popBackStackImmediate();

                                                    }
                                                    else if(receivedJSON.getString("success").equals("1")){
                                                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).remove(CountdownFragment.this).replace(R.id.mainLayout, new AlertWaitFragment()).commit();
                                                    }
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        });

                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    } catch (CryptorException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });
                } catch (CryptorException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };
       Thread t =  new Thread(r);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    private void goBackToAlertWaitFragment(){
        // Send the Alert.
        try {
            sendAlert();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (CryptorException e) {
            e.printStackTrace();
        }
    }


    public void onDestroy(){
        super.onDestroy();
        //cancel timer when this fragment is destroyed
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //get initial connected location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //begin attempting updates
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    //stops
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    //Sets up the google api client
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Sets up the location requests
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 5 meters
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    public void toggleUIClicking(boolean toggler){
        sendButton.setClickable(toggler);
        sendButton.setFocusable(toggler);
        sendButton.setFocusableInTouchMode(toggler);

        cancelButton.setClickable(toggler);
        cancelButton.setFocusable(toggler);
        cancelButton.setFocusableInTouchMode(toggler);
    }

}