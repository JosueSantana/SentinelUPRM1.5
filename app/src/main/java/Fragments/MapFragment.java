package Fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private double latitude;
    private double longitude;
    private String name;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private TextView bannerText;

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
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        latitude = getArguments().getFloat("latitude");
        longitude = getArguments().getFloat("longitude");
        System.out.println("lat:" + latitude + ", lon: " + longitude);
        name = getArguments().getString("name");

        mapFragment = new SupportMapFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.mapLayout, mapFragment, "mapfragment").commit();
        mapFragment.getMapAsync(this);

        bannerText = (TextView) root.findViewById(R.id.mapbannertext);
        bannerText.setText(name);

        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //if(jsonArray != null) {

//            if (!multipleMarkers) {
                // Add a marker in Sydney and move the camera
                LatLng myLocation = new LatLng(latitude, longitude);

                Float zoom = new Float(mMap.getMaxZoomLevel() * .90);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom.floatValue()));
                mMap.addMarker(new MarkerOptions().position(myLocation).title(name).visible(true));

//            } else {
//                for (int i = 0; i < jsonArray.length(); i++ ){
//                    try {
//                        LatLng myLocation = new LatLng(Double.parseDouble(jsonArray.getJSONObject(i).getString("latitude"))
//                                , Double.parseDouble(jsonArray.getJSONObject(i).getString("longitude")));
//
//                        mMap.addMarker(new MarkerOptions().position(myLocation).title(jsonArray.getJSONObject(i).getString("name")).visible(true));
//
//                        if(i == 0){
//                            Float zoom = new Float(mMap.getMaxZoomLevel() * .90);
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom.floatValue()));
//                        }
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
        //}
    }
}
