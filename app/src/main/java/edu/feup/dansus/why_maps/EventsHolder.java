package edu.feup.dansus.why_maps;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Susana on 01/12/2017.
 */

public class EventsHolder extends RecyclerView.ViewHolder {
    public TextView weekday;
    public TextView date;
    public TextView address;
    public TextView moreContextBt;
    public TextView addNotesBt;
    public ImageView photo;
    public long currentID;
    private FragmentManager frag;


    public EventsHolder(View itemView, FragmentManager frag, Long id) {

        super(itemView);
        weekday = itemView.findViewById(R.id.weekday_tv);
        date = itemView.findViewById(R.id.date_tv);
        address = itemView.findViewById(R.id.address_tv);
        moreContextBt = itemView.findViewById(R.id.moreContext_bt);
        addNotesBt = itemView.findViewById(R.id.addNotes_bt);
        photo=itemView.findViewById(R.id.photo_imgv);
        this.frag = frag;
        this.currentID=id+1;


        addNotesBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showAddNotesDialog(currentID);

            }
        });

        moreContextBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showMoreContextDialog(currentID);

            }
        });


    }

    private void showMoreContextDialog(long id) {
        MoreContextDialog dialog = MoreContextDialog.newInstance(id);
        dialog.show(frag,"more_context_dialog");
    }

    private void showAddNotesDialog(long id) {

        AddNotesDialog dialog = AddNotesDialog.newInstance(id);
        dialog.show(frag,"add_notes_dialog");

    }

}