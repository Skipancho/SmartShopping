package com.smartshopping;

public class MyReview extends Review {
    private String pName;
    private int gap;

    public MyReview(int rID, String pCode, int score, String text, String date, String pName,int gap) {
        super(rID, pCode, score, text, date);
        this.pName = pName;
        this.gap = gap;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }
}
