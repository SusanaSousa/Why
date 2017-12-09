package edu.feup.dansus.why_maps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by Susana on 03/12/2017.
 */

public class MoreContextDialog extends DialogFragment {
    private ArrayList<Event> mEvents = new ArrayList<>(); // Array pointing to the global events array
    private long eventID;
    private WhyApp app;


    public MoreContextDialog(){
        //Empty constructor
    }


    public static MoreContextDialog newInstance(long eventID) {
        MoreContextDialog fragment = new MoreContextDialog();

        Bundle args = new Bundle();
        args.putLong("ID", eventID);
        fragment.setArguments(args);
        return fragment; // no frag arguments
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Getting a reference to the global events list
        app = (WhyApp) getActivity().getApplicationContext();
        mEvents = app.events;

        // Extracting fragment arguments
        Bundle args = getArguments();
        eventID = args.getLong("ID");

        return inflater.inflate(R.layout.more_context_dialog, container); // Inflating the layout

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Event current = new Event();

        for (Event ev: mEvents){
            if (ev.getEventID() == eventID){
                current = ev;
            }
        }

        //Setting weekday
        SimpleDateFormat wd = new SimpleDateFormat("EEEE", Locale.US);
        // Binding UI elements
        TextView weekday = view.findViewById(R.id.weekday_tv);
        weekday.setText(wd.format(current.getDate()));

        //Setting Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("d 'of' MMMM yyyy ',' H:mm ", Locale.US); //TODO check if is just a E
        TextView date = view.findViewById(R.id.eventDate_tv);
        date.setText(dateFormat.format(current.getDate()));

        //Setting duration
        TextView duration = view.findViewById((R.id.setDuration_tv));
        duration.setText(Double.toString(current.getDuration()));

        //Heart Rate
        TextView heartRate=(TextView) view.findViewById(R.id.setHearRate_tv);
        heartRate.setText(Integer.toString((int)current.getHearRate())); //Setting heartRate from double to string

        //Notes
        TextView notes=(TextView) view.findViewById(R.id.userNotestv);
        notes.setText(current.getNotes());

        // Setting photos
        ImageView imgFront = (ImageView) view.findViewById(R.id.front_start);
        ImageView imgRear = (ImageView) view.findViewById(R.id.rear_start);
        try {
            imgFront.setImageBitmap(RotateBitmap(BitmapFactory.decodeFile(current.getPhotoStartFront()), 180));
            imgRear.setImageBitmap(BitmapFactory.decodeFile(current.getPhotoStartRear()));
        }catch(Exception e){

        }

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


}