package com.develop.bean;

/**
 * Created by fySpring
 * Date : 2017/3/24
 * To do :
 */

public class StepEntity {

    private String curDate; //当天的日期
    private String steps;   //当天的步数
    private String userID;
    private String userName;   //当天的步数
    private String ImageURL;

    public StepEntity() {
    }

    public StepEntity(String curDate, String steps) {
        this.curDate = curDate;
        this.steps = steps;
    }

    public String getCurDate() {
        return curDate;
    }

    public void setCurDate(String curDate) {
        this.curDate = curDate;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "StepEntity{" +
                "curDate='" + curDate + '\'' +
                ", steps=" + steps +
                '}';
    }

}
