package edu.feup.dansus.why_maps;

/**
 * Created by Susana on 21/11/2017.
 */

public class User {

    long userID;
    String username;
    String userProfession;
    int userAge;
    double userThreshold;

    public User(){}

    public User(long userID, String username, String userProfession, int userAge, double userThreshold){
        this.userID=userID;
        this.username=username;
        this.userProfession=userProfession;
        this.userAge=userAge;
        this.userThreshold=userThreshold;
    }

    //All necessary getting and setting methods
    public long getUserID(){
        return this.userID;
    }
    public void setUserID(long userID){
        this.userID=userID;
    }

    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public String getUserProfession(){
        return this.userProfession;
    }
    public void setUserProfession(String profession){
        this.userProfession=profession;
    }
    public int getUserAge(){
        return this.userAge;
    }
    public void setUserAge(int age){
        this.userAge=age;
    }
    public double getUserThreshold(){
        return this.userThreshold;
    }
    public void setUserThreshold(double tresh){
        this.userThreshold=tresh;
    }

}
