package edu.feup.dansus.why_maps;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Susana on 26/11/2017.
 */

public class WindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater mInflater;
    List<Event> mEvents;


    public WindowAdapter (LayoutInflater layout, List<Event> events){
        mInflater=layout;
        mEvents=events;

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
        TextView eventDate=(TextView) view.findViewById(R.id.eventDate_tv);
        eventDate.setText(formatEventData(event.getDate())); //Setting date as a string

        //Event Duration
        TextView eventDuration=(TextView) view.findViewById(R.id.setDuration_tv);
        eventDuration.setText(event.getDuration().toString()); //Setting duration as a string

        //Heart Rate
        TextView heartRate=(TextView) view.findViewById(R.id.setHearRate_tv);
        heartRate.setText(Double.toString(event.getTimeRR())); //Setting heartRate from double to string


        return view;
    }

    private String formatEventData (Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, d 'of' MMM, HH:mm", Locale.US);
        return dateFormat.format(date);
    }
}
