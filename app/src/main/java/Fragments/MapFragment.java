package Fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import edu.uprm.Sentinel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private double[] latitude;
    private double[] longitude;
    private String[] name;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private TextView bannerText;
    private boolean multipleMarkers;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        multipleMarkers = getArguments().getBoolean("multipleMarkers");

        if(!multipleMarkers){
            latitude = new double[]{getArguments().getDouble("latitude")};
            longitude = new double[]{getArguments().getDouble("longitude")};
            name = new String[]{getArguments().getString("name")};
            bannerText = (TextView) root.findViewById(R.id.mapbannertext);
            bannerText.setText(name[0]);
        }
        else{
            latitude = getArguments().getDoubleArray("latitude");
            longitude = getArguments().getDoubleArray("longitude");
            name = getArguments().getStringArray("name");
            RelativeLayout banner = (RelativeLayout) root.findViewById(R.id.mapbannercontainer);
            banner.setVisibility(View.INVISIBLE);
        }

        mapFragment = new SupportMapFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.mapLayout, mapFragment, "mapfragment").commit();
        mapFragment.getMapAsync(this);
        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_default, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //if(jsonArray != null) {

            if (!multipleMarkers) {
                // Add a marker in Sydney and move the camera
                LatLng myLocation = new LatLng(latitude[0], longitude[0]);

                Float zoom = new Float(mMap.getMaxZoomLevel() * .90);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom.floatValue()));
                mMap.addMarker(new MarkerOptions().position(myLocation).title(name[0]).visible(true));

            } else {
                for (int i = 0; i < name.length; i++ ){
                        LatLng myLocation = new LatLng(latitude[i], longitude[i]);

                        mMap.addMarker(new MarkerOptions().position(myLocation).title(name[i]).visible(true));

                        if(i == 0){
                            Float zoom = new Float(mMap.getMaxZoomLevel() * .90);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom.floatValue()));
                        }
                }
            }
        //}
    }
}
