package edu.feup.dansus.why_maps;

import android.app.Application;

/**
 * Created by dany on 25-11-2017.
 * Application pattern. Class of a single instance across all activities and instantiated before
 * all of them.
 */

public class WHYApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
