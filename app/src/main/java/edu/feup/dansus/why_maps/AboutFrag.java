package edu.feup.dansus.why_maps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by dany on 26-11-2017.
 */

public class AboutFrag extends DialogFragment {
    private TextView versionLabel;

    public AboutFrag() {
        // Required empty public constructor
    }

    public static AboutFrag newInstance() {
        AboutFrag fragment = new AboutFrag();
        return fragment; // no frag arguments
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_dialog, container, false); // Inflating the layout
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Binding UI elements
        versionLabel = view.findViewById(R.id.versionLabel);

        // Getting application version
        String versionName = BuildConfig.VERSION_NAME; // defined in build.gradle

        // Setting text
        versionLabel.setText("Version " + versionName);



    }
}