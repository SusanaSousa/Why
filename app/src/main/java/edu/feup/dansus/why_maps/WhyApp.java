package edu.feup.dansus.why_maps;

import android.app.Application;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dany on 25-11-2017.
 * Application pattern. Class of a single instance across all activities and instantiated before
 * all of them.
 */

public class WhyApp extends Application {

    public static ArrayList<Event> events = new ArrayList<>(); // EventsFrag extracted from the DB
    public static final String VJ_ADDRESS = "00:23:FE:00:0B:54";
    public static User currentUser;

    @Override
    public void onCreate() {

        super.onCreate();

        //Importing from the DB
        DatabaseHandler db = new DatabaseHandler(this);

        // Artificial data creation
        User sampleUser = new User();
        sampleUser.username = "Susana";
        sampleUser.userAge = 22;
        sampleUser.userProfession = "Engenheira";
        sampleUser.userThreshold = 170;

        currentUser = sampleUser;

        Event sampleEvent = new Event();
        Double time = (double) 4;
        Date date = new Date();
        sampleEvent.user=sampleUser;
        sampleEvent.duration = time;
        sampleEvent.date = date;
        sampleEvent.latitude = 41.183208;
        sampleEvent.longitude = -8.583512;
        sampleEvent.hearRate = 5;

        db.addEvent(sampleEvent);


        Event sampleEvent2 = new Event();
        Double time2 = (double) 2;
        Date date2 = new Date();
        sampleEvent2.user=sampleUser;
        sampleEvent2.duration = time2;
        sampleEvent2.date = date;
        sampleEvent2.latitude =41.177063;
        sampleEvent2.longitude = -8.594091;
        sampleEvent2.hearRate = 8;

        db.addEvent(sampleEvent2);

        // Create sample data
        /*Event sampleEvent3 = new Event();
        Double time3 = (double) 20;
        Date date3 = new Date();
        sampleEvent3.user=sampleUser;
        sampleEvent3.duration = time3;
        sampleEvent3.bodyTemp = 35;
        sampleEvent3.date = date;
        sampleEvent3.latitude = 41.176412;
        sampleEvent3.longitude = -8.603869;
        sampleEvent3.timeRR = 4;

        db.addEvent(sampleEvent3);*/

        events = db.getAllEvents();
        db.close();
    }
}
