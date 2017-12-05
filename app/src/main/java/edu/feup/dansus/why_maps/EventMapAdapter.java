package edu.feup.dansus.why_maps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Susana on 26/11/2017.
 */

public class EventMapAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Event> mEvents = new ArrayList<>();
    private Event mEvent;

    public EventMapAdapter (LayoutInflater layout){
        this.mInflater=layout;
        this.mEvents=WhyApp.events;
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
    // In order to define the contents of the InfoWindow based on the marker

    @Override
    public View getInfoContents(Marker marker) {

        View view = mInflater.inflate(R.layout.info_window,null);
        final double lat = marker.getPosition().latitude;
        final double lon = marker.getPosition().longitude;

        //Check the correspondent event from mEvent
        for (int i=0; i<mEvents.size();i++){
            if (mEvents.get(i).getLatitude()==lat && mEvents.get(i).getLongitude()==lon){
                mEvent=mEvents.get(i);
            }
        }

        //Populate fields
        //Event Date
        TextView eventOn=(TextView) view.findViewById(R.id.eventOn_tv);
        eventOn.setText("Event on " + formatEventData(mEvent.getDate())); //Setting date as a string

        //Event Duration
        TextView eventDuration=(TextView) view.findViewById(R.id.setDuration_tv);
        eventDuration.setText(Double.toString(mEvent.getDuration())); //Setting duration as a string

        //Heart Rate
        TextView heartRate=(TextView) view.findViewById(R.id.setHearRate_tv);
        heartRate.setText(Double.toString(mEvent.getHearRate())); //Setting heartRate from double to string

        // Notes
        TextView notes = (TextView) view.findViewById(R.id.notesContent_tv);
        notes.setText(mEvent.getNotes());

        // ImageView Front
        ImageView imgFront = (ImageView) view.findViewById(R.id.front_start);
        Bitmap frontBitmap = BitmapFactory.decodeFile(mEvent.getPhotoStartFront());
        imgFront.setImageBitmap(frontBitmap);

        // ImageView Rear
        ImageView imgRear = (ImageView) view.findViewById(R.id.rear_start);
        Bitmap rearBitmap = BitmapFactory.decodeFile(mEvent.getPhotoStartRear());
        imgRear.setImageBitmap(rearBitmap);

        return view;
    }

    private String formatEventData (Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, d 'of' MMM, HH:mm", Locale.US);
        return dateFormat.format(date);
    }
}
