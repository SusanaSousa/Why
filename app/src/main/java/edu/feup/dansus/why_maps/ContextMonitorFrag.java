package edu.feup.dansus.why_maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by dany on 26-11-2017.
 * Context monitor screen
 */

public class ContextMonitorFrag extends Fragment implements OnMapReadyCallback {
    private SupportMapFragment mapFragment; // Map will be a fragment
    private GoogleMap mMap;
    private List<Event> mEvents = new ArrayList<>(); // Array pointing to the global events array
    private WhyApp app;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.context_monitor_layout, container, false);

        // Getting a reference to the global events list
        app = (WhyApp) getActivity().getApplicationContext();
        mEvents = app.events;

        // Get the SupportMapFragment (the activity one) and request notification when the map is ready to be used.
        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        return view;
    }


    @Override
    public void onMapReady(GoogleMap map){ // Map is ready!
        mMap = map;

        //Getting location information from all events in DB (assuming that they belong to the same user.
        final List<LatLng> eventLoc = new ArrayList<LatLng>();
        double latitude;
        double longitude;

        // Enabling Current location and moving the camera there
        mMap.setMyLocationEnabled(true); // By this time, we will have the required permissions (fragment doesn't launch without it)
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setBuildingsEnabled(true);

        // Loading events
        for (int i=0; i<mEvents.size(); i++){
            latitude=mEvents.get(i).getLatitude();
            longitude=mEvents.get(i).getLongitude();
            LatLng newEvent= new LatLng(latitude, longitude);
            eventLoc.add(newEvent);
        }


        // Add a marker for all events in DB
        for (int i=0; i < eventLoc.size();i++){
            map.addMarker(new MarkerOptions()
                    .position(eventLoc.get(i))
                    .title("Event "+ i)); // TODO: custom marker
        }

        // Updating and zooming in on the current location
        // getDeviceLocation();

    }

    // Getting a BitmapDescriptor from a .xml defined icon
    // Source: https://gist.github.com/Ozius/1ef2151908c701854736

    /* private BitmapDescriptor getBitmapDescriptor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) getActivity().getResources().getDrawable(id, getActivity().getTheme());

            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    } */

    /* private void getDeviceLocation() {
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     * Source: https://developers.google.com/maps/documentation/android-api/current-place-tutorial

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = (Location) task.getResult();
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mLastKnownLocation = null;
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        } */
    }
