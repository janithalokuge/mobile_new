package com.example.janitha.myapplication;

/**
 * Created by Siri on 10/30/2016.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.janitha.myapplication.services.FenceEnterService;
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

    Activity currentActivity =this;
    Context currentContext;

    private MapFragment mapFragment;
    private GoogleMap map;
    private Location currentLocation;
    private Marker currentMarker;
    private  Circle circle;

    TextView textView_LatitudeValue;
    TextView textView_LongitudeValue;
    EditText editText_FenceRadius;
    Button button_UpdateLocation;
    String fenceSettingsRadius = "500m";
    private int fenceRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_location);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_home_map);
        mapFragment.getMapAsync(this);
        currentContext = getApplicationContext();

        button_UpdateLocation = (Button)findViewById(R.id.button_UpdateLocation) ;
        textView_LatitudeValue = (TextView)findViewById(R.id.textView_LatitudeValue);
        textView_LongitudeValue = (TextView)findViewById(R.id.textView_LongitudeValue);
        editText_FenceRadius = (EditText)findViewById(R.id.editText_FenceRadius);
        //fenceRadius = Integer.parseInt(editText_FenceRadius.getText().toString());
        if(AppData.getData(currentContext,AppData.STR_HOME_LOCATOIN_FENCE_RADIUS, Integer.class) != null){
           fenceRadius = (int)AppData.getData(currentContext,AppData.STR_HOME_LOCATOIN_FENCE_RADIUS, Integer.class);
        }
        else{
            fenceRadius = 100;
        }

        editText_FenceRadius.setText(""+fenceRadius);


        button_UpdateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location tempLocation = new Location("");
                tempLocation.setLatitude(currentMarker.getPosition().latitude);
                tempLocation.setLongitude(currentMarker.getPosition().longitude);

                if (AppData.saveData(getApplicationContext(),AppData.STR_HOME_LOCATOIN, tempLocation)) {
                    Toast.makeText(currentContext, "Home Location LatLng updated", Toast.LENGTH_SHORT).show();
                    if(AppData.saveData(getApplicationContext(),AppData.STR_HOME_LOCATOIN_FENCE_RADIUS, new Integer( editText_FenceRadius.getText().toString() ))) {
                        Toast.makeText(currentContext, "Home Location Fence Radius updated", Toast.LENGTH_SHORT).show();
                        Toast.makeText(currentContext, "Home Location saved successfully", Toast.LENGTH_LONG).show();
                        button_UpdateLocation.setEnabled(false);

                        //Restart FenceEnter Service to update the Location Fence details
                        Intent fenceEnterServiceIntent = new Intent(currentActivity, FenceEnterService.class);
                        fenceEnterServiceIntent.putExtra("HomeLocation_FenceEnterStatus","User entered Home Location area");
                        currentContext.startService(fenceEnterServiceIntent);

                    }
                    else{
                        Toast.makeText(currentContext, "Error while updating Home Location Radius!", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(currentContext, "Error while saving Home Location!", Toast.LENGTH_LONG).show();
                }

            }
        });

        //move this method to mapReady method
//        editText_FenceRadius.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                if(AppData.getData(getApplicationContext(),AppData.STR_HOME_LOCATOIN, Location.class) != null) {
//                    currentLocation =(Location) AppData.getData(getApplicationContext(),AppData.STR_HOME_LOCATOIN, Location.class);
//                } else {
//                    currentLocation = getIntent().getExtras().getParcelable(MainActivity.LAST_HOME_LOCATION);
//                }
//                LatLng initialLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//
//
//                try {
//                    fenceRadius = Integer.parseInt(editText_FenceRadius.getText().toString());
//                    drawCircle(new LatLng(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude), fenceRadius);
//                }
//                catch (NumberFormatException e) { // When converting a null string to int
//                    e.printStackTrace();
//                    drawCircle(new LatLng(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude), 0);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                button_UpdateLocation.setEnabled(true);
//            }
//        });

    }

    @Override
    public void onMapReady(GoogleMap tempMap){
        this.map=tempMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        Location currentLocation = MainActivity.currentLocation;

        if(AppData.getData(currentContext,AppData.STR_HOME_LOCATOIN, Location.class) != null) {
            currentLocation =(Location) AppData.getData(currentContext,AppData.STR_HOME_LOCATOIN, Location.class);
        } else {
            currentLocation = getIntent().getExtras().getParcelable(MainActivity.LAST_HOME_LOCATION);
        }

        LatLng initialLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(initialLatLng));


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            Toast.makeText(this, "Please enable GPS",
                    Toast.LENGTH_LONG).show();
        }

        if (currentLocation != null) {
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
                button_UpdateLocation.setEnabled(true);
                drawMarker(latLng);
                updateLatLng();
                drawCircle(latLng,fenceRadius);
            }
        });

        editText_FenceRadius.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(AppData.getData(currentContext,AppData.STR_HOME_LOCATOIN, Location.class) != null) {
                    currentLocation =(Location) AppData.getData(currentContext,AppData.STR_HOME_LOCATOIN, Location.class);
                } else {
                    currentLocation = getIntent().getExtras().getParcelable(MainActivity.LAST_HOME_LOCATION);
                }
                LatLng initialLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());


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
                button_UpdateLocation.setEnabled(true);
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
