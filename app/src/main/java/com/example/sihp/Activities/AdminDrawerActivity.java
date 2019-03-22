package com.example.sihp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class AdminDrawerActivity extends AppCompatActivity {
    String current_user;
    ArrayList<Admin> admins ;
    LatLng latLang;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private static String CURRENT_LOCATION = "corrent_location";
    private static String LATTITUDE = "lattitude_key";
    private static String LONGITUDE = "longitude_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_admin_drawer);

        /*preferences = getSharedPreferences("SIHP", Context.MODE_PRIVATE);
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
        });*/
    }

    private void sendData(ArrayList<Admin> list) {
        /*admins = list;
        Log.i("logs:"," size "+list.size());
        for (int i=0;i<list.size();i++){
            Log.i("logs:",""+list.get(i).getEmail());
            if (current_user.equals(list.get(i).getEmail().trim())){

                String lat = list.get(i).getLat();
                String longi =list.get(i).getLongi();
                String name = list.get(i).getName();
                //latLang = new LatLng(lat,longi);

                Log.i("matched",lat+":"+longi+"   "+name);
                editor.putString(LATTITUDE, lat);
                editor.putString(LONGITUDE,longi);
                editor.commit();
                break;
            }
        }*/
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_range) {
            startActivity(new Intent(this,AdminMapsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}
