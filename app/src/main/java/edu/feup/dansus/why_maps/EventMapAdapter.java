package edu.feup.dansus.why_maps;

import android.view.LayoutInflater;
import android.view.View;
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
    LayoutInflater mInflater;
    ArrayList<Event> mEvents = new ArrayList<>();


    public EventMapAdapter (LayoutInflater layout, List<Event> events){
        mInflater=layout;
        mEvents=WhyApp.events;
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
    // In order to define the contents of the InfoWindow based on the marker
    @Override
    public View getInfoContents(Marker marker) {

        View view = mInflater.inflate(R.layout.info_window,null);
        double lat = marker.getPosition().latitude;
        double lon = marker.getPosition().longitude;
        Event event = new Event();

        //Check the correspondent event from mEvent
        for (int i=0; i<mEvents.size();i++){
            if (mEvents.get(i).getLatitude()==lat && mEvents.get(i).getLongitude()==lon){
                event=mEvents.get(i);
            }
        }

        //Populate fields
        //Event Date
        TextView eventOn=(TextView) view.findViewById(R.id.eventOn_tv);
        eventOn.setText("Event on " + formatEventData(event.getDate())); //Setting date as a string

        //Event Duration
        TextView eventDuration=(TextView) view.findViewById(R.id.setDuration_tv);
        eventDuration.setText(Double.toString(event.getDuration())); //Setting duration as a string

        //Heart Rate
        TextView heartRate=(TextView) view.findViewById(R.id.setHearRate_tv);
        heartRate.setText(Double.toString(event.getHearRate())); //Setting heartRate from double to string

        return view;
    }

    private String formatEventData (Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, d 'of' MMM, HH:mm", Locale.US);
        return dateFormat.format(date);
    }
}
