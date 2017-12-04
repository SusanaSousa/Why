package edu.feup.dansus.why_maps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Susana on 30/11/2017.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsHolder> {
    private Context mContext;
    ArrayList<Event> mEvents = new ArrayList<>();
    public FragmentManager frag;
    public long eventID;



    public EventsAdapter(Context context, FragmentManager frag) {
        this.mContext = context;
        this.frag = frag;
        this.mEvents = WhyApp.events;


    }

    @Override
    public EventsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Inflate the costum layout
        View row = inflater.inflate(R.layout.event_item, parent, false);
        EventsHolder viewHolder = new EventsHolder(row,frag,eventID);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventsHolder holder, int position) {
        eventID=mEvents.get(position).getEventID();
        //Setting weekday
        SimpleDateFormat weekday = new SimpleDateFormat("EEEE", Locale.US); //TODO check if is just a E
        TextView weekday_tv = holder.weekday;
        weekday_tv.setText(weekday.format(mEvents.get(position).getDate()));

        //Setting Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("d 'of' MMMM yyyy ',' H:mm ", Locale.US); //TODO check if is just a E
        TextView date_tv = holder.date;
        date_tv.setText(dateFormat.format(mEvents.get(position).getDate()));

        //Setting Address
        TextView address_tv = holder.address;
        String add=getAddress(mEvents.get(position).getLatitude(), mEvents.get(position).getLongitude() );
        address_tv.setText(add);

        //Setting Image - let the image be the first photo taken
        //((ViewHolder)holder).photo

    }

    @Override
    public int getItemCount(){return mEvents.size();
    }
    private Context getContext() {
        return mContext;
    }


    private String getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(mContext, Locale.getDefault());
        String locationTxt = new String();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                locationTxt=address;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return locationTxt;
    }
}
