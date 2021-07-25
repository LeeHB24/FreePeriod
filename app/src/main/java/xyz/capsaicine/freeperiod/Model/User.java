package xyz.capsaicine.freeperiod.Model;

import java.util.ArrayList;

import xyz.capsaicine.freeperiod.app.Account;

public class User {
    public enum Auth{ Waiting, Accepted, Blocked };

    private int userId;
    private String userEmail;
    private String userPassword;
    private String userSchool;
    private String userName;
    private int userReportedCount;
    private float userRatingAverage;
    private ArrayList<Lecture> userLectureList;
    private Auth userAuth;

    public User(int id, String email, String password, String school, String name, int reportedCount, float ratingAverage, ArrayList<Lecture> lectureList, Auth auth){
        userId = id;
        userEmail = email;
        userPassword = password;
        userSchool = school;
        userName = name;
        userReportedCount = reportedCount;
        userRatingAverage = ratingAverage;
        userLectureList = lectureList;
        userAuth = auth;
    }
    public User(String email, String password, String school, String name){
        userPassword = password;
        userEmail = email;
        userSchool = school;
        userName = name;
        userReportedCount = 0;
        userRatingAverage = 0;
        userLectureList = new ArrayList<Lecture>();
        userAuth = Auth.Waiting;
    }


    public int getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserReportedCount() {
        return userReportedCount;
    }

    public float getUserRatingAverage() {
        return userRatingAverage;
    }

    public ArrayList<Lecture> getUserLectureList() {
        return userLectureList;
    }

    public Auth getUserAuth() {
        return userAuth;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserReportedCount(int userReportedCount) {
        this.userReportedCount = userReportedCount;
    }

    public void setUserRatingAverage(float userRatingAverage) {
        this.userRatingAverage = userRatingAverage;
    }

    public void setUserLectureList(ArrayList<Lecture> userLectureList) {
        this.userLectureList = userLectureList;
    }

    public void setUserAuth(Auth userAuth) {
        this.userAuth = userAuth;
    }
}
