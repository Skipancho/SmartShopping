package com.smartshopping.main.review;

public class Review {
    private int rID;
    private String pCode;
    private int score;
    private String text;
    private String date;

    public Review(int rID, String pCode, int score, String text, String date) {
        this.rID = rID;
        this.pCode = pCode;
        this.score = score;
        this.text = text;
        this.date = date;
    }

    public int getrID() {
        return rID;
    }

    public void setrID(int rID) {
        this.rID = rID;
    }

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
