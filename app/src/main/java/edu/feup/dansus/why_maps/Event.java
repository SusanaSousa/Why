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
    Image photo;
    double latitude;
    double longitude;
    double timeRR;
    double bodyTemp;
    double duration;

    //Constructor

    public Event(){

    }
    public Event(User user, Date date, Image photo, double latitude, double longitude, double timeRR, double bodyTemp, double duration){
        this.user=user;
        this.date=date;
        this.photo=photo;
        this.latitude=latitude;
        this.longitude=longitude;
        this.timeRR=timeRR;
        this.bodyTemp=bodyTemp;
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
    public void setDate(Date date){
        this.date=date;
    }
    public Image getPhoto(){
        return this.photo;
    }
    public void setPhoto(Image img){
        this.photo=img;
    }
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
    public double getTimeRR(){
        return this.timeRR;
    }
    public void setTimeRR(double timeRR){
        this.timeRR=timeRR;
    }
    public double getBodyTemp(){
        return this.bodyTemp;
    }
    public void setBodyTemp(double temp){
        this.bodyTemp=temp;
    }
    public double getDuration(){
        return this.duration;
    }
    public void setDuration(double duration){
        this.duration=duration;
    }

}
