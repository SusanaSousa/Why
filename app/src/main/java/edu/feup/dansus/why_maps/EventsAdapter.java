package edu.feup.dansus.why_maps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<Event> mEvents = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView weekday;
        public TextView date;
        public TextView address;
        public TextView moreContextBt;
        public TextView addNotesBt;
        public ImageView photo;

        public ViewHolder(View itemView) {

            super(itemView);
            weekday = itemView.findViewById(R.id.weekday_tv);
            date = itemView.findViewById(R.id.date_tv);
            address = itemView.findViewById(R.id.address_tv);
            moreContextBt = itemView.findViewById(R.id.moreContext_bt);
            addNotesBt = itemView.findViewById(R.id.addNotes_bt);
            photo=itemView.findViewById(R.id.photo_imgv);

        }
    }


    public EventsAdapter(Context context) {
        this.mContext = context;
        this.mEvents = WhyApp.events;

    }



    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Inflate the costum layout
        View row = inflater.inflate(R.layout.event_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(row);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {

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
        String sPlace = new String();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getAddressLine(1);
            String country = addresses.get(0).getAddressLine(2);

            String[] splitAddress = address.split(",");
            sPlace = splitAddress[0] + "\n";
            if (city != null && !city.isEmpty()) {
                String[] splitCity = city.split(",");
                sPlace += splitCity[0];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sPlace;
    }
}
