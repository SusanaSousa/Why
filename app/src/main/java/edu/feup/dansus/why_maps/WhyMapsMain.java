package edu.feup.dansus.why_maps;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class WhyMapsMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private SupportMapFragment mapFragment; // Map will be a fragment
    private GoogleMap map;
    private LocationRequest mLocationRequest; // For getting the location
    Location mCurrentLocation; // For getting the location

    //DB data
    private List<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_why_maps_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Dealing with DB
        DatabaseHandler db = new DatabaseHandler(this);


        //////////////////////////////////////////////////////////////////////////
       // Create sample data
        User sampleUser = new User();
        sampleUser.username = "Susana";
        sampleUser.userAge = 22;
        sampleUser.userProfession = "Eng";
        sampleUser.userThreshold = 2.1;

        Event sampleEvent = new Event();
        Time time = new Time(20);
        Date date = new Date();
        sampleEvent.user=sampleUser;
        sampleEvent.duration = time;
        sampleEvent.bodyTemp = 37;
        sampleEvent.date = date;
        sampleEvent.latitude = 41.183208;
        sampleEvent.longitude = -8.583512;
        sampleEvent.timeRR = 5;

        db.addEvent(sampleEvent);


        // Create sample data
        User sampleUser2 = new User();
        sampleUser2.username = "Joanita";
        sampleUser2.userAge = 20;
        sampleUser2.userProfession = "Agricultora";
        sampleUser2.userThreshold = 2.1;

        Event sampleEvent2 = new Event();
        Time time2 = new Time(20);
        Date date2 = new Date();
        sampleEvent2.user=sampleUser2;
        sampleEvent2.duration = time2;
        sampleEvent2.bodyTemp = 38;
        sampleEvent2.date = date;
        sampleEvent2.latitude =41.177063;
        sampleEvent2.longitude = -8.594091;
        sampleEvent2.timeRR = 8;

        db.addEvent(sampleEvent2);

        // Create sample data

        Event sampleEvent3 = new Event();
        Time time3 = new Time(20);
        Date date3 = new Date();
        sampleEvent3.user=sampleUser;
        sampleEvent3.duration = time3;
        sampleEvent3.bodyTemp = 35;
        sampleEvent3.date = date;
        sampleEvent3.latitude = 41.176412;
        sampleEvent3.longitude = -8.603869;
        sampleEvent3.timeRR = 4;

        db.addEvent(sampleEvent3);

        events = db.getAllEvents();

        events.get(0);
        for (Event event : events) {
            // do something
        }
    }

    @Override
    public void onMapReady(GoogleMap map){
        // Location information
        LatLng porto = new LatLng(41.177605, -8.596285);


        /////////////////////////////
        //Getting location information from all events in DB (assuming that they belong to the same user.
        final List<LatLng> eventLoc = new ArrayList<LatLng>();
        double latitude;
        double longitude;

        for (int i=0; i<events.size(); i++){
            latitude=events.get(i).getLatitude();
            longitude=events.get(i).getLongitude();

        LatLng newEvent= new LatLng(latitude, longitude);
        eventLoc.add(newEvent);
        }


        // Adding a pre-defined marker
        map.addMarker(new MarkerOptions().position(porto)
        .title("okokok"));

        /////////////////////////////////////
        // Add a marker for all events in DB
        for (int i=0; i<eventLoc.size();i++){
            map.addMarker(new MarkerOptions().position(eventLoc.get(i)).title("Event "+i));
        }


        // Moving the camera viewpoint
        map.moveCamera(CameraUpdateFactory.newLatLng(porto));

        // Zooming it a little bit
        map.moveCamera(CameraUpdateFactory.zoomTo(15)); // zoom level 15 gives street-level detail
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
        getMenuInflater().inflate(R.menu.why_maps_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Here we do fragment managing

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
