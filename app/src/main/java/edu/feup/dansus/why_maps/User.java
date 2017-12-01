package edu.feup.dansus.why_maps;

/**
 * Created by Susana on 21/11/2017.
 */

public class User {
    String username;
    String userProfession;
    String userEmail;
    int userAge;
    int userThreshold;

    public User(){}

    public User(String username, String userProfession, String userEmail, int userAge, int userThreshold){
        this.username=username;
        this.userProfession=userProfession;
        this.userEmail=userEmail;
        this.userAge=userAge;
        this.userThreshold=userThreshold;
    }

    //All necessary getting and setting methods
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
    public String getUserEmail(){return this.userEmail;}
    public void setUserEmail(String email){this.userEmail=email;}
    public int getUserAge(){
        return this.userAge;
    }
    public void setUserAge(int age){
        this.userAge=age;
    }
    public int getUserThreshold(){
        return this.userThreshold;
    }
    public void setUserThreshold(int tresh){
        this.userThreshold=tresh;
    }

}
