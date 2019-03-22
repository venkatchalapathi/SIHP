package com.example.sihp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sihp.Models.Admin;
import com.example.sihp.Models.ComPojo;
import com.example.sihp.R;
import com.example.sihp.Utils.GPSTracker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Drawer2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String current_user;
    ArrayList<Admin> admins ;
    LatLng latLang;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayList<ComPojo> tempList;
    FirebaseAuth auth;
    String lattutude;
    String longitude;
    private static String CURRENT_LOCATION = "corrent_location";
    private static String LATTITUDE = "lattitude_key";

    private static String LONGITUDE = "longitude_key";
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        auth = FirebaseAuth.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        preferences = getSharedPreferences("SIHP", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();

        admins = new ArrayList<>();

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        Toast.makeText(this, "" + currentFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
        current_user = currentFirebaseUser.getEmail();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SIHP/Admins");

        final ArrayList<Admin> list = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    Admin admin = ds.getValue(Admin.class);
                    list.add(admin);
                }
                sendData(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("SIHP/Problems");

        final ArrayList<ComPojo> data = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ComPojo comPojo = ds.getValue(ComPojo.class);
                    data.add(comPojo);
                }
                checkProblems(data);
                Log.i("location:","size "+data.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //gps = new GPSTracker(this);
    }
    private void checkProblems(ArrayList<ComPojo> data) {
        tempList = new ArrayList<>();

        /*Log.i("location:",""+lattutude);
        double lat = Double.parseDouble(lattutude);
        double lon = Double.parseDouble(longitude);*/
        LatLng center = new LatLng(17.968901,79.594055);
        for (int i=0;i<data.size();i++){
            LatLng dest = new LatLng(data.get(i).getLattitude(),data.get(i).getLongitude());
            if (isMarkerOutsideCircle(center,dest,20000)){
                Log.i("location:","Matched "+data.size());
                String problem = data.get(i).getProblem();
                double lati = data.get(i).getLattitude();
                double loni = data.get(i).getLattitude();
                ComPojo pojo = new ComPojo(problem,lati,loni);
                tempList.add(pojo);
            }
            Log.i("location:","size "+tempList.size());
        }

        recyclerView.setAdapter(new ProblemsAdapter(this,tempList));
       // updateProblems(tempList);
    }

    private boolean isMarkerOutsideCircle(LatLng centerLatLng, LatLng draggedLatLng, double radius) {
        float[] distances = new float[1];
        Location.distanceBetween(centerLatLng.latitude,
                centerLatLng.longitude,
                draggedLatLng.latitude,
                draggedLatLng.longitude, distances);
        return radius < distances[0];
    }

    private void sendData(ArrayList<Admin> list) {
        admins = list;
        Log.i("logs:"," size "+list.size());
        for (int i=0;i<list.size();i++){
            Log.i("logs:",""+list.get(i).getEmail());
            if (current_user.equals(list.get(i).getEmail().trim())){

                String lat = list.get(i).getLat();
                String longi =list.get(i).getLongi();
                String name = list.get(i).getName();
                //latLang = new LatLng(lat,longi);

                Log.i("matched",lat+":"+longi+"   "+name);
                lattutude = lat;
                longitude = longi;
                break;
            }
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Log.i("matched","Backpressed");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.acti_logout) {
            auth.signOut();
            finish();
            return true;
        }
        if (id == R.id.action_region){

           /* Intent intent = new Intent(this,AdminMapsActivity.class);
            intent.putParcelableArrayListExtra("array",tempList);
            intent.putExtra("lat",lattutude);
            intent.putExtra("lon",longitude);
            startActivity(intent);
*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
