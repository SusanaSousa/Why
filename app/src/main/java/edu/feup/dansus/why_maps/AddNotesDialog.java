package edu.feup.dansus.why_maps;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Susana on 01/12/2017.
 */

public class AddNotesDialog extends DialogFragment {
    private ArrayList<Event> mEvents = new ArrayList<>(); // Array pointing to the global events array
    private WhyApp app;
    public int adapterPos;
    private EditText mNotes;
    private DatabaseHandler dbHandler;

    public AddNotesDialog(){
        //Empty constructor
    }


    public static AddNotesDialog newInstance() {
        AddNotesDialog fragment = new AddNotesDialog();
        return fragment; // no frag arguments
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Getting a reference to the activity's DB Handler
        dbHandler = new DatabaseHandler(this.getActivity());

        // Getting a reference to the global events list
        app = (WhyApp) getActivity().getApplicationContext();
        mEvents = app.events;
        return inflater.inflate(R.layout.add_notes_dialog, container); // Inflating the layout

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Binding UI elements
        mNotes=(EditText) view.findViewById(R.id.notes);


        Button addBt=(Button) view.findViewById(R.id.add_bt);
        addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNotesToDB();
                getDialog().dismiss();
            }
        });


        Button cancelBt=(Button) view.findViewById(R.id.cancel_bt);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        // Show soft keyboard automatically and request focus to field
        mNotes.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    private void addNotesToDB() {
        String notes = mNotes.getText().toString();
        Event currEvent = mEvents.get(adapterPos);
        currEvent.notes=notes;

        dbHandler.updateEventNotes(currEvent);
        dbHandler.close();


    }
}
