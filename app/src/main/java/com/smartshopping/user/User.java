package com.smartshopping.user;

public class User {
    private String userID;
    private String userPW;
    private String phoneNum;
    private String name;
    private String nickName;
    private String pwAsk;
    private String pwAnswer;

    public User(){}

    public User(String userID, String phoneNum, String name, String nickName) {
        this.userID = userID;
        this.phoneNum = phoneNum;
        this.name = name;
        this.nickName = nickName;
        this.userPW = "";
        this.pwAsk = "";
        this.pwAnswer = "";
    }

    public User(String userID, String userPW, String phoneNum, String name, String nickName, String pwAsk, String pwAnswer) {
        this.userID = userID;
        this.userPW = userPW;
        this.phoneNum = phoneNum;
        this.name = name;
        this.nickName = nickName;
        this.pwAsk = pwAsk;
        this.pwAnswer = pwAnswer;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPW() {
        return userPW;
    }

    public void setUserPW(String userPW) {
        this.userPW = userPW;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPwAsk() {
        return pwAsk;
    }

    public void setPwAsk(String pwAsk) {
        this.pwAsk = pwAsk;
    }

    public String getPwAnswer() {
        return pwAnswer;
    }

    public void setPwAnswer(String pwAnswer) {
        this.pwAnswer = pwAnswer;
    }
}
