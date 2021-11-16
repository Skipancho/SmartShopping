package com.smartshopping.main.review;

import com.smartshopping.main.review.Review;

public class Review_Product extends Review {
    private String nickName;
    public Review_Product(int rID, String pCode, int score, String nickName, String text, String date) {
        super(rID, pCode, score, text, date);
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
