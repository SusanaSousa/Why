package edu.feup.dansus.why_maps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Susana on 21/11/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    //Database information
    private static final String DATABASE_NAME = "EventsDatabase";
    private static final int DATABASE_VERSION = 1; //cada vez que a base de dados é actualizada é definida uma nova versão da base de dados

    //Tables name
    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_USERS = "users";

    //Table Users columns
    private static final String KEY_USER_ID = "userID";
    private static final String KEY_USER_NAME = "username";
    private static final String KEY_USER_PROF = "profession";
    private static final String KEY_USER_AGE = "age";
    private static final String KEY_USER_THRESH = "userthresh";

    //Table Events columns
    private static final String KEY_EVENT_USER_ID_FK = "userId";
    private static final String KEY_EVENT_ID = "eventID";
    private static final String KEY_EVENT_DATE = "date_time";
    //private static final String KEY_EVENT_IMAGE = "photo";
    private static final String KEY_EVENT_GPS_LAT = "gps_lat";
    private static final String KEY_EVENT_GPS_LONG = "gps_long";
    private static final String KEY_EVENT_RR = "RR_time";
    private static final String KEY_EVENT_TEMP = "body_temperature";
    private static final String KEY_EVENT_DUR = "event_duration";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    // Called when the database is created for the FIRST time.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_PROF + " TEXT," +
                KEY_USER_AGE + " INTEGER," +
                KEY_USER_THRESH + " REAL" +
                ")";


        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS +
                "(" +
                KEY_EVENT_ID + " INTEGER PRIMARY KEY," +
                KEY_EVENT_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_EVENT_DATE + " TEXT," +
                //KEY_EVENT_IMAGE + "TEXT," +
                KEY_EVENT_GPS_LAT + " REAL," +
                KEY_EVENT_GPS_LONG + " REAL," +
                KEY_EVENT_RR + " REAL," +
                KEY_EVENT_TEMP + " REAL," +
                KEY_EVENT_DUR + " TEXT" +
                ")";
        sqLiteDatabase.execSQL(CREATE_EVENTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_USERS_TABLE);
    }

    //Called when the database needs to be updated
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(sqLiteDatabase);
        }
    }
    //CRUD Operations (Create, Read, Update, Delete)
    //CREATE EVENT
    public void addEvent (Event event){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple events).
            long userId = addOrUpdateUser(event.user);
            ContentValues values = new ContentValues();
            values.put(KEY_EVENT_USER_ID_FK, userId);
            //values.put(KEY_EVENT_IMAGE, event.photo);
            values.put(KEY_EVENT_DATE, event.date.toString());
            values.put(KEY_EVENT_GPS_LAT , event.latitude);
            values.put(KEY_EVENT_GPS_LONG, event.longitude);
            values.put(KEY_EVENT_RR, event.timeRR);
            values.put(KEY_EVENT_TEMP, event.bodyTemp);
            values.put(KEY_EVENT_DUR, event.duration.toString());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            sqLiteDatabase.insertOrThrow(TABLE_EVENTS, null, values);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            sqLiteDatabase.endTransaction();
        }

    }

    // Adding new user
    void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getUserID());
        values.put(KEY_USER_NAME, user.getUsername());
        values.put(KEY_USER_AGE, user.getUserAge());
        values.put(KEY_USER_PROF, user.getUserProfession());
        values.put(KEY_USER_THRESH, user.getUserThreshold());

        // Inserting Row
        try {
            db.insert(TABLE_USERS, null, values);
            db.close(); // Closing database connection

        } catch (Exception e) {

            Log.d(TAG, "Error adding just a user");


        }
    }

    public long addOrUpdateUser(User user) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long userId = -1;
        sqLiteDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, user.getUsername());
            values.put(KEY_USER_PROF, user.getUserProfession());
            values.put(KEY_USER_AGE, user.getUserAge());
            values.put(KEY_USER_THRESH, user.getUserThreshold());

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            //The update function returns the number of rows affected by the update
            int rows = sqLiteDatabase.update(TABLE_USERS, values, KEY_USER_NAME + "= ?", new String[]{user.username});
            // Check if update succeeded
            if (rows >= 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_USER_ID, TABLE_USERS, KEY_USER_NAME);
                Cursor cursor = sqLiteDatabase.rawQuery(usersSelectQuery, new String[]{String.valueOf(user.username)}); // Cursor is an object that can iterate on the result rows of your query.
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0); //returns the value of the first columns (IDcolumn) as a int
                        sqLiteDatabase.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                userId = sqLiteDatabase.insertOrThrow(TABLE_USERS, null, values);
                sqLiteDatabase.setTransactionSuccessful();
            }
        } catch (Exception e) {

            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return userId;
    }
    // Getting All Contacts
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();

        // SELECT * FROM EVENTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
        String EVENTS_SELECT_QUERY =
                String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                        TABLE_EVENTS,
                        TABLE_USERS,
                        TABLE_EVENTS, KEY_EVENT_USER_ID_FK,
                        TABLE_USERS, KEY_USER_ID);

        //String selectQuery = "SELECT  * FROM " + TABLE_USERS;
        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
         SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(EVENTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    User newUser = new User();
                    newUser.username = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME));
                    newUser.userProfession = cursor.getString(cursor.getColumnIndex(KEY_USER_PROF));
                    newUser.userAge = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_USER_AGE)));
                    newUser.userThreshold = Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_USER_THRESH)));
                    newUser.userID=Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));

                    Event newEvent = new Event();
                    newEvent.eventID=Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_EVENT_ID)));
                    newEvent.duration= Time.valueOf(cursor.getString(cursor.getColumnIndex(KEY_EVENT_DUR)));
                    newEvent.bodyTemp=Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_EVENT_TEMP)));
                   // newEvent.date=(cursor.getString(cursor.getColumnIndex(KEY_EVENT_DATE)));
                    newEvent.latitude=Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_EVENT_GPS_LAT)));
                    newEvent.longitude=Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_EVENT_GPS_LONG)));
                    newEvent.timeRR = Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_EVENT_RR)));
                    newEvent.user=newUser;
                    events.add(newEvent);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return events;
    }
}
