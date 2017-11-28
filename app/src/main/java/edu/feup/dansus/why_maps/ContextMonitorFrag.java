package edu.feup.dansus.why_maps;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Bio.Library.namespace.BioLib;

/**
 * Created by dany on 26-11-2017.
 * Context monitor screen
 */

public class ContextMonitorFrag extends Fragment implements OnMapReadyCallback {
    private SupportMapFragment mapFragment; // Map will be a fragment
    private GoogleMap mMap;
    private List<Event> mEvents = new ArrayList<>(); // Array pointing to the global events array
    private DatabaseHandler dbHandler;
    private BluetoothDevice deviceToConnect;
    private boolean connectionState=false;
    private BioLib lib = null;
    private WhyApp app;
    private final static int NUM_SAMPLES = 5;
    private boolean isMonitoring = false;
    private FloatingActionButton playBtn;

    //Buffer
    // CircularFifoQueue<Integer> queue = new CircularFifoQueue(5); //buffering data


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.context_monitor_layout, container, false);

        // Getting a reference to the activity's DB Handler
        dbHandler = new DatabaseHandler(this.getActivity());

        // Getting a reference to the global events list
        app = (WhyApp) getActivity().getApplicationContext();
        mEvents = app.events;

        // Get the SupportMapFragment (the activity one) and request notification when the map is ready to be used.
        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        // Initializing BioLib library
        try
        {
            lib = new BioLib(this.getActivity(), mHandler);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // So we can handle action bar clicks from the fragment
        setHasOptionsMenu(true);

        // Instantiating and configuring the play button
        playBtn = (FloatingActionButton) view.findViewById(R.id.playButton);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isMonitoring == false){
                    isMonitoring = true;
                    playBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                } else {
                    isMonitoring = false;
                    playBtn.setImageResource(R.drawable.ic_fiber_manual_record_black_24dp);
                }
            }
        });


        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bluetooth: {

                // Firstly, checking if Bluetooth is enabled and asking user to enable it in case it's not
                if (!isBluetoothEnabled()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle(R.string.AlertTitle);
                    alertDialog.setMessage(getString(R.string.AlertText));
                    alertDialog.show();
                } else {
                    if (connectionState == false) {
                        Connect(item);
                    } else {
                        Disconnect(item);
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map){ // Map is ready!
        mMap = map;

        map.setInfoWindowAdapter(new EventMapAdapter(getLayoutInflater(),mEvents));

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

        //

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

    /**
     * The Handler that gets information back from the BioLib
     */
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case BioLib.MESSAGE_PUSH_BUTTON:
                    Date DATETIME_PUSH_BUTTON = (Date)msg.obj;
                    int numOfPushButton = msg.arg1;
                    Log.i("PUSH-BUTTON", "numOfPushButton" + numOfPushButton + DATETIME_PUSH_BUTTON);

                    break;


                case BioLib.MESSAGE_PEAK_DETECTION:
                    BioLib.QRS qrs = (BioLib.QRS)msg.obj;
                    Log.i("HR Info", "PEAK: " + qrs.position + "  BPMi: " + qrs.bpmi + " bpm  BPM: " + qrs.bpm + " bpm  R-R: " + qrs.rr + " ms");

                    if (qrs.bpm > 170){
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Alert message to be shown");
                        alertDialog.show();
                    }

            }
        }
    };

    /***
     * Connect to device.
     */
    private void Connect(MenuItem item) {
        try {
            deviceToConnect = lib.mBluetoothAdapter.getRemoteDevice(app.VJ_ADDRESS);
            lib.Connect(app.VJ_ADDRESS, NUM_SAMPLES);
            connectionState = true;
            item.setIcon(R.drawable.ic_bluetooth_connected_black_24dp); // Updating the action bar icon
            Snackbar.make(getView(), getString(R.string.SuccessfulConnection)+" "+deviceToConnect.getName(), Snackbar.LENGTH_LONG).show();
            playBtn.setVisibility(View.VISIBLE); // Showing record button
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(getView(), R.string.NoConnection, Snackbar.LENGTH_LONG).show();
        }
    }
    /***
     * Disconnect from device.
     */
    private void Disconnect(MenuItem item)
    {
        try
        {
            lib.Disconnect();
            connectionState = false;
            item.setIcon(R.drawable.ic_bluetooth_black_24dp); // Updating the action
            Snackbar.make(getView(), R.string.SuccessDisc, Snackbar.LENGTH_SHORT).show();
            playBtn.setVisibility(View.INVISIBLE); // Hiding record button
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Snackbar.make(getView(), R.string.NoDisc, Snackbar.LENGTH_SHORT).show();
        }
    }

    private double calculateAverage(ArrayList <Integer> marks) {
        // Source: https://stackoverflow.com/questions/10791568/calculating-average-of-an-array-list

        if (marks == null || marks.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (Integer mark : marks) {
            sum += mark;
        }

        return sum / marks.size();
    }

    private boolean isBluetoothEnabled()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();

    }

    }
