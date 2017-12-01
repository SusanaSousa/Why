package edu.feup.dansus.why_maps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

/**
 * Created by dany on 26-11-2017.
 */

public class EventsFrag extends Fragment {

    private RecyclerView mRecyclerView;
    private EventsAdapter mEventsAdapter;
    private ArrayList<Event> mEvents = new ArrayList<>(); // Array pointing to the global events array
    private WhyApp app;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        // Getting a reference to the global events list
        app = (WhyApp) getActivity().getApplicationContext();
        mEvents = app.events;

        mRecyclerView = (RecyclerView) view.findViewById(R.id.eventsView);
        mRecyclerView.setHasFixedSize(true); // while the items may change, the size will not
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //Setting the adapter
        mEventsAdapter = new EventsAdapter(getActivity().getApplicationContext());
        mRecyclerView.setAdapter(mEventsAdapter);

        return view;
    }
}
