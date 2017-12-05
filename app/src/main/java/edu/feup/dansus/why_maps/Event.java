package edu.feup.dansus.why_maps;

import android.media.Image;

import java.util.Date;

/**
 * Created by Susana on 21/11/2017.
 */

public class Event {

    //Attributes of feup.sou.su.why.Event class that correspond to the database tab columns
    User user;
    long eventID;
    Date date;
    String photo_start_front; //Variables where the path of the images will be saved.
    String photo_start_rear;
    String notes;
    double latitude;
    double longitude;
    double hearRate;
    double duration;


    //Constructor

    public Event(){

    }
    public Event(User user, Date date, String photo_start_front, String photo_start_rear, String notes, double latitude, double longitude, double hearRate, double duration){
        this.user=user;
        this.date=date;
        this.photo_start_front=photo_start_front;
        this.photo_start_rear=photo_start_rear;
        this.notes=notes;
        this.latitude=latitude;
        this.longitude=longitude;
        this.hearRate=hearRate;
        this.duration=duration;
    }

    //All necessary getting and setting methods
    public User getUser(){return this.user;}
    public void setUser(User id){this.user=id;}
    public long getEventID(){return this.eventID;}
    public void setEventID(long id){this.eventID=id;}
    public Date getDate(){
        return this.date;
    }
    public void setDate(Date date){this.date=date;}
    public double getLatitude(){
        return this.latitude;
    }
    public void setLatitude(double latitude){
        this.latitude=latitude;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public void setLongitude(double longitude){
        this.longitude=longitude;
    }
    public double getHearRate(){
        return this.hearRate;
    }
    public void setHearRate(double hearRate){
        this.hearRate=hearRate;
    }
    public double getDuration(){
        return this.duration;
    }
    public void setDuration(double duration){
        this.duration=duration;
    }
    public String getPhotoStartFront(){return this.photo_start_front;}
    public String getPhotoStartRear(){return this.photo_start_rear;}
    public String getNotes(){return this.notes;}
    public void setNotes(String notes){this.notes=notes;}

}
