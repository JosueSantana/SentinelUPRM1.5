package Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import OtherHandlers.Constants;
import OtherHandlers.HttpHelper;
import edu.uprm.Sentinel.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.cryptonode.jncryptor.CryptorException;
import org.json.JSONException;
import org.json.JSONObject;

import OtherHandlers.CryptographyHandler;
import OtherHandlers.JSONHandler;
import edu.uprm.Sentinel.SplashActivity;

public class CountdownFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private android.location.Location mLastLocation;
    private LocationRequest mLocationRequest;
    private TextView countDownDisplay;

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FASTEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 5; // 10 meters
    private ProgressBar spinner;
    private ImageButton cancelButton;
    private ImageButton sendButton;
    CountDownTimer CDT;
    boolean buttonPressed = false;
    private SharedPreferences credentials;
    private SharedPreferences.Editor editor;

    private FragmentManager fm;

    // Required empty public constructor:
    public CountdownFragment() {}

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
        mGoogleApiClient.connect();
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
        fm = getActivity().getSupportFragmentManager();

        spinner = (ProgressBar) getView().findViewById(R.id.progressBar);
        spinner.setVisibility(View.INVISIBLE);

        credentials = this.getActivity().getSharedPreferences(Constants.CREDENTIALS_SP, 0);
        editor = credentials.edit();

        // The string that holds the countdown number
        countDownDisplay = (TextView) getView().findViewById(R.id.countDownDisplay);

        // Set up the countdown timer
        CDT = new CountDownTimer(7000, 1000) {

            public void onTick(long millisUntilFinished) {
                if(!buttonPressed) {
                    countDownDisplay.setText(String.valueOf((millisUntilFinished / 1000) - 1));
                }
                //periodically print location so we know it's working
                if (mGoogleApiClient.isConnected()) {
                    String text = String.valueOf(mLastLocation.getLatitude()) + ", " + String.valueOf(mLastLocation.getLongitude());
                    System.out.println("Coordinates: " + text);
                }
            }

            public void onFinish() {
                if(!buttonPressed){
                    toggleUIClicking(false);
                    goBackToAlertWaitFragment();
                }
            }
        }.start();

        //reference to the send button, moves into the alertwaitfragment
        /**
         * Send Button (Green Button) Listener:
         */
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

        /**
         * Cancel Button (Red Button) Listener:
         */
        cancelButton = (ImageButton) getView().findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CDT.cancel();
                if(mGoogleApiClient.isConnected()){
                    mGoogleApiClient.disconnect();
                }
                stopLocationUpdates();
                buttonPressed = true;
                toggleUIClicking(false);
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

    }

    private void sendAlert() throws JSONException, CryptorException {

        final CryptographyHandler crypto = new CryptographyHandler();
        final JSONObject alertJSON = new JSONObject();

        Runnable r = new Runnable(){
            @Override
            public void run() {
                try {
                    alertJSON.put("token", Constants.getToken(getContext()));
                    alertJSON.put("latitude", mLastLocation.getLatitude());
                    alertJSON.put("longitude", mLastLocation.getLongitude());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner.setVisibility(View.VISIBLE);
                        }
                    });

                    Ion.with(getContext())
                            .load(Constants.SEND_ALERT_URL)
                            .setBodyParameter(Constants.SENTINEL_MESSAGE_KEY, crypto.encryptJSON(alertJSON))
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if(HttpHelper.requestIsSuccessful(e)) {
                                        try {
                                            JSONObject receivedSentinelMessage = JSONHandler.convertStringToJSON(result);
                                            String encryptedJSONReceived = JSONHandler.getSentinelMessage(receivedSentinelMessage);
                                            final String decryptedJSONReceived = crypto.decryptString(encryptedJSONReceived);

                                            final JSONObject receivedJSON = JSONHandler.convertStringToJSON(decryptedJSONReceived);

                                            //(int titleID, int messageID, int positiveID, int negativeID, String kind, boolean hasNeg)
                                            CountdownFragment.this.getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    System.out.println("received:" + receivedJSON);
                                                    try {
                                                        spinner.setVisibility(View.INVISIBLE);
                                                        if (HttpHelper.receivedSuccessMessage(receivedJSON, "4")) {
                                                            showProceedMessage(R.string.timenotuptitle, R.string.timenotupmessage, R.string.okmessage, R.string.cancelmessage, "timenotout", false);
                                                        } else if (HttpHelper.receivedSuccessMessage(receivedJSON, "3")) {
                                                            buttonPressed = true;
                                                            showProceedMessage(R.string.alertnoinlocationtitle, R.string.alertnoinlocationmessage, R.string.okmessage, R.string.cancelmessage, "outofbounds", false);
                                                        } else if (HttpHelper.receivedSuccessMessage(receivedJSON, "2")) {
                                                            editor.putBoolean("sessionDropped", true).commit();
                                                            Constants.deleteToken(getContext());
                                                            Intent splashIntent = new Intent(getActivity(), SplashActivity.class);
                                                            splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //use to clear activity stack
                                                            startActivity(splashIntent);
                                                        } else if (receivedJSON.getString("success").equals("1")) {
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
                                    /**
                                     * IF THE REQUEST WAS UNSUCCESSFUL:
                                     */
                                    else{
                                        Toast.makeText(CountdownFragment.this.getActivity(),
                                                "Please check your internet connection.",
                                                Toast.LENGTH_SHORT).show();
                                        getActivity()
                                                .getSupportFragmentManager()
                                                .popBackStackImmediate();
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    private void showProceedMessage(int titleID, int messageID, int positiveID, int negativeID, String kind, boolean hasNeg) {
        //prepare strings to pass to Fragment through Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("dialogtitle", titleID);
        bundle.putInt("dialogmessage", messageID);
        bundle.putInt("positivetitle", positiveID);
        bundle.putInt("negativetitle", negativeID);
        bundle.putString("intentkind", kind);
        bundle.putBoolean("hasneg", hasNeg );

        //Call up AlertDialog
        IntentDialogFragment dialogFragment = new IntentDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "Proceed Dialog Fragment");
    }
    //stops
    protected void stopLocationUpdates() {
        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
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