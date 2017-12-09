package edu.feup.dansus.why_maps;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import Bio.Library.namespace.BioLib;

/**
 * Created by dany on 26-11-2017.
 * Context monitor screen
 */

public class ContextMonitorFrag extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, PictureCapturingListener {
    private SupportMapFragment mapFragment; // Map will be a fragment
    private GoogleMap mMap;
    private List<Event> mEvents = new ArrayList<>(); // Array pointing to the global events array
    private DatabaseHandler dbHandler;
    private BluetoothDevice deviceToConnect;
    private boolean connectionState=false;
    private BioLib lib = null;
    private WhyApp app;
    private final static int NUM_SAMPLES = 10;
    private boolean isMonitoring = false;
    private FloatingActionButton playBtn;
    private MenuItem btAppBar;
    private ArrayList<Integer> mBPM = new ArrayList<>(); // Array list where the correspondent event samples will be added
    private ArrayList<LatLng> mLocs = new ArrayList<>(); //Array list where the two location points will be saved.
    private boolean isEventHappening = false;
    private boolean isPartOfEvent=false;
    private boolean isPartOfUserEvent = false;
    private long startTime;
    private long endTime;
    private int instBPM;

    // Location
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = "ContextMonitor";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000; // code to handle connection errors to Google Play Services
    private LocationRequest mLocationRequest;
    private Location mLocation; // current location
    private Location startLoc;
    private Location endLoc;
    private MediaPlayer mPlayer;
    private EventMapAdapter mapAdapter;

    // Cameras
    private String startFrontPic = null;
    private String startRearPic = null;
    //private String endFrontPic = null;
    //private String endRearPic = null;
    private APictureCapturingService pictureService; // picture capturing service
    private int picCounter = 0; // we can't use the boolean event status, because pictures are delivered async


    //Buffer
    // CircularFifoQueue<Integer> queue = new CircularFifoQueue(5); //buffering data

