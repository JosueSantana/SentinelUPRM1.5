package Fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import edu.uprm.Sentinel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private double latitude;
    private double longitude;
    private String name;
    private GoogleMap theMap;
    private MapView mMapView;

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

        // Inflate the layout for this fragment
        System.out.println("HERE");
        latitude = Double.parseDouble(getArguments().getString("incidentlatitude"));
        longitude = Double.parseDouble(getArguments().getString("incidentlongitude"));
        name = getArguments().getString("incidentname");


        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMapView = (MapView) getActivity().findViewById(R.id.mapview);
        this.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        theMap = googleMap;
        theMap.getUiSettings().setZoomControlsEnabled(true);

        System.out.println("ONMAPREADY!!");

        LatLng incidentPosition = new LatLng(latitude,longitude);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        theMap.setMyLocationEnabled(true);
        theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(incidentPosition, 13));

        theMap.addMarker(new MarkerOptions()
                .title(name)
                .position(incidentPosition));
    }
}
