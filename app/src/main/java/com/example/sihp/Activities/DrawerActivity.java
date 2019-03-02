package com.example.sihp.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sihp.Models.ComPojo;
import com.example.sihp.Utils.FetchURL;
import com.example.sihp.Utils.GPSTracker;
import com.example.sihp.R;
import com.example.sihp.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, TaskLoadedCallback {


    private FirebaseAuth mAuth;
    private GoogleMap mMap;

    LatLng markedLocation;
    ArrayList markerPoints = new ArrayList();
    private Polyline currentPolyline;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    FloatingActionButton fabnav,fabadd;
    FirebaseUser currentUser;
    EditText searchBar, searchBar1;
    GPSTracker gps;
    double lat;
    double longi;

    private static String ORDER_KEY = "sort_order_key";
    private static String RANDOM = "random";
    private static String ADD = "add";
    private static String SINGLE = "single";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser() ;
        if (mAuth.getCurrentUser()!=null){
            TextView mail_id = findViewById(R.id.profile_mail_ID);
           // mail_id.setText(""+mAuth.getCurrentUser().getEmail());

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            preferences = getSharedPreferences("pref_file", Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.apply();

            fabnav = findViewById(R.id.fabnav);
            fabadd = findViewById(R.id.fabadd);
            searchBar = findViewById(R.id.searchEditText);

            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            gps = new GPSTracker(this);
            if (gps.canGetLocation) {
                lat = gps.getLatitude();
                longi = gps.getLongitude();
            }
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    gps = new GPSTracker(DrawerActivity.this);
                    // check if GPS enabled
                    if (gps.canGetLocation()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        updatePointer(latitude, longitude);
                        // \n is for new line
                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            fabnav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    searchBar.setVisibility(View.VISIBLE);
                    loadMapFromTo();

                }
            });
            fabadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DrawerActivity.this, "Add clicked", Toast.LENGTH_SHORT).show();
                    addProblem();
                }
            });
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }else {
            startActivity(new Intent(this, LoginActivity.class));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!= null){
            TextView mail_id = findViewById(R.id.profile_mail_ID);
           // mail_id.setText(""+mAuth.getCurrentUser());
        }
    }

    private void addProblem() {
        markerPoints.clear();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_layout,null);

        final Spinner sp = view.findViewById(R.id.spinner);
        final TextView textView = view.findViewById(R.id.showlatlongi);
        dialog.setView(view);
        dialog.setTitle("Add Problem on this location");
        textView.setText(""+markedLocation);
        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(DrawerActivity.this, "Added", Toast.LENGTH_SHORT).show();
                String problem = sp.getSelectedItem().toString();
                String location = textView.getText().toString();
                setVauesToDB(problem,markedLocation.latitude,markedLocation.longitude);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.show();
    }

    private void setVauesToDB(String problem, double latitude, double longitude) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("SIHP");

        Map<String,Object> map = new HashMap<>();
        map.put("problem",problem);
        map.put("lattitude",latitude);
        map.put("longitude",longitude);

        myRef.child("Problems").push().setValue(map);
    }

    private void loadMapFromTo() {
        markerPoints.clear();
        mMap.clear();
        editor.putString(ORDER_KEY,SINGLE);
        editor.commit();
    }

    private void updatePointer(double latitude, double longitude) {
        mMap.setMyLocationEnabled(true);
        LatLng sydney = new LatLng(lat, longi);

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(sydney, 7);
        mMap.animateCamera(cameraUpdateFactory);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            searchBar.setVisibility(View.VISIBLE);
            loadMapFromTo();
            return true;
        }
        if (id == R.id.action_logout){
            mAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            markerPoints.clear();
            mMap.clear();
            editor.putString(ORDER_KEY,RANDOM);
            editor.commit();

        } else if (id == R.id.nav_gallery) {
            markerPoints.clear();
            editor.putString(ORDER_KEY,ADD);
            editor.commit();
            fabadd.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_share) {
            Toast.makeText(DrawerActivity.this, "Share app", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_send) {
            Toast.makeText(DrawerActivity.this, "About option", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        GPSTracker gpsTracker = new GPSTracker(this);
        LatLng sydney = new LatLng(lat, longi);

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 5));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (preferences.getString(ORDER_KEY,SINGLE).equals(SINGLE)){
                    Toast.makeText(DrawerActivity.this, "Single", Toast.LENGTH_SHORT).show();
                    getSingle(latLng);
                }
                else if (preferences.getString(ORDER_KEY, RANDOM).equals(RANDOM)){
                    Toast.makeText(DrawerActivity.this, "Randowm", Toast.LENGTH_SHORT).show();
                    getRandom(latLng);
                }else if (preferences.getString(ORDER_KEY,ADD).equals(ADD)){
                    Toast.makeText(DrawerActivity.this, "Add begins", Toast.LENGTH_SHORT).show();
                    addPlaces(latLng);
                }

            }
        });

    }

    private void addPlaces(LatLng latLng) {

        if (markerPoints.size() > 1) {
            markerPoints.clear();
            mMap.clear();
        }

        markerPoints.add(latLng);

        MarkerOptions options = new MarkerOptions();

        options.position(latLng);

         if (markerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        mMap.addMarker(options);

        if (markerPoints.size() >= 1) {
            markedLocation = (LatLng) markerPoints.get(0);

        }
    }

    private void getSingle(final LatLng latLng) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("SIHP/Problems");

        final ArrayList<ComPojo> list = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    ComPojo post = ds.getValue(ComPojo.class);
                    list.add(post);

                }

                sendData(list,latLng);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        Log.i("fSize1:",""+list.size());

    }

    private void sendData(ArrayList<ComPojo> list, LatLng latLng) {
        Log.i("fSize2:",""+list.size());
        LatLng currentLocation = new LatLng(lat, longi);
        if (markerPoints.size() > 1) {
            markerPoints.clear();
            mMap.clear();
            //  Log.i("click:", "" + i++);
        }
        // Adding new item to the ArrayList
        markerPoints.add(latLng);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(latLng);

        if (markerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } /*else if (markerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }*/

        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);

        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 1) {
            LatLng origin = currentLocation;
            LatLng dest = (LatLng) markerPoints.get(0);

            double lat1 = currentLocation.latitude;
            double lng1 = currentLocation.longitude;

            double lat2 = dest.latitude;
            double lng2 = dest.longitude;

            double total_distance = getDistance(lat1,lng1,lat2,lng2);

            Toast.makeText(DrawerActivity.this, "Total Distance is :"+total_distance+" miles", Toast.LENGTH_SHORT).show();

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            Log.d("url-->", url);

            FetchURL fetchURL = new FetchURL(DrawerActivity.this,list);
            fetchURL.execute(url, "driving");

        }
    }

    private double getDistance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)); double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        return dist;
    }

    private void getRandom(final LatLng latLng) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("SIHP/Problems");
        final ArrayList<ComPojo> list = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    ComPojo post = ds.getValue(ComPojo.class);
                    list.add(post);

                }

                sendTwoData(list,latLng);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void sendTwoData(ArrayList<ComPojo> list, LatLng latLng) {
        if (markerPoints.size() > 1) {
            markerPoints.clear();
            mMap.clear();
            //  Log.i("click:", "" + i++);
        }
        // Adding new item to the ArrayList
        markerPoints.add(latLng);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(latLng);

        if (markerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (markerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);

        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {
            LatLng origin = (LatLng) markerPoints.get(0);
            LatLng dest = (LatLng) markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            Log.d("url-->", url);

            FetchURL fetchURL = new FetchURL(DrawerActivity.this,list);
            fetchURL.execute(url, "driving");

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String key = "key=" + getResources().getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    @Override
    public void onTaskDone(Object... values) {

        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onCompareDone(ArrayList<ComPojo> list) {
        if (list.size()> 0){
            //ArrayList<ComPojo> markersArray = new ArrayList<ComPojo>();

            for(int i = 0 ; i < list.size() ; i++) {

                //String latLng = list.get(i).getLocation();

                Log.i("act:",""+list.get(i).lattitude);
                drawCircle(new LatLng(list.get(i).getLattitude(), list.get(i).getLongitude()));
            }

        }else {
            showAlert();
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

    private void showAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("No Data Found in our Records!");
        dialog.setMessage("Please ....");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
