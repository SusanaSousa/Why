package edu.feup.dansus.why_maps;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
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
    private MenuItem btAppBar;
    private ArrayList<Integer> mBPM = new ArrayList<>(); // Array list where the correspondent event samples will be added
    private ArrayList<LatLng> mLoc = new ArrayList<>(); //Array list where the two location points will be saved.
    private boolean isEventHappening = false;
    private boolean isPartOfEvent=false;
    private long startTime;
    private long endTime;
    private Location location;
    private Location startLoc;
    private Location endLoc;

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
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, BioLib.REQUEST_ENABLE_BT);
                } else {
                    if (!connectionState) {
                        Connect();
                    } else {
                        Disconnect();
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.why_maps_main, menu);
        this.btAppBar= menu.getItem(1);
        super.onCreateOptionsMenu(menu, inflater);
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
                    Log.i("PUSH-BUTTON", "Pushbutton was clicked");
                    break;

                case BioLib.MESSAGE_PEAK_DETECTION:
                    BioLib.QRS qrs = (BioLib.QRS)msg.obj;
                    Log.i("HR Info", "PEAK: " + qrs.position + "  BPMi: " + qrs.bpmi + " bpm  BPM: " + qrs.bpm + " bpm  R-R: " + qrs.rr + " ms");

                    if (qrs.bpm > 200){
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Alert message to be shown");
                        alertDialog.show();
                    }
                    break;

                case BioLib.UNABLE_TO_CONNECT_DEVICE:
                    Toast.makeText(app, R.string.NoConnection, Toast.LENGTH_LONG).show();
                    break;

                case BioLib.STATE_CONNECTED:
                    // Informing the user
                    Toast.makeText(app, getString(R.string.SuccessfulConnection) + " " + deviceToConnect.getName(), Toast.LENGTH_SHORT).show();

                    // Adjusting system variables
                    connectionState = true;

                    // Adjusting buttons
                    btAppBar.setIcon(R.drawable.ic_bluetooth_connected_black_24dp); // Updating the action bar icon
                    playBtn.setVisibility(View.VISIBLE); // Showing record button

                    // Syncing clocks
                    Date currentDate = Calendar.getInstance().getTime();

                    try {
                        lib.SetRTC(currentDate);
                    } catch (Exception e){
                        Log.i("Clock syncing", "Failed");
                    }

                    break;

                case BioLib.MESSAGE_DISCONNECT_TO_DEVICE:
                    // Informing the user
                    Toast.makeText(app, R.string.SuccessDisc, Toast.LENGTH_SHORT).show();

                    // Updating system variables
                    connectionState = false;
                    btAppBar.setIcon(R.drawable.ic_bluetooth_black_24dp); // Updating the action
                    playBtn.setVisibility(View.INVISIBLE); // Hiding record button
                    break;

                case BioLib.STATE_CONNECTING:
                    Toast.makeText(app, R.string.ConnectingString, Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    };

    /***
     * Connect to device.
     */
    private void Connect() {
        try {
            deviceToConnect = lib.mBluetoothAdapter.getRemoteDevice(app.VJ_ADDRESS);
            lib.Connect(app.VJ_ADDRESS, NUM_SAMPLES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***
     * Disconnect from device.
     */
    private void Disconnect() {
        try {
            lib.Disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(app, R.string.NoDisc, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroyView(){
        if (connectionState){ // Destroying connection when fragment is gone
            Disconnect();
            super.onDestroyView();
        }

        super.onDestroyView();
    }

    private void processBPMInfo(int bpm, boolean isEventHappening, boolean isPartOfEvent) {
        int threshold=170; // here we will call user threshold
        //Check is the current sample is part of an Event or not
        isPartOfEvent = bpm > threshold;

        // Dealing with isEventHappening and isPartOfEvent possible combinations

        if (isEventHappening==false && isPartOfEvent==true){ //the event is not happening and this is the sample that initializes the event
            mBPM.clear(); //We know that this is the first element, so here we clear the array making sure it is empty
            mBPM.add(bpm); //Add the sample to the array
            startTime = SystemClock.elapsedRealtime(); //Initializes the timer in order to calculate event's duration
            //trigger to take a photo
            startLoc = location;

            isEventHappening=true;
        }else if (isEventHappening==true && isPartOfEvent==true){
            mBPM.add(bpm);
        }else if (isEventHappening==true && isPartOfEvent==false){ //the event is happening and ends here
            endTime = SystemClock.elapsedRealtime();
            //trigger to take photo
            endLoc = location;
            isEventHappening=false;
            processEventInfo(mBPM,mLoc,startTime, endTime, startLoc, endLoc);

        }else{
            //do nothing
        }


    }

    private void processEventInfo(ArrayList<Integer> mBPM, ArrayList<LatLng> mLoc, long startTime, long endTime, Location starrLoc, Location endLoc) {

        // Calculate the average heart rate
        double avgBPM=calculateAverage(mBPM);

        // Calculate the event's duration
        long tDelta = endTime - startTime; //miliseconds
        double duration = tDelta / 1000.0; //seconds

        //Interpolate locations

        //Add information to dataBase
        Date date = new Date();
    }

}

