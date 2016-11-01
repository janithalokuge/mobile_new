package com.example.janitha.myapplication;

/**
 * Created by Siri on 10/30/2016.
 */
import com.example.janitha.myapplication.MainActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeLocationActivity extends AppCompatActivity implements OnMapReadyCallback{

    public MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_location);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_home_map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map){
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            Toast.makeText(this, "Please enable GPS",
                    Toast.LENGTH_LONG).show();
        }

        if (MainActivity.homeLastLocation != null) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(MainActivity.homeLastLocation.getLatitude(),MainActivity.homeLastLocation.getLongitude()))
                    .title("Current Position"));
        }
    }
}
