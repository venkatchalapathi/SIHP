package com.example.sihp.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.example.sihp.Models.ComPojo;
import com.example.sihp.R;
import com.example.sihp.Utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GPSTracker gps;
    private Circle mCircle;
    private Marker mMarker;
    double lat;
    double longi;
    private static String LATTITUDE = "lattitude_key";
    private static String LONGITUDE = "longitude_key";
    DatabaseReference reference;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maps);
        preferences = getSharedPreferences("SIHP", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();

        String  la = getIntent().getStringExtra("lat");
        String lo = getIntent().getStringExtra("lon");
        ArrayList<ComPojo> list = getIntent().getParcelableArrayListExtra("array");
        Log.i("size::",""+list.size());

        updateProblems(list);

        lat = Double.parseDouble(la);
        longi = Double.parseDouble(lo);

        Log.i("checkLOC",la+""+lo);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gps = new GPSTracker(this);
        /*if (gps.canGetLocation) {
            lat = gps.getLatitude();
            longi = gps.getLongitude();
        }*/
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, longi);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,16));

        mMap.addCircle(new CircleOptions()
                .center(sydney).radius(20000)
                .strokeColor(Color.BLUE).fillColor(0x220000FF)
                .strokeWidth(5.0f));

        mMap.setMyLocationEnabled(true);

    }

    private void updateProblems(ArrayList<ComPojo> list) {
        if (list.size()> 0){
            //ArrayList<ComPojo> markersArray = new ArrayList<ComPojo>();

            for(int i = 0 ; i < list.size() ; i++) {

                //String latLng = list.get(i).getLocation();

                Log.i("act:",""+list.get(i).lattitude);
                drawCircle(new LatLng(list.get(i).getLattitude(), list.get(i).getLongitude()));
            }

        }
    }
    private void drawCircle(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(450);
        circleOptions.strokeColor(Color.RED);
        circleOptions.fillColor(0xffffff00);
        circleOptions.strokeWidth(2);
        mMap.addCircle(circleOptions);
    }

}