    // Play sound when event goes above EVENT_ALARM_THRESH
    private static final int EVENT_ALARM_THRESH = 60000;
    private boolean hasAlarmPlayed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.context_monitor_layout, container, false);

        // Getting a reference to the activity's DB Handler
        dbHandler = new DatabaseHandler(this.getActivity());

        // Picture taking service
        pictureService = PictureCapturingServiceImpl.getInstance(this.getActivity());

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

        // Creating the Location interface
        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // Prefer GPS
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds (minimum interval between location updates)
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds (best case scenario)

        return view;
    }

    // Map callbacks

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_bluetooth: {

                // Firstly, checking if Bluetooth is enabled and asking user to enable it in case it's not
                if (!isBluetoothEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, BioLib.REQUEST_ENABLE_BT);
                    break;
                } else {
                    if (!connectionState) {
                        Connect();
                        break;
                    } else {
                        Disconnect();
                        break;
                    }
                }
            }

            case R.id.action_heart: {
                if (connectionState && isMonitoring) {
                    isPartOfUserEvent = true;
                    break;
                }

                break;
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
    public void onMapReady(final GoogleMap map){ // Map is ready!
        mMap = map;

        mapAdapter = new EventMapAdapter(getLayoutInflater());
        map.setInfoWindowAdapter(mapAdapter);

        // Enabling Current location and moving the camera there
        mMap.setMyLocationEnabled(true); // By this time, we will have the required permissions (fragment doesn't launch without it)
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setBuildingsEnabled(true);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Getting the clicked event ID
                long currentID = 0;
                double lat = marker.getPosition().latitude;
                double lon = marker.getPosition().longitude;

                for (Event ev: mEvents){
                    if (ev.getLatitude() == lat && ev.getLongitude() == lon){
                        currentID = ev.getEventID();
                    }
                }
                showAddNotesDialog(currentID); // Showing the fragment
            }
        });

        loadEventsToMap();
    }



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
                    if (isMonitoring) {
                        isPartOfUserEvent = true;
                    }
                    break;

                case BioLib.MESSAGE_PEAK_DETECTION:
                    BioLib.QRS qrs = (BioLib.QRS)msg.obj;
                    Log.i("HR Info", "PEAK: " + qrs.position + "  BPMi: " + qrs.bpmi + " bpm  BPM: " + qrs.bpm + " bpm  R-R: " + qrs.rr + " ms");

                    if (isMonitoring && isPartOfUserEvent){
                        processInsUserEvent(qrs.bpm);
                    }

                    if (isMonitoring){
                        processBPMInfo(qrs.bpm);
                    }

                    break;

                case BioLib.UNABLE_TO_CONNECT_DEVICE:
                    Toast.makeText(app, R.string.NoConnection, Toast.LENGTH_LONG).show();
                    break;

                case BioLib.STATE_CONNECTED:
                    // Informing the user of the successful operation
                    Toast successT = Toast.makeText(app, Html.fromHtml(getString(R.string.SuccessfulConnection) + " " + "<strong>" + deviceToConnect.getName() + "</strong>"), Toast.LENGTH_SHORT);
                    TextView v = (TextView) successT.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    successT.show();

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
                    isPartOfEvent = false;
                    isPartOfUserEvent = false;
                    isPartOfEvent = false;
                    isMonitoring = false;
                    picCounter = 0;
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

            // Resetting state variables
            isPartOfEvent = false;
            isPartOfUserEvent = false;
            isPartOfEvent = false;
            isMonitoring = false;
            hasAlarmPlayed = false;
            picCounter = 0;
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

    private void processBPMInfo(int bpm) {
        int threshold=app.currentUser.getUserThreshold(); // here we will call user threshold

        //Check is the current sample is part of an Event or not
        if (bpm > 0.95*threshold){
            isPartOfEvent = true;
        } else {
            isPartOfEvent = false;
        }

        // Dealing with isEventHappening and isPartOfEvent possible combinations

        if (isEventHappening==false && isPartOfEvent==true){ //the event is not happening and this is the sample that initializes the event
            mBPM.clear(); //We know that this is the first element, so here we clear the array making sure it is empty
            mBPM.add(bpm); //Add the sample to the array
            startTime = SystemClock.elapsedRealtime(); //Initializes the timer in order to calculate event's duration

            startLoc = mLocation;

            // Photo trigger
            pictureService.startCapturing(this);
            isEventHappening=true;
            hasAlarmPlayed = false;

            // User feedback
            Toast.makeText(app, R.string.RecordString, Toast.LENGTH_SHORT).show();

        }else if (isEventHappening==true && isPartOfEvent==true){
            mBPM.add(bpm);

            long timeSinceStart = SystemClock.elapsedRealtime() - startTime;

            if (timeSinceStart > EVENT_ALARM_THRESH && !hasAlarmPlayed){ // We play an alert sound if event is too long
                mPlayer = MediaPlayer.create(this.getContext(), R.raw.eye_tiger);
                mPlayer.start(); // Start playing
                hasAlarmPlayed = true;
            }



        }else if (isEventHappening==true && isPartOfEvent==false){ //the event is happening and ends here
            endTime = SystemClock.elapsedRealtime();
            endLoc = mLocation;
            isEventHappening=false;
            processEventInfo();
            Toast.makeText(app, R.string.StopEventString, Toast.LENGTH_SHORT).show();
        }
    }

    private void processInsUserEvent(int bpm) {
        instBPM = bpm;
        //The event's duration is defined as 1s
        double duration = 1; //seconds
        //location - in this case there is no need to interpolate locations. Only the startlocation is considered
        startLoc = mLocation;
        //Add information to dataBase
        Date date = new Date();
        // Creating the event object and adding it to the DB
        Event currentEvent = new Event (app.currentUser,date, null, null, null,startLoc.getLatitude(), startLoc.getLongitude(),instBPM,duration);
        dbHandler.addEvent(currentEvent);
        app.events = dbHandler.getAllEvents();
        dbHandler.close();

        //after processing this user triggered event, the status returns to false;
        isPartOfUserEvent=false;

        loadEventsToMap();
    }

    // Google Location Services overrides

    @Override
    public void onConnected(@Nullable Bundle bundle) { // When location services are connected
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null){ // location may be null, initially
            // we start requesting location updates, to be delivered to the listener
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            updateMapLocation();
        }

    }

    @Override
    public void onConnectionSuspended(int i) { // Location services connection successful
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Source: https://github.com/treehouse/android-location-example/blob/master/app/src/main/java/teamtreehouse/com/iamhere/MapsActivity.java
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this.getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onLocationChanged(Location location) { // Whenever a new location is detected by Google Play Services
        mLocation = location;
        updateMapLocation();
    }

    // Fragment overrides

    @Override
    public void onResume(){ // onResume() is called when fragment is started or when its state is restored
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause(){ // companion to onPause
        super.onPause();
        if (mGoogleApiClient.isConnected()){ // it only makes sense to disconnect if we are connected
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void updateMapLocation(){
        double currentLat = mLocation.getLatitude();
        double currentLng = mLocation.getLongitude();

        LatLng currentLatLng = new LatLng(currentLat, currentLng);

        // Creating the new camera position
        CameraPosition cameraPos = new CameraPosition.Builder() // Builder pattern (to set multiple settings at once)
                .target(currentLatLng)
                .zoom(10) // city-level zoom
                .tilt(45)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

    }

    private LatLng getAvgLocation (Location startlLoc, Location endLoc){ // Getting the average coordinates between a set of two Locations
        double startLat = startLoc.getLatitude();
        double startLng = startLoc.getLongitude();
        double endLat = endLoc.getLatitude();
        double endLng = endLoc.getLongitude();

        // Averaging (assuming everything is correctly given as a double)
        LatLng avgLoc = new LatLng((startLat+endLat)/2, (startLng+endLng)/2);

        return avgLoc;
    }

    private void processEventInfo() {

        // Calculate the average heart rate
        double avgBPM=calculateAverage(mBPM);

        // Calculate the event's duration
        long tDelta = endTime - startTime; //miliseconds
        double duration = tDelta / 1000.0; //seconds

        //Interpolate locations
        LatLng avgLocation = getAvgLocation(startLoc, endLoc);

        //Date
        Date date = new Date();

        // Creating the event object and adding it to the DB
        Event currentEvent = new Event(app.currentUser, date, startFrontPic, startRearPic, "", avgLocation.latitude, avgLocation.longitude, avgBPM, duration);
        dbHandler.addEvent(currentEvent);
        WhyApp.events = dbHandler.getAllEvents();
        dbHandler.close();
        loadEventsToMap();
    }

    private void loadEventsToMap(){
        //Getting location information from all events in DB (assuming that they belong to the same user)
        ArrayList<LatLng> eventLoc = new ArrayList<LatLng>();
        double latitude, longitude;

        // Updating event list
        mEvents = dbHandler.getAllEvents();
        dbHandler.close();

        // Loading events
        for (int i=0; i < mEvents.size(); i++){
            latitude=mEvents.get(i).getLatitude();
            longitude=mEvents.get(i).getLongitude();
            LatLng newEvent= new LatLng(latitude, longitude);
            eventLoc.add(newEvent);
        }


        // Add a marker for all the events in DB

        // Custom marker icon
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromBitmap(vectorToBitmap(R.drawable.ic_location_on_roxo_24dp));

        for (int i=0; i < eventLoc.size();i++){
            mMap.addMarker(new MarkerOptions()
                    .position(eventLoc.get(i))
                    .title("Event "+ i)
                    .icon(customMarker));
        }
    }


    /**
     * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
     * for use as a marker icon. Workaround adapted from code
     * originally by the Google Maps team
     */
    private Bitmap vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private void showAddNotesDialog(long id) {
        AddNotesDialog dialog = AddNotesDialog.newInstance(id);
        dialog.show(getActivity().getSupportFragmentManager(),"add_notes_dialog");
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override // callback when all pictures are taken
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
//
//        if (picturesTaken != null && !picturesTaken.isEmpty()) {    // photos were captured
//            Toast.makeText(this.getContext(), "Picture was taken!", Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) { // runs each time a picture (front or rear) is taken
        if (pictureData != null && pictureUrl != null) { // pictures were successfully taken

            // Optional resizing could happen here (at a bitmap level, which would have to be extracted)
            picCounter += 1;

            if (pictureUrl.contains("0_pic.jpg")) {
                startRearPic = pictureUrl;
            } else if (pictureUrl.contains("1_pic.jpg")) {
                startFrontPic = pictureUrl;
            }

            if (picCounter == 2){ // end of the event
                picCounter = 0;
            }

        }
    }


}
