package edu.feup.dansus.why_maps;

import android.app.Application;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dany on 25-11-2017.
 * Application pattern. Class of a single instance across all activities and instantiated before
 * all of them.
 */

public class WhyApp extends Application {

    public static List<Event> events = new ArrayList<>(); // EventsFrag extracted from the DB

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
        sampleUser.userThreshold = 2.1;

        Event sampleEvent = new Event();
        Time time = new Time(20);
        Date date = new Date();
        sampleEvent.user=sampleUser;
        sampleEvent.duration = time;
        sampleEvent.bodyTemp = 37;
        sampleEvent.date = date;
        sampleEvent.latitude = 41.183208;
        sampleEvent.longitude = -8.583512;
        sampleEvent.timeRR = 5;

        db.addEvent(sampleEvent);

        // Create sample data
        User sampleUser2 = new User();
        sampleUser2.username = "Joanita";
        sampleUser2.userAge = 20;
        sampleUser2.userProfession = "Agricultora";
        sampleUser2.userThreshold = 2.1;

        Event sampleEvent2 = new Event();
        Time time2 = new Time(20);
        Date date2 = new Date();
        sampleEvent2.user=sampleUser2;
        sampleEvent2.duration = time2;
        sampleEvent2.bodyTemp = 38;
        sampleEvent2.date = date;
        sampleEvent2.latitude =41.177063;
        sampleEvent2.longitude = -8.594091;
        sampleEvent2.timeRR = 8;

        db.addEvent(sampleEvent2);

        // Create sample data

        Event sampleEvent3 = new Event();
        Time time3 = new Time(20);
        Date date3 = new Date();
        sampleEvent3.user=sampleUser;
        sampleEvent3.duration = time3;
        sampleEvent3.bodyTemp = 35;
        sampleEvent3.date = date;
        sampleEvent3.latitude = 41.176412;
        sampleEvent3.longitude = -8.603869;
        sampleEvent3.timeRR = 4;

        db.addEvent(sampleEvent3);

        events = db.getAllEvents();
    }
}
