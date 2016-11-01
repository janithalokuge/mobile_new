package com.example.janitha.myapplication;

/**
 * Created by Siri on 10/30/2016.
 */
import com.example.janitha.myapplication.MainActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

public class HomeLocationActivity extends AppCompatActivity implements OnMapReadyCallback{

    private MapFragment mapFragment;
    private GoogleMap map;
    private Location lastLocation;
    private Marker currentMarker;
    private  Circle circle;

    TextView textView_LatitudeValue;
    TextView textView_LongitudeValue;
    EditText editText_FenceRadius;
    String fenceSettingsRadius = "500m";
    private int fenceRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_location);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_home_map);
        mapFragment.getMapAsync(this);

        textView_LatitudeValue = (TextView)findViewById(R.id.textView_LatitudeValue);
        textView_LongitudeValue = (TextView)findViewById(R.id.textView_LongitudeValue);
        editText_FenceRadius = (EditText)findViewById(R.id.editText_FenceRadius);
        fenceRadius = Integer.parseInt(editText_FenceRadius.getText().toString());

        editText_FenceRadius.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    fenceRadius = Integer.parseInt(editText_FenceRadius.getText().toString());
                    drawCircle(new LatLng(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude), fenceRadius);
                }
                catch (NumberFormatException e) { // When converting a null string to int
                    e.printStackTrace();
                    drawCircle(new LatLng(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap tempMap){
        this.map=tempMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        Location lastLocation = MainActivity.homeLastLocation;
        lastLocation = getIntent().getExtras().getParcelable(MainActivity.LAST_LOCATION);
        LatLng initialLatLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(initialLatLng));


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            Toast.makeText(this, "Please enable GPS",
                    Toast.LENGTH_LONG).show();
        }

        if (lastLocation != null) {
            currentMarker = map.addMarker(new MarkerOptions()
                    .position(initialLatLng)
                    .title("Current Position")
                    .draggable(true));
        }

        updateLatLng();

        drawCircle(initialLatLng,fenceRadius);  //draw the (circular)boundary of the Fence

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                drawMarker(latLng);
                updateLatLng();
                drawCircle(latLng,fenceRadius);
            }
        });
    }

    public void drawMarker(LatLng latLng){
        currentMarker.remove();
        currentMarker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Current Position")
                .draggable(true));
    }

    public void updateLatLng(){
        if(currentMarker !=null){
            textView_LatitudeValue.setText(new DecimalFormat("#.000").format(currentMarker.getPosition().latitude));
            textView_LongitudeValue.setText(new DecimalFormat("#.000").format(currentMarker.getPosition().longitude));
        }
    }

    public void drawCircle(LatLng latLng, int radius){
        if(currentMarker != null){
            if (circle != null) {
                circle.remove();
            }
            circle = map.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(radius)
                    .strokeColor(Color.RED)
                    .fillColor(0x44ff0000)
                    .strokeWidth(7));
        }

    }
}
